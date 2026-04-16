package com.myapp.hospitalmanagement.entity.dto;

import com.myapp.hospitalmanagement.entity.enumaration.Specialization;
import lombok.Data;

@Data
public class DoctorUpdateDTO {
    private String name;
    private String email;
    private Specialization specialization;
}
