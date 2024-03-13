package com.nguyenphuong.PackageTransfer.modals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GeneralPackageInfo {
  @JsonProperty("incoming")
  private Long incoming;

  @JsonProperty("post_incoming")
  private Long postIncoming;

  @JsonProperty("put_incoming")
  private Long putIncoming;

  @JsonProperty("patch_incoming")
  private Long patchIncoming;

  @JsonProperty("delete_incoming")
  private Long deleteIncoming;

  @JsonProperty("outgoing")
  private Long outgoing;

  @JsonProperty("done_outgoing")
  private Long doneOutgoing;

  @JsonProperty("failed_outgoing")
  private Long failedOutgoing;

  @JsonProperty("archived_outgoing")
  private Long archivedOutgoing;

  @JsonProperty("webhook")
  private Long webhook;
}
