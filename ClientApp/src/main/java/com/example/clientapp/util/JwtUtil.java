package com.example.clientapp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class JwtUtil {

  @Value("${app.SECRET_KEY}")
  private String secret_key;

  public String generateToken(String email) {
    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours exp
        .signWith(SignatureAlgorithm.HS256, secret_key)
        .compact();
  }

  public String extractEmailFromToken(String token) {
    if(token == null){
      return "";
    }
    return extractClaimsFromToken(token).getSubject();
  }

  public boolean isTokenExpired(String token) {
    if(token == null){
      // if no token assume it is expired
      return true;
    }
    return extractClaimsFromToken(token).getExpiration().before(new Date());
  }

  public boolean validateToken(String token, String email) {
    String extractedEmail = extractEmailFromToken(token);
    return (extractedEmail.equals(email) && !isTokenExpired(token));
  }

  //Helper methods

  private Claims extractClaimsFromToken(String token) {
    return Jwts.parser()
        .setSigningKey(secret_key)
        .parseClaimsJws(token)
        .getBody();
  }

}
