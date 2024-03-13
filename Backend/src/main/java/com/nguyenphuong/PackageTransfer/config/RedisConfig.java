package com.nguyenphuong.PackageTransfer.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
  private final static Logger LOGGER = LoggerFactory.getLogger(RedisConfig.class);
  @Value("${redis.enable}")
  private Boolean isRedisEnable;
  @Value("${redis.url}")
  private String url;
  @Value("${redis.database}")
  private Integer database;
  @Value("${redis.password}")
  private String password;

  @Bean
  public RedissonClient redissonClient() {
    if (!isRedisEnable) {
      return null;
    }
    try {
      Config config = new Config();
      config.useSingleServer()
          .setAddress(url)
          .setDatabase(database);
      if (StringUtils.isNotBlank(password)) {
        config.useSingleServer().setPassword(password);
      }
      return Redisson.create(config);
    } catch (RedisException ex) {
      LOGGER.error("Can't connect to Redis", ex);
      return null;
    }
  }
}
