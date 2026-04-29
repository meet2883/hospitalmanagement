package com.myapp.hospitalmanagement.controller;

import com.myapp.hospitalmanagement.entity.Appointment;
import com.myapp.hospitalmanagement.entity.User;
import com.myapp.hospitalmanagement.service.JWTService;
import com.myapp.hospitalmanagement.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.ApiResponse;


@RestController
@RequestMapping("/api/user")
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
                return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(true, "Logged in successfully", token)
                );
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
