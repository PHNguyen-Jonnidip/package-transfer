package com.nguyenphuong.PackageTransfer.services;

import com.nguyenphuong.PackageTransfer.config.exceptions.BadRequestException;
import com.nguyenphuong.PackageTransfer.config.exceptions.RecordNotFoundException;
import com.nguyenphuong.PackageTransfer.enums.IncomingPackageStatus;
import com.nguyenphuong.PackageTransfer.enums.OutgoingPackageStatus;
import com.nguyenphuong.PackageTransfer.mapper.IncomingPackageDetailMapper;
import com.nguyenphuong.PackageTransfer.modals.dto.IncomingPackageDetail;
import com.nguyenphuong.PackageTransfer.modals.entities.IncomingPackageEntity;
import com.nguyenphuong.PackageTransfer.modals.entities.OutgoingPackageEntity;
import com.nguyenphuong.PackageTransfer.modals.entities.WebhookEntity;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.IncomingPackageSearchCriteria;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.WebhookSearchCriteria;
import com.nguyenphuong.PackageTransfer.repositories.IncomingPackageRepository;
import com.nguyenphuong.PackageTransfer.repositories.OutgoingPackageRepository;
import com.nguyenphuong.PackageTransfer.repositories.WebhookRepository;
import com.nguyenphuong.PackageTransfer.utils.CryptoUtils;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class IncomingPackageService {
  private final static Logger LOGGER = LoggerFactory.getLogger(IncomingPackageService.class);

  @Autowired
  private IncomingPackageRepository incomingPackageRepository;

  @Autowired
  private OutgoingPackageRepository outgoingPackageRepository;

  @Autowired
  private WebhookRepository webhookRepository;

  @Autowired
  private ExecutorService executorService;

  @Autowired
  private IncomingPackageDetailMapper incomingPackageDetailMapper;

  @Autowired
  private RedisJobQueueService redisJobQueueService;

  private static final String[] SUPPORTED_HTTP_METHOD = new String[]{"POST", "PUT", "DELETE"};

  public Page<IncomingPackageEntity> getAll(IncomingPackageSearchCriteria searchCriteria, Pageable pageable) {
    return incomingPackageRepository.findAll(searchCriteria.toSpecification(), pageable);
  }

  public IncomingPackageDetail getOneByID(String uuid, UserDetails userDetails) {
    IncomingPackageEntity incomingPackageEntity = getOneNullable(uuid);
    if (incomingPackageEntity == null || !incomingPackageEntity.getCreatedBy().equals(userDetails.getUsername())) {
      throw new RecordNotFoundException("Can't find incoming package having id: " + uuid);
    }
    return incomingPackageDetailMapper.toDetailModel(incomingPackageEntity);
  }

  public IncomingPackageEntity create(IncomingPackageEntity createIncoming) {
    createIncoming.setStatus(IncomingPackageStatus.PENDING);
    try {
      JSONParser parser = new JSONParser();
      parser.parse(createIncoming.getPayload());
    } catch (ParseException ex) {
      throw new BadRequestException("The payload is not JSON");
    }
    if (createIncoming.getTargetTopics().isEmpty()) {
      throw new BadRequestException("Can't create incoming without any target topics");
    }
    if (StringUtils.isBlank(createIncoming.getMethod())) {
      createIncoming.setMethod(HttpMethod.POST.toString());
    } else if (!Arrays.asList(SUPPORTED_HTTP_METHOD).contains(createIncoming.getMethod())) {
      throw new BadRequestException("We only support for " + String.join(", ", SUPPORTED_HTTP_METHOD) + " method.");
    }
    createIncoming.setPayload(CryptoUtils.encrypt(createIncoming.getPayload()));
    IncomingPackageEntity incomingPackageEntity = incomingPackageRepository.save(createIncoming);
    executorService.execute(() -> {
      consume(incomingPackageEntity);
    });
    return incomingPackageEntity;
  }

  private IncomingPackageEntity getOneNullable(String uuid) {
    return incomingPackageRepository.findById(uuid).orElse(null);
  }

  private void consume(IncomingPackageEntity incomingPackageEntity) {
    LOGGER.info("Start consuming incoming package");
    WebhookSearchCriteria webhookSearchCriteria = new WebhookSearchCriteria();
    webhookSearchCriteria.setTargetTopics(incomingPackageEntity.getTargetTopics());
    webhookSearchCriteria.setIsAvailable(true);
    List<WebhookEntity> webhookEntityList = webhookRepository.findAll(webhookSearchCriteria.toSpecification());
    this.process(incomingPackageEntity, webhookEntityList);
  }

  private List<OutgoingPackageEntity> process(IncomingPackageEntity incomingPackageEntity, List<WebhookEntity> webhookEntityList) {
    MDC.put("INCOMING_PACKAGE_ID", incomingPackageEntity.getId());
    List<OutgoingPackageEntity> outgoingPackageEntities = new ArrayList<>();
    webhookEntityList.forEach(webhook -> {
      OutgoingPackageEntity outgoingPackageEntity = new OutgoingPackageEntity();
      outgoingPackageEntity.setIncomingPackageEntity(incomingPackageEntity);
      outgoingPackageEntity.setWebhookEntity(webhook);
      outgoingPackageEntity.setStatus(OutgoingPackageStatus.PENDING);
      outgoingPackageEntities.add(outgoingPackageRepository.save(outgoingPackageEntity));
    });
    LOGGER.info("Outgoing packages {} are created from incoming package {}", outgoingPackageEntities, incomingPackageEntity.getId());
    incomingPackageEntity.setStatus(IncomingPackageStatus.DONE);
    LOGGER.info("Complete consuming incoming package");
    incomingPackageRepository.save(incomingPackageEntity);
    List<OutgoingPackageEntity> savedOutgoingPackageEntities = outgoingPackageRepository.saveAll(outgoingPackageEntities);

    // Add to redis queue
    redisJobQueueService.addAll(savedOutgoingPackageEntities);
    return savedOutgoingPackageEntities;
  }

  @PreDestroy
  private void preDestroy() {
    executorService.shutdown();
  }
}
