package com.myapp.hospitalmanagement.service;

import com.myapp.hospitalmanagement.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    @Autowired
    private MyUserDetailsService myUserDetailsService;

    private SecretKey key = null;

    private final Map<String, Date> blacklistTokens = new HashMap<>();

    public JWTService() {
            key = Jwts.SIG.HS256.key().build();
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts
                .builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        String username = userDetails.getUsername();
        return (username.equals(extractUsername(token)) && !isTokenExpire(token));
    }

    public boolean isTokenExpire(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey() {
        return this.key;
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistTokens.containsKey(token) ? true : false;
    }

    public boolean blackListToken(String token, Date date) {
        blacklistTokens.put(token, date);
        return true;
    }
}
