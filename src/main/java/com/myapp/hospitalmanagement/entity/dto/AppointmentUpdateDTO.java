package com.myapp.hospitalmanagement.entity.dto;

import com.myapp.hospitalmanagement.entity.enumaration.AppointmentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentUpdateDTO {
    private LocalDateTime appointmentdatetime;
    private AppointmentStatus status;
    private Long patientId;
    private Long doctorId;
}
