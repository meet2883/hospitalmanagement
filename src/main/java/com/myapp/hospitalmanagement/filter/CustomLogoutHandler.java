package com.myapp.hospitalmanagement.filter;

import com.myapp.hospitalmanagement.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CustomLogoutHandler implements LogoutHandler {
    @Autowired
    private JWTService jwtService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) {
        String token = null;
        String authToken = request.getHeader("Authorization");

        if (authToken != null && authToken.startsWith("Bearer ")) {
            token = authToken.substring(7);
        }

        if (authToken != "") {
            Date tokenExpiryDate = jwtService.extractExpiration(token);
            jwtService.blackListToken(token, tokenExpiryDate);
        }
    }
}
