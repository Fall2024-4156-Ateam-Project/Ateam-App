package com.example.clientapp;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class RequestLoggingFilter implements Filter {

  private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;

    logger.info("Incoming Request: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());

    // Log headers
    Enumeration<String> headerNames = httpRequest.getHeaderNames();
    if (headerNames != null) {
      while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        String headerValue = httpRequest.getHeader(headerName);
        logger.info("Header: {} = {}", headerName, headerValue);
      }
    }

    // Continue with the filter chain
    chain.doFilter(request, response);
  }
}