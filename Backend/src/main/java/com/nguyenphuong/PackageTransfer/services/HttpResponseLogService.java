package com.nguyenphuong.PackageTransfer.services;

import com.nguyenphuong.PackageTransfer.config.exceptions.RecordNotFoundException;
import com.nguyenphuong.PackageTransfer.modals.entities.HttpResponseLogEntity;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.HttpResponseLogSearchCriteria;
import com.nguyenphuong.PackageTransfer.repositories.HttpResponseLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class HttpResponseLogService {
  @Autowired
  private HttpResponseLogRepository httpResponseLogRepository;

  public Page<HttpResponseLogEntity> getAll(HttpResponseLogSearchCriteria searchCriteria, Pageable pageable) {
    return httpResponseLogRepository.findAll(searchCriteria.toSpecification(), pageable);
  }

  public HttpResponseLogEntity getOneByID(String uuid, UserDetails userDetails) {
    HttpResponseLogEntity httpResponseLogEntity = getOneNullable(uuid);
    if (httpResponseLogEntity == null ||
        !httpResponseLogEntity
            .getOutgoingPackageEntity()
            .getIncomingPackageEntity()
            .getCreatedBy()
            .equals(userDetails.getUsername())) {
      throw new RecordNotFoundException("Can't find http response log having id: " + uuid);
    }
    return httpResponseLogEntity;
  }

  private HttpResponseLogEntity getOneNullable(String uuid) {
    return httpResponseLogRepository.findById(uuid).orElse(null);
  }
}
