package com.myapp.hospitalmanagement.entity.dto;

import com.myapp.hospitalmanagement.entity.enumaration.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentWithMedicalRecordDTO {
    private Long id;
    private LocalDateTime appointmentdatetime;
    private AppointmentStatus status;
    private PatientInfo patient;
    private DoctorInfo doctor;

    // Medical record status for frontend decision making
    private Boolean hasMedicalRecord;
    private MedicalRecordSummary medicalRecord;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientInfo {
        private Long id;
        private String patientName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorInfo {
        private Long id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicalRecordSummary {
        private Long id;
        private String diagnosis;
        private LocalDateTime createdAt;
    }
}
