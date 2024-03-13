package com.nguyenphuong.PackageTransfer.utils;

import com.nguyenphuong.PackageTransfer.config.exceptions.UnauthorizedException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtTokenUtils {
  @Value("${jwt.secret}")
  private String secret;

  private String issuer = "package-transfer-issuer";

  public String generateToken(Authentication authentication) {
    UserDetails user = (UserDetails) authentication.getPrincipal();
    Date now = new Date();
    Date expiryTime = new Date(now.getTime() + TimeUnit.HOURS.toMillis(1));
    return Jwts.builder()
        .setSubject(user.getUsername())
        .setIssuedAt(now)
        .setExpiration(expiryTime)
        .signWith(SignatureAlgorithm.HS512, secret)
        .setIssuer(issuer)
        .compact();
  }

  public String getUsernameFromToken(String token) {
    try {
      return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    } catch (Exception e) {
      throw new UnauthorizedException("Can't get user from token");
    }
  }

  public String getIssuerFromToken(String token) {
    return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getIssuer();
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    String username = getUsernameFromToken(token);
    return username.equals(userDetails.getUsername()) &&
        !isTokenExpired(token) &&
        getIssuerFromToken(token).equals(issuer);
  }

  private boolean isTokenExpired(String token) {
    Date expiryTime = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration();
    return expiryTime.before(new Date());
  }
}
