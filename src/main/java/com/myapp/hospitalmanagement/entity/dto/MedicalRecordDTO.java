package com.myapp.hospitalmanagement.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class MedicalRecordDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String remarks;
    private String keyPoints;
    private String diagnosis;
    private List<PrescriptionDTO> prescriptions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
