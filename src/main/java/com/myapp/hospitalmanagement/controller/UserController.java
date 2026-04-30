package com.myapp.hospitalmanagement.controller;

import com.myapp.hospitalmanagement.entity.User;
import com.myapp.hospitalmanagement.service.JWTService;
import com.myapp.hospitalmanagement.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.ApiResponse;

import java.time.Duration;


@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final MyUserDetailsService myUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Autowired
    public UserController(
            AuthenticationManager authenticationManager,
            MyUserDetailsService myUserDetailsService,
            JWTService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody User user) {
        try {
            User registerUser = myUserDetailsService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(true, "User created successfully.", user)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword())
            );
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(user.getName());

                ApiResponse response = new ApiResponse<>(true, "Logged in successfully", null);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token);

                ResponseCookie cookie = ResponseCookie.from("auth_token", token)
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(Duration.ofDays(1))
                        .sameSite("Strict")
                        .build();
                headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .headers(headers)
                        .body(response);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, "Logged in failed", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }
}
