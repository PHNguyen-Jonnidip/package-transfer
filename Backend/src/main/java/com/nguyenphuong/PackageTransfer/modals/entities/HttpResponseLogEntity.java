package com.nguyenphuong.PackageTransfer.modals.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "http_response_logs", indexes = {
    @Index(name = "index_http_response_logs_outgoing_package_id", columnList = "outgoing_package_id")
})
public class HttpResponseLogEntity extends AbstractEntity {
  @Column(name = "request_body", columnDefinition = "TEXT")
  @JsonProperty("request_body")
  private String requestBody;

  @Column(name = "request_headers", columnDefinition = "TEXT")
  @JsonProperty("request_headers")
  private String requestHeaders;

  @Column(name = "response_body", columnDefinition = "TEXT")
  @JsonProperty("response_body")
  private String responseBody;

  @Column(name = "response_code")
  @JsonProperty("response_code")
  private String responseCode;

  @Column(name = "response_headers", columnDefinition = "TEXT")
  @JsonProperty("response_headers")
  private String responseHeaders;

  @Column(name = "correlation_id")
  @JsonProperty("correlation_id")
  private String correlationId;

  @Column(name = "callback_url")
  @JsonProperty("callback_url")
  private String callbackUrl;

  @ManyToOne(optional = false)
  @JoinColumn(name = "outgoing_package_id")
  @JsonIgnore
  private OutgoingPackageEntity outgoingPackageEntity;

  @JsonProperty("outgoing_package_id")
  public String getOutgoingPackageId() {
    return this.outgoingPackageEntity.getId();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
