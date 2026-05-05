package com.myapp.hospitalmanagement.controller;

import com.myapp.hospitalmanagement.entity.User;
import com.myapp.hospitalmanagement.entity.UserPrincipal;
import com.myapp.hospitalmanagement.entity.dto.UserDTO;
import com.myapp.hospitalmanagement.service.JWTService;
import com.myapp.hospitalmanagement.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.ApiResponse;

import java.time.Duration;
import java.util.HashMap;


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
    public ResponseEntity<ApiResponse<UserDTO>> login(@RequestBody User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(user.getEmail());

                UserDetails userData = myUserDetailsService.loadUserByUsername(user.getEmail());

                UserDTO data = new UserDTO();
                data.setEmail(user.getEmail());

                // Get the actual name from UserPrincipal if possible
                if (userData instanceof UserPrincipal) {
                    UserPrincipal userPrincipal = (UserPrincipal) userData;
                    data.setName(userPrincipal.getUser().getName());
                    data.setRole(userPrincipal.getUser().getRole());
                    data.setId(userPrincipal.getUser().getId());
                }

                ApiResponse<UserDTO> response = new ApiResponse<>(true, "Logged in successfully", data);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token);

                ResponseCookie cookie = ResponseCookie.from("auth_token", token)
                        .httpOnly(true)
                        .secure(false)  // Set to false for HTTP (localhost), change to true for HTTPS production
                        .path("/")
                        .maxAge(Duration.ofDays(1))
                        .sameSite("Lax")  // Changed from Strict to Lax for better cross-site behavior
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
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, "Invalid email or password", null)
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, "Authentication failed", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }
}
