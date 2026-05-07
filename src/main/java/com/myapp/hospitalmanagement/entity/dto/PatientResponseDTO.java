package com.myapp.hospitalmanagement.entity.dto;

import com.myapp.hospitalmanagement.entity.Insurance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientResponseDTO {
    private Long id;
    private String patientName;
    private String gender;
    private String phoneNumber;
    private String bloodGroup;
    private InsuranceInfo insurance;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InsuranceInfo {
        private Long id;
        private String policyName;
    }
}
