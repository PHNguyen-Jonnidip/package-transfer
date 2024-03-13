package com.nguyenphuong.PackageTransfer.modals.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginForm {
  @JsonProperty("username")
  private String username;

  @JsonProperty("password")
  private String password;
}
