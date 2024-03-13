package com.nguyenphuong.PackageTransfer.modals.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nguyenphuong.PackageTransfer.enums.WebhookSecurityType;
import com.nguyenphuong.PackageTransfer.enums.WebhookStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "webhooks", indexes = {
    @Index(name = "index_webhooks_topic", columnList = "topic")
})
public class WebhookEntity extends AbstractEntity {
  @Column(name = "callback_url", nullable = false)
  @JsonProperty("callback_url")
  private String callbackURL;

  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private WebhookStatus status;

  @Column(name = "security_type", nullable = false)
  @JsonProperty("security_type")
  @Enumerated(EnumType.STRING)
  private WebhookSecurityType securityType;

  @Column(name = "topic", nullable = false)
  @JsonProperty("topic")
  private String topic;

  @Column(name = "basic_auth_username")
  @JsonProperty("basic_auth_username")
  private String basicAuthUserName;

  @Column(name = "basic_auth_password")
  @JsonProperty("basic_auth_password")
  private String basicAuthPassword;

  @Column(name = "api_key_custom_header")
  @JsonProperty("api_key_custom_header")
  private String apiKeyCustomHeader;

  @Column(name = "api_key_prefix")
  @JsonProperty("api_key_prefix")
  private String apiKeyPrefix;

  @Column(name = "api_key_secret")
  @JsonProperty("api_key_secret")
  private String apiKeySecret;

  @Column(name = "oauth2_access_token_url")
  @JsonProperty("oauth2_access_token_url")
  private String oauth2AccessTokenUrl;

  @Column(name = "oauth2_client_id")
  @JsonProperty("oauth2_client_id")
  private String oauth2ClientId;

  @Column(name = "oauth2_client_secret")
  @JsonProperty("oauth2_client_secret")
  private String oauth2ClientSecret;

  @Column(name = "oauth2_scope")
  @JsonProperty("oauth2_scope")
  private String oauth2Scope;

  @Column(name = "created_by")
  @JsonProperty("created_by")
  @ReadOnlyProperty
  private String createdBy;

  @Column(name = "updated_by")
  @JsonProperty("updated_by")
  @ReadOnlyProperty
  private String updatedBy;

  @Column(name = "deleted_at")
  @JsonProperty("deleted_at")
  @ReadOnlyProperty
  private Instant deletedAt;

  @ToString.Exclude
  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "webhookEntity")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<OutgoingPackageEntity> outgoingPackageEntities;

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
