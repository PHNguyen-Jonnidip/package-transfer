package com.nguyenphuong.PackageTransfer.controllers;

import com.nguyenphuong.PackageTransfer.modals.entities.WebhookEntity;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.WebhookSearchCriteria;
import com.nguyenphuong.PackageTransfer.modals.share.Paginator;
import com.nguyenphuong.PackageTransfer.services.WebhookService;
import com.nguyenphuong.PackageTransfer.utils.AuthenticationContextUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks")
@Tag(name = "Webhook", description = "Webhook APIs")
@SecurityRequirement(name = "user-token")
public class WebhookController {
  @Autowired
  private WebhookService webhookService;

  @GetMapping("")
  public ResponseEntity<Paginator<WebhookEntity>> getAll(
      @PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable,
      @RequestParam(name = "url.contain", required = false) String urlContain,
      @RequestParam(name = "topic.equal", required = false) String topic
  ) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    WebhookSearchCriteria searchCriteria = new WebhookSearchCriteria();
    searchCriteria.setUrlContain(urlContain);
    searchCriteria.setTopic(topic);
    searchCriteria.setCreatedBy(userDetails.getUsername());
    Page<WebhookEntity> webhookPage = webhookService.getAll(searchCriteria, pageable);
    return ResponseEntity.ok(new Paginator<>(webhookPage));
  }

  @GetMapping("/{id}")
  public ResponseEntity<WebhookEntity> getOneById(@PathVariable String id) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    WebhookEntity webhookEntity = webhookService.getOneByID(id, userDetails);
    return ResponseEntity.ok(webhookEntity);
  }

  @PostMapping("")
  public ResponseEntity<WebhookEntity> create(@RequestBody WebhookEntity webhookEntity) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    webhookEntity.setCreatedBy(userDetails.getUsername());

    webhookEntity = webhookService.create(webhookEntity);
    return new ResponseEntity<>(webhookEntity, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<WebhookEntity> update(@PathVariable String id, @RequestBody WebhookEntity webhookEntity) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    webhookEntity = webhookService.update(id, webhookEntity, userDetails);
    return ResponseEntity.ok(webhookEntity);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable String id) {
    UserDetails userDetails = AuthenticationContextUtils.getUserContext();
    webhookService.delete(id, false, userDetails);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
