package com.nguyenphuong.PackageTransfer.controllers;

import com.nguyenphuong.PackageTransfer.enums.IncomingPackageStatus;
import com.nguyenphuong.PackageTransfer.modals.dto.IncomingPackageDetail;
import com.nguyenphuong.PackageTransfer.modals.entities.IncomingPackageEntity;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.IncomingPackageSearchCriteria;
import com.nguyenphuong.PackageTransfer.modals.share.Paginator;
import com.nguyenphuong.PackageTransfer.services.IncomingPackageService;
import com.nguyenphuong.PackageTransfer.utils.AuthenticationContextUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incoming-packages")
@Tag(name = "Incoming Package", description = "Incoming Package APIs")
@SecurityRequirement(name = "user-token")
public class IncomingPackageController {
  @Autowired
  private IncomingPackageService incomingPackageService;

  @GetMapping("")
  public ResponseEntity<Paginator<IncomingPackageEntity>> getAll(
      @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
      @RequestParam(name = "status.equal", required = false) IncomingPackageStatus status,
      @RequestParam(name = "payload.contain", required = false) String payloadContain
  ) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    IncomingPackageSearchCriteria searchCriteria = new IncomingPackageSearchCriteria();
    searchCriteria.setStatus(status);
    searchCriteria.setPayloadContain(payloadContain);
    searchCriteria.setCreatedBy(userDetails.getUsername());
    Page<IncomingPackageEntity> incomingPackages = incomingPackageService.getAll(searchCriteria, pageable);
    return ResponseEntity.ok(new Paginator<>(incomingPackages));
  }

  @GetMapping("/{uuid}")
  public ResponseEntity<IncomingPackageDetail> getOneById(@PathVariable String uuid) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    IncomingPackageDetail incomingPackageEntity = incomingPackageService.getOneByID(uuid, userDetails);
    return ResponseEntity.ok(incomingPackageEntity);
  }

  @PostMapping("")
  public ResponseEntity<IncomingPackageEntity> create(@RequestBody IncomingPackageEntity incomingPackageEntity) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    incomingPackageEntity.setCreatedBy(userDetails.getUsername());
    incomingPackageEntity = incomingPackageService.create(incomingPackageEntity);
    return new ResponseEntity<>(incomingPackageEntity, HttpStatus.CREATED);
  }
}
