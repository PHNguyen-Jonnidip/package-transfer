package com.nguyenphuong.PackageTransfer.config.security;

import brave.Span;
import brave.Tracer;
import com.nguyenphuong.PackageTransfer.services.UserService;
import com.nguyenphuong.PackageTransfer.utils.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  @Autowired
  private JwtTokenUtils jwtTokenUtil;

  @Autowired
  private UserService userService;

  @Autowired
  private Tracer tracer;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    long startTime = System.currentTimeMillis();
    Span span = tracer.currentSpan();
    if (span == null) {
      span = tracer.newTrace();
    }
    String correlationId = span.context().traceIdString();
    response.setHeader("X-CorrelationId", correlationId);
    String header = request.getHeader("Authorization");
    String username = "";
    if (header != null && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      username = jwtTokenUtil.getUsernameFromToken(token);

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = userService.loadUserByUsername(username);

        if (jwtTokenUtil.validateToken(token, userDetails)) {
          UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
          authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          response.setHeader("X-Username", username);
        }
      }
    }
    chain.doFilter(request, response);
    long elapsed = System.currentTimeMillis() - startTime;
    Map<String, Object> requestParameterMap = new HashMap<>();
    for (String param : Collections.list(request.getParameterNames())) {
      requestParameterMap.put(param, request.getParameter(param));
    }
    LOGGER.info("request_method={}, request_url={}, media_type={} with request_parameters={}, user_principal={}, responded with http_code={} in response_time={} ms",
        request.getMethod(), request.getRequestURI(), request.getHeader("accept"), requestParameterMap, username, response.getStatus(), elapsed);
  }
}