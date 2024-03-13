package com.nguyenphuong.PackageTransfer.modals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class HttpResponseLogDetail {
  @JsonProperty("id")
  private String id;

  @JsonProperty("callback_url")
  private String callbackUrl;

  @JsonProperty("http_code")
  private String httpCode;

  @JsonProperty("http_header")
  private String httpHeader;

  @JsonProperty("response_body")
  private String responseBody;

  @JsonProperty("created_at")
  private Instant createdAt;
}
