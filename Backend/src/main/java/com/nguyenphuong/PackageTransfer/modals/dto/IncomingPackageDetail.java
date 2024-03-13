package com.nguyenphuong.PackageTransfer.modals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nguyenphuong.PackageTransfer.enums.IncomingPackageStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class IncomingPackageDetail {
  @JsonProperty("id")
  private String id;

  @JsonProperty("payload")
  private String payload;

  @JsonProperty("status")
  private IncomingPackageStatus status;

  @JsonProperty("target_topics")
  private Set<String> targetTopics;

  @JsonProperty("created_at")
  private Instant createdAt;

  @JsonProperty("outgoing_packages")
  private List<OutgoingPackageDetail> outgoingPackages;
}
