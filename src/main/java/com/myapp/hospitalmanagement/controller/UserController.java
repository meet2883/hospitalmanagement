package com.myapp.hospitalmanagement.controller;

import com.myapp.hospitalmanagement.entity.User;
import com.myapp.hospitalmanagement.entity.UserPrincipal;
import com.myapp.hospitalmanagement.entity.dto.UserDTO;
import com.myapp.hospitalmanagement.entity.dto.UserRegistrationDTO;
import com.myapp.hospitalmanagement.service.JWTService;
import com.myapp.hospitalmanagement.service.MyUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.*;
import utils.ApiResponse;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;


@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final MyUserDetailsService myUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final ApplicationContext applicationContext;

    @Autowired
    public UserController(
            AuthenticationManager authenticationManager,
            MyUserDetailsService myUserDetailsService,
            JWTService jwtService,
            ApplicationContext applicationContext
    ) {
        this.authenticationManager = authenticationManager;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtService = jwtService;
        this.applicationContext = applicationContext;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody UserRegistrationDTO userDTO) {
        try {
            User registerUser = myUserDetailsService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(true, "User created successfully.", registerUser)
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

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize
    ) {
        try {
            if (pageNum == null) pageNum = 0;
            if (pageSize == null) pageSize = 5;

            Page<UserDTO> users = myUserDetailsService.filterUsers(
                    PageRequest.of(pageNum, pageSize), email, name, role
            );

            return ResponseEntity.status(HttpStatus.OK).body(
              new ApiResponse<>(true, "Users data", users)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @PostMapping("/me")
    public ResponseEntity<ApiResponse<?>> verifyToken(HttpServletRequest request) {
        String token = extractTokenFromCookies(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                     new ApiResponse<>(false, "No token found", null)
            );
        }

        String username = jwtService.extractUsername(token);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, "Invalid token", null)
            );
        }

        UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(token, userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ApiResponse<>(false, "Token expired or invalid", null)
            );
        }

        UserDTO user = new UserDTO();
        if (userDetails instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) userDetails;
            user.setId(userPrincipal.getUser().getId());
            user.setName(userPrincipal.getUser().getName());
            user.setEmail(userPrincipal.getUser().getEmail());
            user.setRole(userPrincipal.getUser().getRole());
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse<>(true, "User retirived successfully", user)
        );
    }

    private String extractTokenFromCookies(HttpServletRequest request) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("auth_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
