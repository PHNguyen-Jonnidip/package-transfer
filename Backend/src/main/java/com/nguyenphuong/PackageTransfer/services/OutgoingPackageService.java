package com.nguyenphuong.PackageTransfer.services;

import com.nguyenphuong.PackageTransfer.config.exceptions.RecordNotFoundException;
import com.nguyenphuong.PackageTransfer.modals.entities.OutgoingPackageEntity;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.OutgoingPackageSearchCriteria;
import com.nguyenphuong.PackageTransfer.repositories.OutgoingPackageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class OutgoingPackageService {
  private final static Logger LOGGER = LoggerFactory.getLogger(OutgoingPackageService.class);
  @Autowired
  private OutgoingPackageRepository outgoingPackageRepository;

  public Page<OutgoingPackageEntity> getAll(OutgoingPackageSearchCriteria searchCriteria, Pageable pageable) {
    return outgoingPackageRepository.findAll(searchCriteria.toSpecification(), pageable);
  }

  public OutgoingPackageEntity getOneByID(String uuid, UserDetails userDetails) {
    OutgoingPackageEntity outgoingPackageEntity = getOneNullable(uuid);
    if (outgoingPackageEntity == null ||
        !outgoingPackageEntity.getIncomingPackageEntity().getCreatedBy().equals(userDetails.getUsername())) {
      throw new RecordNotFoundException("Can't find outgoing package having id: " + uuid);
    }
    return outgoingPackageEntity;
  }

  private OutgoingPackageEntity getOneNullable(String uuid) {
    return outgoingPackageRepository.findById(uuid).orElse(null);
  }
}
