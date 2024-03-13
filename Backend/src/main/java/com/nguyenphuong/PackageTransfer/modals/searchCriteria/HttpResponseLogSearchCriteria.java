package com.nguyenphuong.PackageTransfer.modals.searchCriteria;

import com.nguyenphuong.PackageTransfer.modals.entities.HttpResponseLogEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
public class HttpResponseLogSearchCriteria extends HttpResponseLogEntity {
  private String outgoingPackageId;
  private String incomingPackageId;
  private String webhookId;
  private String createdBy;

  public Specification<HttpResponseLogEntity> toSpecification() {
    Specification<HttpResponseLogEntity> specification = Specification.where(null);

    if (StringUtils.isNotBlank(outgoingPackageId)) {
      specification = specification.and((Specification<HttpResponseLogEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root.join("outgoingPackageEntity").get("id"), outgoingPackageId));
    }

    if (StringUtils.isNotBlank(incomingPackageId)) {
      specification = specification.and((Specification<HttpResponseLogEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root
          .join("outgoingPackageEntity")
          .join("incomingPackageEntity")
          .get("id"), incomingPackageId));
    }

    if (StringUtils.isNotBlank(webhookId)) {
      specification = specification.and((Specification<HttpResponseLogEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root
          .join("outgoingPackageEntity")
          .join("webhookEntity")
          .get("id"), webhookId));
    }

    if (StringUtils.isNotBlank(createdBy)) {
      specification = specification.and((Specification<HttpResponseLogEntity>) (root, criteriaQuery, criteriaBuilder)
          -> criteriaBuilder.equal(root
          .join("outgoingPackageEntity")
          .join("incomingPackageEntity")
          .get("createdBy"), createdBy));
    }
    return specification;
  }
}
