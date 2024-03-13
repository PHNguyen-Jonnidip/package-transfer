package com.nguyenphuong.PackageTransfer.services;

import brave.Span;
import brave.Tracer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyenphuong.PackageTransfer.enums.OutgoingPackageStatus;
import com.nguyenphuong.PackageTransfer.enums.WebhookSecurityType;
import com.nguyenphuong.PackageTransfer.modals.entities.HttpResponseLogEntity;
import com.nguyenphuong.PackageTransfer.modals.entities.IncomingPackageEntity;
import com.nguyenphuong.PackageTransfer.modals.entities.OutgoingPackageEntity;
import com.nguyenphuong.PackageTransfer.modals.entities.WebhookEntity;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.OutgoingPackageSearchCriteria;
import com.nguyenphuong.PackageTransfer.repositories.HttpResponseLogRepository;
import com.nguyenphuong.PackageTransfer.repositories.OutgoingPackageRepository;
import com.nguyenphuong.PackageTransfer.utils.CryptoUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class DeliveryOutgoingPackageService {
  private final static Logger LOGGER = LoggerFactory.getLogger(DeliveryOutgoingPackageService.class);

  @Value("${delivery.number-thread-ratio}")
  private int numThreadRatio;

  @Value("${spring.datasource.hikari.maximum-pool-size}")
  private int databaseMaxPoolSize;

  @Value("${delivery.running-mode}")
  private String runningMode;

  @Autowired
  private OutgoingPackageRepository outgoingPackageRepository;

  @Autowired
  private HttpResponseLogRepository httpResponseLogRepository;

  @Autowired
  private Tracer tracer;

  @Autowired
  private RedisJobQueueService redisJobQueueService;

  private ExecutorService executorService;

  public OutgoingPackageEntity delivery(String uuid) {
    OutgoingPackageEntity outgoingPackageEntity = outgoingPackageRepository.findById(uuid).orElse(null);
    if (outgoingPackageEntity == null) {
      LOGGER.error("Can't find any outgoing package with id {}", uuid);
      return null;
    }
    LOGGER.info("Marking outgoing package {} to in progress", uuid);
    outgoingPackageEntity = markInProgress(outgoingPackageEntity);
    MDC.put("OUTGOING_PACKAGE_ID", uuid);

    // Step 2: Execute call back
    LOGGER.info("Processing outgoing payload {} status", uuid);
    processDelivery(outgoingPackageEntity);

    // Step 3: Save result
    LOGGER.info("Updating outgoing payload {} status", uuid);
    updateDeliveryStatus(outgoingPackageEntity);
    return outgoingPackageEntity;
  }

  private OutgoingPackageEntity markInProgress(OutgoingPackageEntity outgoingPackageEntity) {
    outgoingPackageEntity.setStatus(OutgoingPackageStatus.DELIVERING);
    outgoingPackageEntity.setDeliveryStartTimeStamp(Instant.now());
    outgoingPackageEntity = outgoingPackageRepository.save(outgoingPackageEntity);
    LOGGER.info("Marked outgoing package {} as IN_PROGRESS", outgoingPackageEntity.getId());
    return outgoingPackageEntity;
  }

  private void processDelivery(OutgoingPackageEntity outgoingPackageEntity) {
    WebhookEntity webhookEntity = outgoingPackageEntity.getWebhookEntity();
    if (webhookEntity.getDeletedAt() != null) {
      LOGGER.warn("The webhook is deleted");
      outgoingPackageEntity.setStatus(OutgoingPackageStatus.FAILED);
      return;
    }
    ResponseEntity<String> response = executeHTTPCall(outgoingPackageEntity);
    if (response.getStatusCode().is2xxSuccessful()) {
      LOGGER.info("Marking Outgoing Payload {} status to DONE", outgoingPackageEntity.getId());
      outgoingPackageEntity.setStatus(OutgoingPackageStatus.DONE);
    } else {
      if (outgoingPackageEntity.isArchivable()) {
        outgoingPackageEntity.setStatus(OutgoingPackageStatus.ARCHIVED);
      } else {
        outgoingPackageEntity.setStatus(OutgoingPackageStatus.FAILED);
      }
      LOGGER.info("Fail to deliver Payload to {} response_code: {}  response_body: {}",
          webhookEntity.getCallbackURL(), response.getStatusCode(), response.getBody());
    }
  }

  private void updateDeliveryStatus(OutgoingPackageEntity outgoingPackageEntity) {
    Instant completeTime = Instant.now();
    if (outgoingPackageEntity.getStatus().equals(OutgoingPackageStatus.FAILED)) {
      LOGGER.info("FAILED to delivery outgoing package having id {}", outgoingPackageEntity.getId());
      outgoingPackageEntity.setLastFailedDeliveryTimeStamp(completeTime);
      if (outgoingPackageEntity.getFirstFailedDeliveryTimeStamp() == null) {
        outgoingPackageEntity.setFirstFailedDeliveryTimeStamp(completeTime);
      } else {
        outgoingPackageEntity.increaseNumberOfRetry();
      }
    }
    outgoingPackageEntity.setDeliveryEndTimeStamp(completeTime);
    outgoingPackageEntity = outgoingPackageRepository.save(outgoingPackageEntity);
    LOGGER.info("Complete delivery outgoing package with id {}", outgoingPackageEntity.getId());
    if (outgoingPackageEntity.getStatus().equals(OutgoingPackageStatus.FAILED)) {
      redisJobQueueService.addOutgoingToQueue(outgoingPackageEntity.getNextRetryInstant(), outgoingPackageEntity.getId());
    }
  }

  private ResponseEntity<String> executeHTTPCall(OutgoingPackageEntity outgoingPackageEntity) {
    WebhookEntity webhookEntity = outgoingPackageEntity.getWebhookEntity();
    IncomingPackageEntity incomingPackageEntity = outgoingPackageEntity.getIncomingPackageEntity();
    LOGGER.info("Executing HTTP call calling: {} ", incomingPackageEntity.getPayload());

    SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
    clientHttpRequestFactory.setConnectTimeout(60 * 1000);
    clientHttpRequestFactory.setReadTimeout(60 * 1000);
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Payload-Submitted-At", DateTimeFormatter.ISO_INSTANT.format(incomingPackageEntity.getCreatedAt()));
    headers.set("X-Payload-Id", incomingPackageEntity.getId());
    headers.setContentType(MediaType.APPLICATION_JSON);
    RestTemplate restTemplate;
    if (webhookEntity.getSecurityType().equals(WebhookSecurityType.OAUTH2)) {
      ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
      String decryptClientSecret = CryptoUtils.decrypt(webhookEntity.getOauth2ClientSecret());
      resourceDetails.setClientId(webhookEntity.getOauth2ClientId());
      resourceDetails.setClientSecret(decryptClientSecret);
      resourceDetails.setAccessTokenUri(webhookEntity.getOauth2AccessTokenUrl());
      resourceDetails.setGrantType("client_credentials");
//      if (StringUtils.isNotBlank(webhookEntity.getOauth2Scope())) {
//        resourceDetails.setScope(List.of(webhookEntity.getOauth2Scope()));
//      } else {
//        resourceDetails.setScope(List.of("read", "write"));
//      }
      restTemplate = new OAuth2RestTemplate(resourceDetails);
      restTemplate.setRequestFactory(clientHttpRequestFactory);
    } else {
      restTemplate = new RestTemplate(clientHttpRequestFactory);
      addAuthenticationHeader(restTemplate, headers, webhookEntity);
    }
    HttpEntity<String> entity = new HttpEntity<>(CryptoUtils.decrypt(incomingPackageEntity.getPayload()), headers);
    ResponseEntity<String> responseEntity;
    try {
      responseEntity = restTemplate.exchange(
          webhookEntity.getCallbackURL(),
          HttpMethod.valueOf(incomingPackageEntity.getMethod()),
          entity,
          String.class
      );
    } catch (RestClientException ex) {
      String errorMessage = ex.getMessage();
      int failureStatus = this.extractErrorCode(StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : "");
      responseEntity = new ResponseEntity<>(errorMessage, HttpStatus.valueOf(failureStatus));
    } catch (OAuth2AccessDeniedException e) {
      responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    extractToHttpResponseLog(responseEntity, outgoingPackageEntity, headers);
    return responseEntity;
  }

  private void addAuthenticationHeader(RestTemplate restTemplate, HttpHeaders headers, WebhookEntity webhookEntity) {
    switch (webhookEntity.getSecurityType()) {
      case BASIC:
        restTemplate.getInterceptors().add(
            new BasicAuthenticationInterceptor(webhookEntity.getBasicAuthUserName(), CryptoUtils.decrypt(webhookEntity.getBasicAuthPassword())));
        break;
      case API_KEY:
        String decryptToken = CryptoUtils.decrypt(webhookEntity.getApiKeySecret());
        String customTokenPrefix = Optional.ofNullable(webhookEntity.getApiKeyPrefix()).orElse("Bearer");
        String token = customTokenPrefix + " " + decryptToken;
        String customSecurityHeader = Optional.ofNullable(webhookEntity.getApiKeyCustomHeader()).orElse("Authorization");
        headers.set(customSecurityHeader, token);
        break;
    }
  }

  private int extractErrorCode(String errorMessage) {
    if (errorMessage.contains("I/O")) {
      return 500;
    }
    HttpStatus[] httpStatuses = HttpStatus.values();
    HttpStatus httpStatus = Arrays.stream(httpStatuses)
        .filter(status -> errorMessage.toUpperCase().contains(status.getReasonPhrase().toUpperCase()) || errorMessage.contains(String.valueOf(status.value())))
        .findAny()
        .orElse(HttpStatus.BAD_REQUEST);
    return httpStatus.value();
  }

  @Transactional
  void extractToHttpResponseLog(ResponseEntity<String> responseEntity, OutgoingPackageEntity outgoingPackageEntity, HttpHeaders headers) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      HttpResponseLogEntity httpResponseLogEntity = new HttpResponseLogEntity();
      httpResponseLogEntity.setOutgoingPackageEntity(outgoingPackageEntity);
      httpResponseLogEntity.setCallbackUrl(outgoingPackageEntity.getWebhookEntity().getCallbackURL());
      httpResponseLogEntity.setRequestHeaders(CryptoUtils.encrypt(objectMapper.writeValueAsString(headers)));
      httpResponseLogEntity.setRequestBody(outgoingPackageEntity.getIncomingPackageEntity().getPayload());
      httpResponseLogEntity.setResponseCode(responseEntity.getStatusCode().toString());
      httpResponseLogEntity.setResponseHeaders(objectMapper.writeValueAsString(responseEntity.getHeaders().toString()));
      if (Objects.nonNull(responseEntity.getBody())) {
        httpResponseLogEntity.setResponseBody(responseEntity.getBody());
      }

      if (tracer != null) {
        Span span = tracer.currentSpan();
        if (span != null) {
          httpResponseLogEntity.setCorrelationId(span.context().traceIdString());
        }
      }
      httpResponseLogRepository.save(httpResponseLogEntity);
    } catch (Exception ex) {
      LOGGER.error("Failed to create response log for ResponseEntity {}", responseEntity, ex);
    }
  }

  @PostConstruct
  private void postConstruct() {
    if (runningMode.equals("WORKER") || runningMode.equals("STAND_ALONE")) {
      int nThreads = databaseMaxPoolSize * numThreadRatio;
      executorService = Executors.newFixedThreadPool(nThreads);
      executorService.execute(() -> {
        LOGGER.info("Start resume delivery IN_PROGRESS Outgoing Packages");
        OutgoingPackageSearchCriteria searchCriteria = new OutgoingPackageSearchCriteria();
        searchCriteria.setStatus(OutgoingPackageStatus.DELIVERING);
        List<OutgoingPackageEntity> outgoings = outgoingPackageRepository.findAll(searchCriteria.toSpecification());
        outgoings.forEach(outgoing -> delivery(outgoing.getId()));
      });
      ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

      LOGGER.info("Start polling the messages");
      scheduledExecutorService.scheduleAtFixedRate(() -> {
        String outgoingPayloadId = redisJobQueueService.pollFirst();
        if (null != outgoingPayloadId) {
          executorService.execute(() -> {
            try {
              delivery(outgoingPayloadId);
            } catch (Exception e) {
              LOGGER.error("Error in OutgoingPayload delivery", e);
            }
          });
        }
      }, 0, 5, TimeUnit.MILLISECONDS);
    }
  }

  @PreDestroy
  private void preDestroy() {
    LOGGER.info("Shutting down the Delivery Service");
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        LOGGER.warn("Shutdown the executor service forcefully");
        executorService.shutdownNow();
      }
    } catch (InterruptedException e) {
      executorService.shutdownNow();
    }
  }
}
