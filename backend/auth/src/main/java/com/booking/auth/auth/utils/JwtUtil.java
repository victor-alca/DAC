package com.booking.auth.auth.utils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
  private static final String SECRET = System.getenv("JWT_SECRET");
  private static final long EXPIRATION_TIME = 86400000;

  private static final Key key = new SecretKeySpec(
      Base64.getDecoder().decode(SECRET),
      SignatureAlgorithm.HS256.getJcaName());
  

  public static String generateToken(String subject, String userType) {
    return Jwts.builder()
        .setSubject(subject)
        .claim("role", userType)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(key)
        .compact();
  }
}
