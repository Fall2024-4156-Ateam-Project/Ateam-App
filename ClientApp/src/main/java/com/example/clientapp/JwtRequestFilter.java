package com.example.clientapp;

import com.example.clientapp.util.JwtUtil;
import com.example.clientapp.util.Util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private Util util;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {

    // skip some paths
    String requestPath = request.getRequestURI();
    System.out.println("Incoming request: " + requestPath);
    if (requestPath.equals("/register_request") || requestPath.equals("/register_form") ||
        requestPath.equals("/login_form") || requestPath.equals("/login_request") || requestPath.equals("/")) {
      chain.doFilter(request, response);
      return;
    }

    System.out.println("Filter incoming request: " + requestPath);

    String token = util.getCookie("token", request);

    if (token == null || !jwtUtil.validateToken(token, jwtUtil.extractEmailFromToken(token))) {
      response.sendRedirect("/");
      return;
    }
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String email = jwtUtil.extractEmailFromToken(token);
      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(email, null, null);
      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }
    chain.doFilter(request, response);

  }

}