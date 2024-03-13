package com.nguyenphuong.PackageTransfer.controllers;

import com.nguyenphuong.PackageTransfer.modals.dto.GeneralPackageInfo;
import com.nguyenphuong.PackageTransfer.services.GeneralInformationService;
import com.nguyenphuong.PackageTransfer.utils.AuthenticationContextUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/general-information")
@Tag(name = "General Information", description = "General Information APIs")
@SecurityRequirement(name = "user-token")
public class GeneralInformationController {

  @Autowired
  private GeneralInformationService generalInformationService;

  @GetMapping("/packages")
  public ResponseEntity<GeneralPackageInfo> getPackagesInformation(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    return ResponseEntity.ok(generalInformationService.getGeneralPackageInfo(from, userDetails.getUsername()));
  }

  @GetMapping("/statistics-incoming")
  public ResponseEntity<Map<String, Object>> getIncomingStatistic(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    return ResponseEntity.ok(generalInformationService.getStatisticsInfo(from, userDetails.getUsername()));
  }
}
