package com.nguyenphuong.PackageTransfer.controllers;

import com.nguyenphuong.PackageTransfer.modals.entities.HttpResponseLogEntity;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.HttpResponseLogSearchCriteria;
import com.nguyenphuong.PackageTransfer.modals.share.Paginator;
import com.nguyenphuong.PackageTransfer.services.HttpResponseLogService;
import com.nguyenphuong.PackageTransfer.utils.AuthenticationContextUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/http-response-logs")
@Tag(name = "Response Log", description = "Response Log APIs")
@SecurityRequirement(name = "user-token")
public class HttpResponseLogController {
  @Autowired
  private HttpResponseLogService httpResponseLogService;

  @GetMapping("")
  public ResponseEntity<Paginator<HttpResponseLogEntity>> getAll(
      @PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable,
      @RequestParam(name = "outgoing_package_id.equal", required = false) String outgoingPackageId,
      @RequestParam(name = "incoming_package_id.equal", required = false) String incomingPackageId,
      @RequestParam(name = "webhook_id.equal", required = false) String webhookId
  ) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    HttpResponseLogSearchCriteria searchCriteria = new HttpResponseLogSearchCriteria();
    searchCriteria.setWebhookId(webhookId);
    searchCriteria.setOutgoingPackageId(outgoingPackageId);
    searchCriteria.setIncomingPackageId(incomingPackageId);
    searchCriteria.setCreatedBy(userDetails.getUsername());
    Page<HttpResponseLogEntity> httpResponseLogs = httpResponseLogService.getAll(searchCriteria, pageable);
    return ResponseEntity.ok(new Paginator<>(httpResponseLogs));
  }

  @GetMapping("/{uuid}")
  public ResponseEntity<HttpResponseLogEntity> getOneById(@PathVariable String uuid) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    HttpResponseLogEntity httpResponseLogEntity = httpResponseLogService.getOneByID(uuid, userDetails);
    return ResponseEntity.ok(httpResponseLogEntity);
  }
}
