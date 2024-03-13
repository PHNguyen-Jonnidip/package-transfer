package com.nguyenphuong.PackageTransfer.modals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nguyenphuong.PackageTransfer.enums.OutgoingPackageStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OutgoingPackageDetail {
  @JsonProperty("id")
  private String id;

  @JsonProperty("status")
  private OutgoingPackageStatus status;

  @JsonProperty("number_of_retries")
  private Integer numberOfRetries;

  @JsonProperty("delivery_start_time_stamp")
  private Instant deliveryStartTimeStamp;

  @JsonProperty("delivery_end_time_stamp")
  private Instant deliveryEndTimeStamp;

  @JsonProperty("response_logs")
  private List<HttpResponseLogDetail> responseLogs;
}
