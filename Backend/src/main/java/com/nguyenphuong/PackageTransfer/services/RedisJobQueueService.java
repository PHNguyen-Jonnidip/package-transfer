package com.nguyenphuong.PackageTransfer.services;

import com.nguyenphuong.PackageTransfer.modals.entities.OutgoingPackageEntity;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;

@Service
public class RedisJobQueueService {
  private static final Logger LOGGER = LoggerFactory.getLogger(RedisJobQueueService.class);

  @Value("${delivery.outgoing-package-sorted-set-name}")
  private String outgoingSortedSetName;

  @Autowired(required = false)
  private RedissonClient redissonClient;

  @Autowired
  private ApplicationContext context;

  private RScoredSortedSet<String> sortedSet;

  @PostConstruct()
  public void postConstruct() {
    if (redissonClient != null) {
      sortedSet = redissonClient.getScoredSortedSet(outgoingSortedSetName, new StringCodec());
    } else {
      LOGGER.error("RedissonClient is not available. Stopping server");
      if (context instanceof ConfigurableApplicationContext) {
        SpringApplication.exit(context, () -> 0);
      }
    }
  }

  public void addAll(List<OutgoingPackageEntity> outgoingPackageEntities) {
    //TODO add when redis is null
    for (OutgoingPackageEntity outgoingPackageEntity : outgoingPackageEntities) {
      addOutgoingToQueue(Instant.now(), outgoingPackageEntity.getId());
    }
  }

  public void addOutgoingToQueue(Instant deliveryTime, String outgoingPackageId) {
    //TODO add when redis is null
    sortedSet.add(deliveryTime.toEpochMilli(), outgoingPackageId);
  }

  public String pollFirst() {
    //TODO add when redis is null
    Double firstScore = sortedSet.firstScore();
    if (firstScore == null) {
      return null;
    }
    Instant deliveryTime = Instant.ofEpochMilli(firstScore.longValue());
    if (deliveryTime.isAfter(Instant.now())) {
      return null;
    }
    return sortedSet.pollFirst();
  }
}
