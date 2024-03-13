package com.nguyenphuong.PackageTransfer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ExecutorConfig {
  private ExecutorService executorService;

  @Bean(name = {"httpAsyncExecutorService"})
  @Primary
  public ExecutorService executorService() {
    executorService = Executors.newFixedThreadPool(10);
    return executorService;
  }
  
  @PreDestroy
  public void cleanUp() {
    executorService.shutdown();
  }
}
