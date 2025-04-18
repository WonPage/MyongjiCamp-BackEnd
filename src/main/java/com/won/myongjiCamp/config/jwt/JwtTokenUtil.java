package com.won.myongjiCamp.config.jwt;

import com.won.myongjiCamp.config.security.auth.PrincipalDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
@Data
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration_time}")
    private Long expirationTime;

    @Value("${jwt.refresh_expiration_time}")
    private Long refreshExpirationTime;

    public JwtTokenUtil() {
    }

    public String generateRefreshToken(PrincipalDetail userDetails) {
        return doGenerateToken(userDetails.getUsername(), refreshExpirationTime, userDetails.getUserId());
    }

    public String generateToken(PrincipalDetail userDetails) {
        return doGenerateToken(userDetails.getUsername(), expirationTime, userDetails.getUserId());
    }

    private String doGenerateToken(String subject, Long expirationTime, Long userId) {
        Claims claims = Jwts.claims().setSubject(subject);
        claims.put("userId", userId);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }


    public Boolean validateToken(String token, PrincipalDetail userDetails) {
        return extractUsername(token).equals(userDetails.getUsername());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public Boolean isTokenExpired(String token) {
        try {
            Date expiredDate = extractExpiration(token);
            return expiredDate.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
