package com.nguyenphuong.PackageTransfer.config;

import com.nguyenphuong.PackageTransfer.utils.CryptoUtils;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ComponentConfig {
  @Bean
  public CryptoUtils cryptoUtils() {
    return new CryptoUtils();
  }

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes("user-token",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
        .info(new Info()
            .title("Package Transfer")
            .description("Belows are APIs for integrating with Package Transfer.")
        );
  }
}
