package com.nguyenphuong.PackageTransfer.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@EnableWebSecurity
@Configuration
@Order(1)
public class AdminSecurityConfig extends WebSecurityConfigurerAdapter {
  @Value("${admin.username}")
  private String adminUsername;

  @Value("${admin.password}")
  private String adminPassword;

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> configurer = auth.inMemoryAuthentication();
    configurer.withUser(adminUsername).password(passwordEncoder().encode(adminPassword)).authorities("ADMIN").roles("ADMIN");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.requestMatcher(new BasicRequestMatcher()).authorizeRequests((authorizeRequests) ->
        authorizeRequests
            .antMatchers("/api/admin/**").hasRole("ADMIN")
    ).httpBasic().authenticationEntryPoint(authenticationEntryPoint());
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint() {
    return (request, response, authException) -> response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  private class BasicRequestMatcher implements RequestMatcher {
    @Override
    public boolean matches(HttpServletRequest httpRequest) {
      String auth = httpRequest.getHeader("Authorization");
      return !Objects.isNull(auth)
          && auth.startsWith("Basic");
    }
  }
}
