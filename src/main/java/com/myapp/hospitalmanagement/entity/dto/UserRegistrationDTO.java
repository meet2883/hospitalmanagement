package com.myapp.hospitalmanagement.entity.dto;

import com.myapp.hospitalmanagement.entity.enumaration.Role;
import com.myapp.hospitalmanagement.entity.enumaration.Specialization;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String name;
    private String email;
    private String password;
    private Role role;
    private Specialization specialization; // Required when role is DOCTOR
}
