package com.nguyenphuong.PackageTransfer.services;

import com.nguyenphuong.PackageTransfer.config.exceptions.BadRequestException;
import com.nguyenphuong.PackageTransfer.enums.OutgoingPackageStatus;
import com.nguyenphuong.PackageTransfer.modals.dto.GeneralPackageInfo;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.IncomingPackageSearchCriteria;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.OutgoingPackageSearchCriteria;
import com.nguyenphuong.PackageTransfer.modals.searchCriteria.WebhookSearchCriteria;
import com.nguyenphuong.PackageTransfer.repositories.IncomingPackageRepository;
import com.nguyenphuong.PackageTransfer.repositories.OutgoingPackageRepository;
import com.nguyenphuong.PackageTransfer.repositories.WebhookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeneralInformationService {
  @Autowired
  private IncomingPackageRepository incomingPackageRepository;

  @Autowired
  private OutgoingPackageRepository outgoingPackageRepository;

  @Autowired
  private WebhookRepository webhookRepository;

  public GeneralPackageInfo getGeneralPackageInfo(Instant from, String username) {
    IncomingPackageSearchCriteria incomingSearchCriteria = new IncomingPackageSearchCriteria();
    incomingSearchCriteria.setCreatedBy(username);
    incomingSearchCriteria.setStartDate(from);
    incomingSearchCriteria.setMethod("POST");
    Long postIncoming = incomingPackageRepository.count(incomingSearchCriteria.toSpecification());
    incomingSearchCriteria.setMethod("PUT");
    Long putIncoming = incomingPackageRepository.count(incomingSearchCriteria.toSpecification());
    incomingSearchCriteria.setMethod("PATCH");
    Long patchIncoming = incomingPackageRepository.count(incomingSearchCriteria.toSpecification());
    incomingSearchCriteria.setMethod("DELETE");
    Long deleteIncoming = incomingPackageRepository.count(incomingSearchCriteria.toSpecification());

    OutgoingPackageSearchCriteria outgoingSearchCriteria = new OutgoingPackageSearchCriteria();
    outgoingSearchCriteria.setCreatedBy(username);
    outgoingSearchCriteria.setStartDate(from);
    outgoingSearchCriteria.setStatus(OutgoingPackageStatus.DONE);
    Long doneOutgoing = outgoingPackageRepository.count(outgoingSearchCriteria.toSpecification());
    outgoingSearchCriteria.setStatus(OutgoingPackageStatus.FAILED);
    Long failedOutgoing = outgoingPackageRepository.count(outgoingSearchCriteria.toSpecification());
    outgoingSearchCriteria.setStatus(OutgoingPackageStatus.ARCHIVED);
    Long archivedOutgoing = outgoingPackageRepository.count(outgoingSearchCriteria.toSpecification());

    WebhookSearchCriteria webhookSearchCriteria = new WebhookSearchCriteria();
    webhookSearchCriteria.setCreatedBy(username);
    webhookSearchCriteria.setStartDate(from);
    Long webhooks = webhookRepository.count(webhookSearchCriteria.toSpecification());
    return new GeneralPackageInfo(
        putIncoming + patchIncoming + postIncoming + deleteIncoming,
        postIncoming,
        putIncoming,
        patchIncoming,
        deleteIncoming,
        doneOutgoing + failedOutgoing + archivedOutgoing,
        doneOutgoing,
        failedOutgoing,
        archivedOutgoing,
        webhooks
    );
  }

  public Map<String, Object> getStatisticsInfo(Instant from, String username) {
    if (from.isAfter(Instant.now())) {
      throw new BadRequestException("Can't get statistics information after present time");
    }
    if (ChronoUnit.DAYS.between(from, Instant.now()) > 30) {
      throw new BadRequestException("Can't get statistics information before more than 30 days");
    }
    from = from.truncatedTo(ChronoUnit.DAYS);
    Map<String, Object> stats = new LinkedHashMap<>();
    List<String> dates = new ArrayList<>();
    List<Long> incomingCount = new ArrayList<>();
    List<Long> outgoingCount = new ArrayList<>();
    while (from.isBefore(Instant.now())) {
      IncomingPackageSearchCriteria incomingSearchCriteria = new IncomingPackageSearchCriteria();
      incomingSearchCriteria.setCreatedBy(username);
      incomingSearchCriteria.setStartDate(from);
      incomingSearchCriteria.setEndDate(from.plus(1, ChronoUnit.DAYS));
      Long incoming = incomingPackageRepository.count(incomingSearchCriteria.toSpecification());
      incomingCount.add(incoming);
      OutgoingPackageSearchCriteria outgoingSearchCriteria = new OutgoingPackageSearchCriteria();
      outgoingSearchCriteria.setCreatedBy(username);
      outgoingSearchCriteria.setStartDate(from);
      outgoingSearchCriteria.setEndDate(from.plus(1, ChronoUnit.DAYS));
      Long outgoing = outgoingPackageRepository.count(outgoingSearchCriteria.toSpecification());
      outgoingCount.add(outgoing);
      dates.add(from.toString().substring(0, 10));
      from = from.plus(1, ChronoUnit.DAYS);
    }
    stats.put("labels", dates);
    List<List<Long>> series = new ArrayList<>();
    series.add(incomingCount);
    series.add(outgoingCount);
    stats.put("series", series);
    return stats;
  }
}
