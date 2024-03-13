package com.nguyenphuong.PackageTransfer.mapper;

import com.nguyenphuong.PackageTransfer.modals.dto.HttpResponseLogDetail;
import com.nguyenphuong.PackageTransfer.modals.dto.IncomingPackageDetail;
import com.nguyenphuong.PackageTransfer.modals.dto.OutgoingPackageDetail;
import com.nguyenphuong.PackageTransfer.modals.entities.HttpResponseLogEntity;
import com.nguyenphuong.PackageTransfer.modals.entities.IncomingPackageEntity;
import com.nguyenphuong.PackageTransfer.modals.entities.OutgoingPackageEntity;
import com.nguyenphuong.PackageTransfer.repositories.HttpResponseLogRepository;
import com.nguyenphuong.PackageTransfer.repositories.OutgoingPackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class IncomingPackageDetailMapper {
  @Autowired
  private OutgoingPackageRepository outgoingPackageRepository;

  @Autowired
  private HttpResponseLogRepository httpResponseLogRepository;

  public IncomingPackageDetail toDetailModel(IncomingPackageEntity incomingPackageEntity) {
    IncomingPackageDetail incomingPackageDetail = new IncomingPackageDetail();
    incomingPackageDetail.setId(incomingPackageEntity.getId());
    incomingPackageDetail.setStatus(incomingPackageEntity.getStatus());
    incomingPackageDetail.setPayload(incomingPackageEntity.getPayload());
    incomingPackageDetail.setTargetTopics(incomingPackageEntity.getTargetTopics());
    incomingPackageDetail.setCreatedAt(incomingPackageEntity.getCreatedAt());

    incomingPackageDetail.setOutgoingPackages(incomingPackageEntity
        .getOutgoingPackageEntities()
        .stream()
        .map(out -> outgoingPackageRepository.findById(out.getId()).orElse(null))
        .filter(Objects::nonNull)
        .map(this::toDetailModel)
        .collect(Collectors.toList()));

    return incomingPackageDetail;
  }

  public OutgoingPackageDetail toDetailModel(OutgoingPackageEntity outgoingPackageEntity) {
    OutgoingPackageDetail outgoingPackageDetail = new OutgoingPackageDetail();
    outgoingPackageDetail.setId(outgoingPackageEntity.getId());
    outgoingPackageDetail.setStatus(outgoingPackageEntity.getStatus());
    outgoingPackageDetail.setNumberOfRetries(outgoingPackageEntity.getNumberOfRetries());
    outgoingPackageDetail.setDeliveryStartTimeStamp(outgoingPackageEntity.getDeliveryStartTimeStamp());
    outgoingPackageDetail.setDeliveryEndTimeStamp(outgoingPackageEntity.getDeliveryEndTimeStamp());

    outgoingPackageDetail.setResponseLogs(outgoingPackageEntity
        .getHttpResponseLogEntities()
        .stream()
        .map(response -> httpResponseLogRepository.findById(response.getId()).orElse(null))
        .filter(Objects::nonNull)
        .map(this::toDetailModel)
        .collect(Collectors.toList()));

    return outgoingPackageDetail;
  }

  public HttpResponseLogDetail toDetailModel(HttpResponseLogEntity httpResponseLogEntity) {
    HttpResponseLogDetail responseLogDetail = new HttpResponseLogDetail();
    responseLogDetail.setId(httpResponseLogEntity.getId());
    responseLogDetail.setResponseBody(httpResponseLogEntity.getResponseBody());
    responseLogDetail.setHttpHeader(httpResponseLogEntity.getResponseHeaders());
    responseLogDetail.setHttpCode(httpResponseLogEntity.getResponseCode());
    responseLogDetail.setCallbackUrl(httpResponseLogEntity.getCallbackUrl());
    responseLogDetail.setCreatedAt(httpResponseLogEntity.getCreatedAt());
    return responseLogDetail;
  }
}
