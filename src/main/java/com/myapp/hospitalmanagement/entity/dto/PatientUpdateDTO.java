package com.myapp.hospitalmanagement.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private InsuranceInfo insurance;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InsuranceInfo {
        private Long id;
        private String policyName;
        private String policyProvider;
    }
}
