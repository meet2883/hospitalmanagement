package com.myapp.hospitalmanagement.service;

import com.myapp.hospitalmanagement.entity.User;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTService {
    private SecretKey key = null;

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
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }
}
