package com.bikerental.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String userId, String phoneNumber, String role) {
        try {
            SecretKey key = getSigningKey();
            return Jwts.builder()
                    .subject(userId)
                    .claim("phoneNumber", phoneNumber)
                    .claim("role", role)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token", e);
            throw new JwtException("Error generating token");
        }
    }

    public String generateRefreshToken(String userId) {
        try {
            SecretKey key = getSigningKey();
            return Jwts.builder()
                    .subject(userId)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Error generating refresh token", e);
            throw new JwtException("Error generating refresh token");
        }
    }

    private Claims parseClaims(String token) {
        try {
            SecretKey key = getSigningKey();
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired", e);
            throw new JwtException("Token expired");
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported", e);
            throw new JwtException("Token unsupported");
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token", e);
            throw new JwtException("Invalid token");
        } catch (SignatureException e) {
            log.error("JWT signature validation failed", e);
            throw new JwtException("Invalid signature");
        } catch (IllegalArgumentException e) {
            log.error("JWT token is null or empty", e);
            throw new JwtException("Invalid token");
        }
    }

    public String getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getPhoneNumberFromToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.get("phoneNumber", String.class);
        } catch (JwtException e) {
            log.error("Error extracting phone number from token", e);
            return null;
        }
    }

    public String getRoleFromToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.get("role", String.class);
        } catch (JwtException e) {
            log.error("Error extracting role from token", e);
            return null;
        }
    }
}