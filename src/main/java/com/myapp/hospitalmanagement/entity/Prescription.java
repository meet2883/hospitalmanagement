package com.myapp.hospitalmanagement.entity;

import com.myapp.hospitalmanagement.entity.enumaration.PrescriptionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@ToString
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "medical_record_id")
    @ToString.Exclude
    private MedicalRecord medicalRecord;

    private String medicineName;
    private String dosage;
    private String frequency;
    private String duration;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime prescribedAt;

    @Enumerated(EnumType.STRING)
    private PrescriptionStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
