package com.weatherapp.utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Autowired private Environment env;
  private final long EXPIRATION_TIME = 864_000_000; // 10 days in milliseconds

  private final String SAFE_SECRET_KEY =
      "ThisIsASafeSecretKeyThisIsASafeSecretKeyThisIsASafeSecretKeyThisIsASafeSecretKey";

  public String getSecretKey() {
    assert env != null;
    return env.getProperty("JWT_SECRET_KEY", SAFE_SECRET_KEY);
  }

  public String generateToken(String username, List<String> roles) {
    return Jwts.builder()
        .setSubject(username)
        .claim("roles", roles)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(SignatureAlgorithm.HS512, getSecretKey())
        .compact();
  }

  public String getUsernameFromToken(String token) {
    return Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(token).getBody().getSubject();
  }

  public boolean isValidToken(String jwt) {
    try {
      Claims claims = Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(jwt).getBody();
      List<String> roles = claims.get("roles", List.class);
      // TODO: Possible to do something with roles if needed
      // Do something with roles if needed
      // To Allow access to admin resources if the user has admin role
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
