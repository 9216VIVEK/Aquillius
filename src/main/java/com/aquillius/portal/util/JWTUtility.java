package com.aquillius.portal.util;

import java.io.Serializable;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.aquillius.portal.configuration.CustomUserDetails;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JWTUtility implements Serializable {

    private static final long serialVersionUID = 234234523523L;

//    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

    	CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        return Jwts.builder()
            .setSubject((userPrincipal.getUsername()))
            .setIssuedAt(new Date())
            .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(key(), SignatureAlgorithm.HS256)
            .compact();
      }
      
      private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
      }

      public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                   .parseClaimsJws(token).getBody().getSubject();
      }

      public boolean validateJwtToken(String authToken) {
        try {
          Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
          return true;
        } catch (MalformedJwtException e) {
          log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
          log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
          log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
          log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
      }
}