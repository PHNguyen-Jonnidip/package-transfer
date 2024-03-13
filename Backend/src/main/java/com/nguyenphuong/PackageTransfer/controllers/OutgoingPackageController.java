package com.nguyenphuong.PackageTransfer.controllers;

import com.nguyenphuong.PackageTransfer.config.exceptions.RecordNotFoundException;
import com.nguyenphuong.PackageTransfer.enums.OutgoingPackageStatus;
import com.nguyenphuong.PackageTransfer.modals.entities.OutgoingPackageEntity;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.OutgoingPackageSearchCriteria;
import com.nguyenphuong.PackageTransfer.modals.share.Paginator;
import com.nguyenphuong.PackageTransfer.services.DeliveryOutgoingPackageService;
import com.nguyenphuong.PackageTransfer.services.OutgoingPackageService;
import com.nguyenphuong.PackageTransfer.utils.AuthenticationContextUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/outgoing-packages")
@Tag(name = "Outgoing Package", description = "Outgoing Package APIs")
@SecurityRequirement(name = "user-token")
public class OutgoingPackageController {
  @Autowired
  private OutgoingPackageService outgoingPackageService;

  @Autowired
  private DeliveryOutgoingPackageService deliveryOutgoingPackageService;

  @GetMapping("")
  public ResponseEntity<Paginator<OutgoingPackageEntity>> getAll(
      @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
      @RequestParam(name = "status.equal", required = false) OutgoingPackageStatus status,
      @RequestParam(name = "incoming_package_id.equal", required = false) String incomingPackageId,
      @RequestParam(name = "webhook_id.equal", required = false) String webhookId
  ) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    OutgoingPackageSearchCriteria searchCriteria = new OutgoingPackageSearchCriteria();
    searchCriteria.setIncomingPackageId(incomingPackageId);
    searchCriteria.setWebhookId(webhookId);
    searchCriteria.setStatus(status);
    searchCriteria.setCreatedBy(userDetails.getUsername());
    Page<OutgoingPackageEntity> outgoingPackages = outgoingPackageService.getAll(searchCriteria, pageable);
    return ResponseEntity.ok(new Paginator<>(outgoingPackages));
  }

  @GetMapping("/{id}")
  public ResponseEntity<OutgoingPackageEntity> getOneById(@PathVariable String id) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    OutgoingPackageEntity outgoingPackageEntity = outgoingPackageService.getOneByID(id, userDetails);
    return ResponseEntity.ok(outgoingPackageEntity);
  }

  @PostMapping("/{id}/trigger-delivery")
  public ResponseEntity<OutgoingPackageEntity> triggerDelivery(@PathVariable String id) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    OutgoingPackageEntity outgoingPackageEntity = outgoingPackageService.getOneByID(id, userDetails);
    outgoingPackageEntity = deliveryOutgoingPackageService.delivery(outgoingPackageEntity.getId());
    if (outgoingPackageEntity == null) {
      throw new RecordNotFoundException("Can't find outgoing package with id: " + id);
    }
    return ResponseEntity.ok(outgoingPackageEntity);
  }
}
