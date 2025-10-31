package com.javaweb.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    
    private static final String SECRET_KEY = "Igf4IQ6BKCNvbT9fehqgHP1gpJZ2Bx1Qm4F6ZAUXEgQTcJTBKukpvYfpKL/0GQZv";
    //private static final String SCRET_KEY_SHORT = "Igf4IQ6BKCNvbT9fehqgHP1gpJZ2Bx1Qm4F6ZAUXEgQTcJTBKukpvYfpKL/0GQZv";
    
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    //private final SecretKey key_short = Keys.hmacShaKeyFor(SCRET_KEY_SHORT.getBytes(StandardCharsets.UTF_8));
    
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;// 1 giờ
    private final long EXPIRATION_TIME_SHORT = 1000 * 60 * 5; 
    
    private final long REFRESH_EXPIRATION_TIME = 1000L *  60 * 60 * 24 * 7;

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
  //Tạo refresh token
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTokenWithClaims(Map<String,Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject((String) claims.get("email"))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String generateRefreshClaims(Claims claim) {
    	return Jwts.builder()
    			.setClaims(claim)
    			.setSubject(SECRET_KEY)
    			.setIssuedAt(new Date())
    			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
    			.signWith(key, SignatureAlgorithm.HS256)
    			.compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)//Dũng để giải mã chứ kĩ, còn cái Jwt là chưa kí
                .getBody();
    }
    
    public String generateIdToken(String id) {
    	return Jwts.builder()
    			 .setSubject(id)
                 .setIssuedAt(new Date())
                 .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_SHORT))
                 .signWith(key, SignatureAlgorithm.HS256)
                 .compact();
    }
    
    public String extractIdToken(String ref) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(ref) 
                .getBody();
        return claims.getSubject();
    }

    public boolean isTokenExpired(String token) {
        try {
            Date exp = extractAllClaims(token).getExpiration();
            return exp.before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true;
        }
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Lấy email (subject) từ token
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject(); //getBody() có nghĩa là lấy từ Payload
        //Có thể lấy các object từ getHeader và getSignature nếu muốn
    }
    // Kiểm tra token hợp lệ
    public boolean validateToken(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }
    public SecretKey getKey() {
        return key;
    }
    public Claims parseToken(String token) {
        return extractAllClaims(token);
    }


}

