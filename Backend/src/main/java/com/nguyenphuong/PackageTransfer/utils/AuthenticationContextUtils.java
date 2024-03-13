package com.nguyenphuong.PackageTransfer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthenticationContextUtils {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationContextUtils.class);

  public static UserDetails getUserContext() {
    return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
