package com.myapp.hospitalmanagement.entity;

import com.myapp.hospitalmanagement.entity.enumaration.AppointmentStatus;
import com.myapp.hospitalmanagement.entity.enumaration.AppointmentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@ToString(exclude = {"medicalRecord", "patient", "doctor"})
@Getter
@Setter
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDateTime appointmentdatetime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Enumerated(EnumType.STRING)
    private AppointmentType type;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private MedicalRecord medicalRecord;
}
