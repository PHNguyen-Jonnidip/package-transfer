package com.nguyenphuong.PackageTransfer.modals.searchCriteria;

import com.nguyenphuong.PackageTransfer.modals.entities.IncomingPackageEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
public class IncomingPackageSearchCriteria extends IncomingPackageEntity {
  private String payloadContain;
  private String topicContain;
  private Instant startDate;
  private Instant endDate;

  public Specification<IncomingPackageEntity> toSpecification() {
    Specification<IncomingPackageEntity> specification = Specification.where(null);

    if (StringUtils.isNotBlank(topicContain)) {
      specification = specification.and((Specification<IncomingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root.join("targetTopics").get("topic"), topicContain));
    }

    if (Objects.nonNull(getStatus())) {
      specification = specification.and((Specification<IncomingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root.get("status"), getStatus()));
    }

    if (Objects.nonNull(getMethod())) {
      specification = specification.and((Specification<IncomingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root.get("method"), getMethod()));
    }

    if (StringUtils.isNotBlank(payloadContain)) {
      specification = specification.and((Specification<IncomingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.like(root.get("payload"), "%" + payloadContain + "%"));
    }

    if (StringUtils.isNotBlank(getCreatedBy())) {
      specification = specification.and((Specification<IncomingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root.get("createdBy"), getCreatedBy()));
    }

    if (Objects.nonNull(startDate)) {
      specification = specification.and((Specification<IncomingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
    }

    if (Objects.nonNull(endDate)) {
      specification = specification.and((Specification<IncomingPackageEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
    }

    return specification;
  }
}
