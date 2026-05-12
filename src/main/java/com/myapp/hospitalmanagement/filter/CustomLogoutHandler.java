package com.myapp.hospitalmanagement.filter;

import com.myapp.hospitalmanagement.service.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;

@Component
public class CustomLogoutHandler implements LogoutHandler {
    @Autowired
    private JWTService jwtService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, @Nullable Authentication authentication) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    String token = cookie.getValue();

                    Date tokenExpiryDate = jwtService.extractExpiration(token);
                    jwtService.blackListToken(token, tokenExpiryDate);

                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
    }
}
