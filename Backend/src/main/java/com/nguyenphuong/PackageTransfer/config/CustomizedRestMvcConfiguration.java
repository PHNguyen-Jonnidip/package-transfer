package com.nguyenphuong.PackageTransfer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ExposureConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Type;

@Component
public class CustomizedRestMvcConfiguration implements RepositoryRestConfigurer {
  @Autowired
  private EntityManager entityManager;

  @Override
  public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
    config.setDefaultPageSize(10);
    config.setMaxPageSize(50);
    config.setReturnBodyOnCreate(true);
    config.setReturnBodyOnUpdate(true);
    config.setDefaultMediaType(MediaType.APPLICATION_JSON);
    config.setRepositoryDetectionStrategy(RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED);
    config.exposeIdsFor(entityManager.getMetamodel().getEntities().stream().map(Type::getJavaType).toArray(Class[]::new));

    ExposureConfiguration exposureConfiguration = config.getExposureConfiguration();
    exposureConfiguration.disablePutForCreation();
    exposureConfiguration.disablePatchOnItemResources();
  }
}
