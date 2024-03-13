package com.nguyenphuong.PackageTransfer.modals.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nguyenphuong.PackageTransfer.enums.OutgoingPackageStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "outgoing_packages", indexes = {
    @Index(name = "index_outgoing_packages_incoming_package_id", columnList = "incoming_package_id"),
    @Index(name = "index_outgoing_packages_webhook_id", columnList = "webhook_id")
})
public class OutgoingPackageEntity extends AbstractEntity {
  @Column(name = "status", nullable = false)
  @Enumerated(EnumType.STRING)
  private OutgoingPackageStatus status;

  @Column(name = "number_of_retries")
  @JsonProperty("number_of_retries")
  private Integer numberOfRetries = 0;

  @Column(name = "first_failed_delivery_time_stamp")
  @JsonProperty("first_failed_delivery_time_stamp")
  private Instant firstFailedDeliveryTimeStamp;

  @Column(name = "last_failed_delivery_time_stamp")
  @JsonProperty("last_failed_delivery_time_stamp")
  private Instant lastFailedDeliveryTimeStamp;

  @Column(name = "delivery_start_time_stamp")
  @JsonProperty("delivery_start_time_stamp")
  private Instant deliveryStartTimeStamp;

  @Column(name = "delivery_end_time_stamp")
  @JsonProperty("delivery_end_time_stamp")
  private Instant deliveryEndTimeStamp;

  @ManyToOne(optional = false)
  @JoinColumn(name = "incoming_package_id")
  @JsonIgnore
  @ToString.Exclude
  private IncomingPackageEntity incomingPackageEntity;

  @ManyToOne(optional = false)
  @JoinColumn(name = "webhook_id")
  @JsonIgnore
  @ToString.Exclude
  private WebhookEntity webhookEntity;

  @ToString.Exclude
  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY,
      cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
      mappedBy = "outgoingPackageEntity")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private List<HttpResponseLogEntity> httpResponseLogEntities;

  @JsonProperty("incoming_package_id")
  public String getIncomingPackageId() {
    return this.incomingPackageEntity.getId();
  }

  @JsonProperty("webhook_id")
  public String getWebhookId() {
    return this.webhookEntity.getId();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  public void increaseNumberOfRetry() {
    this.numberOfRetries++;
  }

  @JsonIgnore
  public boolean isArchivable() {
    if (firstFailedDeliveryTimeStamp == null) {
      return false;
    }
    return numberOfRetries > 50 || Instant.now().isAfter(firstFailedDeliveryTimeStamp.plus(1, ChronoUnit.DAYS));
  }

  @JsonIgnore
  public Instant getNextRetryInstant() {
    Instant nextRetry;
    if (lastFailedDeliveryTimeStamp == null) {
      nextRetry = Instant.now();
    } else {
      nextRetry = lastFailedDeliveryTimeStamp;
    }
    switch (numberOfRetries) {
      case 0:
        nextRetry = nextRetry.plus(5, ChronoUnit.MINUTES);
        break;
      case 1:
        nextRetry = nextRetry.plus(30, ChronoUnit.MINUTES);
        break;
      case 2:
        nextRetry = nextRetry.plus(1, ChronoUnit.HOURS);
        break;
      case 3:
        nextRetry = nextRetry.plus(3, ChronoUnit.HOURS);
        break;
      case 4:
        nextRetry = nextRetry.plus(7, ChronoUnit.HOURS);
        break;
      case 5:
        nextRetry = nextRetry.plus(12, ChronoUnit.HOURS);
        break;
      case 6:
        nextRetry = nextRetry.plus(1, ChronoUnit.DAYS);
        break;
      default:
        nextRetry = nextRetry.plus(3, ChronoUnit.DAYS);
        break;
    }
    return nextRetry;
  }
}
