package com.career.portal.services;

import com.career.portal.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token){
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject){
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public boolean validateToken(String token, UserDetails userDetails){
        log.debug("Validating JWT token for user: {}", userDetails.getUsername());
        try{
            final String username = extractUsername(token);
            log.debug("Token username: {}, UserDetails username: {}", username, userDetails.getUsername());
            boolean isUsernameValid = username.equals(userDetails.getUsername());
            boolean isTokenNotExpired = !isTokenExpired(token);

            log.debug("Username valid: {}", isUsernameValid);
            log.debug("Token not expired: {}", isTokenNotExpired);

            if (isTokenNotExpired) {
                Date expiration = extractExpiration(token);
                log.debug("Token expires at: {}", expiration);
                log.debug("Current time: {}", new Date());
            }

            boolean isValid = isUsernameValid && isTokenNotExpired;
            log.debug("Overall token validation result: {}", isValid);

            return isValid;
        }catch (Exception e){
            log.error("Error validating JWT token", e);
            return false;
        }
    }

}
