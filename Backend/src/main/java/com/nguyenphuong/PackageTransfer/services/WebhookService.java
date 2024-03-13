package com.nguyenphuong.PackageTransfer.services;

import com.nguyenphuong.PackageTransfer.config.exceptions.BadRequestException;
import com.nguyenphuong.PackageTransfer.config.exceptions.RecordNotFoundException;
import com.nguyenphuong.PackageTransfer.enums.WebhookStatus;
import com.nguyenphuong.PackageTransfer.modals.entities.WebhookEntity;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.WebhookSearchCriteria;
import com.nguyenphuong.PackageTransfer.repositories.WebhookRepository;
import com.nguyenphuong.PackageTransfer.utils.CryptoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class WebhookService {
  @Autowired
  private WebhookRepository webhookRepository;

  public Page<WebhookEntity> getAll(WebhookSearchCriteria searchCriteria, Pageable pageable) {
    return webhookRepository.findAll(searchCriteria.toSpecification(), pageable);
  }

  public WebhookEntity getOneByID(String id, UserDetails userDetails) {
    WebhookEntity webhookEntity = getOneNullable(id);
    if (webhookEntity == null || !webhookEntity.getCreatedBy().equals(userDetails.getUsername())) {
      throw new RecordNotFoundException("Can't find webhook with id " + id);
    }
    switch (webhookEntity.getSecurityType()) {
      case BASIC:
        webhookEntity.setBasicAuthPassword(CryptoUtils.decrypt(webhookEntity.getBasicAuthPassword()));
        break;
      case API_KEY:
        webhookEntity.setApiKeySecret(CryptoUtils.decrypt(webhookEntity.getApiKeySecret()));
        break;
      case OAUTH2:
        webhookEntity.setOauth2ClientSecret(CryptoUtils.decrypt(webhookEntity.getOauth2ClientSecret()));
        break;
    }
    return webhookEntity;
  }

  public WebhookEntity create(WebhookEntity webhookEntity) {
    if (StringUtils.isBlank(webhookEntity.getCallbackURL())) {
      throw new BadRequestException("Callback URL can't be blank");
    }
    if (StringUtils.isBlank(webhookEntity.getTopic())) {
      throw new BadRequestException("Topic can't be blank");
    }
    webhookEntity.setStatus(WebhookStatus.ACTIVE);
    webhookEntity.setDeletedAt(null);
    validateWebhookSecurityFields(webhookEntity);
    return webhookRepository.save(webhookEntity);
  }

  public WebhookEntity update(String id, WebhookEntity updateWebhookEntity, UserDetails userDetails) {
    if (StringUtils.isBlank(updateWebhookEntity.getCallbackURL())) {
      throw new BadRequestException("Callback URL can't be blank");
    }
    if (StringUtils.isBlank(updateWebhookEntity.getTopic())) {
      throw new BadRequestException("Topic can't be blank");
    }
    WebhookEntity webhookEntity = getOneByID(id, userDetails);
    if (webhookEntity.getDeletedAt() != null) {
      throw new BadRequestException("This Webhook is deleted");
    }
    updateWebhookEntity.setDeletedAt(null);
    validateWebhookSecurityFields(updateWebhookEntity);
    BeanUtils.copyProperties(updateWebhookEntity, webhookEntity);
    webhookEntity = webhookRepository.save(updateWebhookEntity);
    return webhookEntity;
  }

  public void delete(String id, Boolean isSoftDelete, UserDetails userDetails) {
    WebhookEntity webhookEntity = getOneByID(id, userDetails);
    if (isSoftDelete) {
      webhookEntity.setDeletedAt(Instant.now());
      webhookRepository.save(webhookEntity);
    } else {
      webhookRepository.delete(webhookEntity);
    }
  }

  private WebhookEntity getOneNullable(String uuid) {
    return webhookRepository.findById(uuid).orElse(null);
  }

  private void validateWebhookSecurityFields(WebhookEntity webhookEntity) {
    if (!UrlValidator.getInstance().isValid(webhookEntity.getCallbackURL())) {
      throw new BadRequestException("The callback URL is not valid");
    }
    switch (webhookEntity.getSecurityType()) {
      case BASIC:
        if (StringUtils.isBlank(webhookEntity.getBasicAuthUserName())) {
          throw new BadRequestException("Please provide Username.");
        }
        if (StringUtils.isBlank(webhookEntity.getBasicAuthPassword())) {
          throw new BadRequestException("Please provide Password.");
        } else {
          webhookEntity.setBasicAuthPassword(CryptoUtils.encrypt(webhookEntity.getBasicAuthPassword()));
        }
        break;
      case API_KEY:
        if (StringUtils.isBlank(webhookEntity.getApiKeySecret())) {
          throw new BadRequestException("Please provide Secret.");
        } else {
          webhookEntity.setApiKeySecret(CryptoUtils.encrypt(webhookEntity.getApiKeySecret()));
        }
        break;
      case OAUTH2:
        if (StringUtils.isBlank(webhookEntity.getOauth2ClientId())) {
          throw new BadRequestException("Please provide Client ID.");
        }
        if (StringUtils.isBlank(webhookEntity.getOauth2ClientSecret())) {
          throw new BadRequestException("Please provide Client Secret.");
        } else {
          webhookEntity.setOauth2ClientSecret(CryptoUtils.encrypt(webhookEntity.getOauth2ClientSecret()));
        }
        if (StringUtils.isBlank(webhookEntity.getOauth2AccessTokenUrl())) {
          throw new BadRequestException("Please provide Token URL.");
        } else if (!UrlValidator.getInstance().isValid(webhookEntity.getOauth2AccessTokenUrl())) {
          throw new BadRequestException("The token URL is not valid");
        }
        break;
    }
  }
}
