package com.nguyenphuong.PackageTransfer.modals.searchCriteria;

import com.nguyenphuong.PackageTransfer.modals.entities.OutgoingPackageEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
public class OutgoingPackageSearchCriteria extends OutgoingPackageEntity {
  private String incomingPackageId;
  private String webhookId;
  private String createdBy;
  private Instant startDate;
  private Instant endDate;

  public Specification<OutgoingPackageEntity> toSpecification() {
    Specification<OutgoingPackageEntity> specification = Specification.where(null);

    if (StringUtils.isNotBlank(incomingPackageId)) {
      specification = specification.and((Specification<OutgoingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root
          .join("incomingPackageEntity")
          .get("id"), incomingPackageId));
    }

    if (StringUtils.isNotBlank(webhookId)) {
      specification = specification.and((Specification<OutgoingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root
          .join("webhookEntity")
          .get("id"), webhookId));
    }

    if (Objects.nonNull(getStatus())) {
      specification = specification.and((Specification<OutgoingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root.get("status"), getStatus()));
    }

    if (Objects.nonNull(createdBy)) {
      specification = specification.and((Specification<OutgoingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root
          .join("incomingPackageEntity")
          .get("createdBy"), createdBy));
    }

    if (Objects.nonNull(startDate)) {
      specification = specification.and((Specification<OutgoingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
    }

    if (Objects.nonNull(endDate)) {
      specification = specification.and((Specification<OutgoingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
    }

    return specification;
  }
}
