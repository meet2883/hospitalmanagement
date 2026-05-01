package com.myapp.hospitalmanagement.service;

import com.myapp.hospitalmanagement.entity.User;
import com.myapp.hospitalmanagement.entity.UserPrincipal;
import com.myapp.hospitalmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JWTService jwtService;

    @Autowired
    public MyUserDetailsService(
            UserRepository userRepository,
            JWTService jwtService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Try to find user by email first (new tokens)
        User user = userRepository.findByEmail(identifier);

        // If not found, try by name (for backward compatibility with old tokens)
        if (user == null) {
            user = userRepository.findByName(identifier);
        }

        if (user == null) {
            System.out.println("User not found with identifier: " + identifier);
            throw new UsernameNotFoundException("User not found");
        }
        return new UserPrincipal(user);
    }

    public User createUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
