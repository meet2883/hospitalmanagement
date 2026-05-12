package com.myapp.hospitalmanagement.service;

import com.myapp.hospitalmanagement.entity.User;
import com.myapp.hospitalmanagement.entity.UserPrincipal;
import com.myapp.hospitalmanagement.entity.dto.UserDTO;
import com.myapp.hospitalmanagement.entity.dto.UserRegistrationDTO;
import com.myapp.hospitalmanagement.entity.enumaration.Role;
import com.myapp.hospitalmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final DoctorService doctorService;

    @Autowired
    public MyUserDetailsService(
            UserRepository userRepository,
            JWTService jwtService,
            DoctorService doctorService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.doctorService = doctorService;
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

    @Transactional
    public User createUser(UserRegistrationDTO userDTO) {
        // Validate: Specialization is required when role is DOCTOR
        if (userDTO.getRole() == Role.DOCTOR && userDTO.getSpecialization() == null) {
            throw new IllegalArgumentException("Specialization is required when creating a doctor user");
        }

        // Create User entity from DTO
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());

        User savedUser = userRepository.save(user);

        // If role is DOCTOR, also create a Doctor profile with specialization
        if (savedUser.getRole() == Role.DOCTOR) {
            com.myapp.hospitalmanagement.entity.Doctor doctor = new com.myapp.hospitalmanagement.entity.Doctor();
            doctor.setName(savedUser.getName());
            doctor.setEmail(savedUser.getEmail());
            doctor.setSpecialization(userDTO.getSpecialization());
            doctor.setUser(savedUser);
            doctorService.createDoctor(doctor);
        }

        return savedUser;
    }

    public Page<UserDTO> filterUsers(PageRequest pageRequest, String email, String name, String role) {
        Specification<User> specification = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(name).ifPresent(username ->
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + username.toLowerCase() + "%"))
            );

            Optional.ofNullable(email).ifPresent(useremail ->
                    predicates.add(cb.like(cb.lower(root.get("email")), "%" + useremail.toLowerCase() + "%"))
            );

            Optional.ofNullable(role).ifPresent(userrole -> {
                predicates.add(cb.equal(root.get("role"), userrole));
            });

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<User> userList = userRepository.findAllUser(specification, pageRequest);
        return userList.map(this::toResponseDTO);
    }

    private UserDTO toResponseDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setRole(user.getRole());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
