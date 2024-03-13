package com.nguyenphuong.PackageTransfer.config.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExceptionResponse {
  private boolean success;
  private String status;
  private String message;

  public ExceptionResponse(boolean success, String status, String message) {
    this.success = success;
    this.status = status;
    this.message = message;
  }
}
