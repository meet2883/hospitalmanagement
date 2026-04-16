package com.myapp.hospitalmanagement.entity.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientUpdateDTO {
    private String patientName;
    private String gender;
    private String phoneNumber;
    private String bloodGroup;
    private String address;
    private Integer age;
    private LocalDate dateOfBirth;
    private Long insuranceId;
}
