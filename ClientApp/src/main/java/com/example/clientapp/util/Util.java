package com.example.clientapp.util;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import org.springframework.stereotype.Component;

@Component
public class Util {

  public String hashEmail(String email) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashBytes = digest.digest(email.getBytes());
      return Base64.getUrlEncoder().encodeToString(hashBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error hashing email", e);
    }
  }

  public String getCurrentTime() {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    return now.format(formatter);
  }

  public String getCookie(String name, HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (name.equals(cookie.getName())) {
          return URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
        }
      }
    }
    return null;
  }

  public void setCookie(String cookieName, String cookieValue, HttpServletResponse response) {
    Cookie cookie = new Cookie(cookieName, URLEncoder.encode(cookieValue, StandardCharsets.UTF_8));
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(3600 * 10); // 10 hours
    response.addCookie(cookie);
  }
}
