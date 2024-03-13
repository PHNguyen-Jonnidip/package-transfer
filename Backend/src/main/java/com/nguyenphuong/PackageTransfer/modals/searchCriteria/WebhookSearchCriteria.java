package com.nguyenphuong.PackageTransfer.modals.searchCriteria;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nguyenphuong.PackageTransfer.enums.WebhookStatus;
import com.nguyenphuong.PackageTransfer.modals.entities.WebhookEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class WebhookSearchCriteria extends WebhookEntity {
  private String urlContain;

  private Set<String> targetTopics = new HashSet<>();

  private Boolean isAvailable;

  private Instant startDate;

  public Specification<WebhookEntity> toSpecification() {
    Specification<WebhookEntity> specification = Specification.where(null);

    if (Boolean.TRUE.equals(isAvailable)) {
      this.setStatus(WebhookStatus.ACTIVE);
      specification = specification.and((Specification<WebhookEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.isNull(root.get("deletedAt")));
    }

    if (StringUtils.isNotBlank(getTopic())) {
      specification = specification.and((Specification<WebhookEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root.get("topic"), getTopic()));
    }

    if (!targetTopics.isEmpty()) {
      specification = specification.and((Specification<WebhookEntity>) (root, criteriaQuery, criteriaBuilder)
          -> root.get("topic").in(targetTopics));
    }

    if (StringUtils.isNotBlank(urlContain)) {
      specification = specification.and((Specification<WebhookEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.like(root.get("callbackURL"), "%" + urlContain + "%"));
    }

    if (getStatus() != null) {
      specification = specification.and((Specification<WebhookEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root.get("status"), getStatus()));
    }

    if (StringUtils.isNotBlank(getCreatedBy())) {
      specification = specification.and((Specification<WebhookEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root.get("createdBy"), getCreatedBy()));
    }

    if (Objects.nonNull(startDate)) {
      specification = specification.and((Specification<WebhookEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
    }

    return specification;
  }
}
