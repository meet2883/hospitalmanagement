package com.myapp.hospitalmanagement.service;

import com.myapp.hospitalmanagement.entity.Appointment;
import com.myapp.hospitalmanagement.entity.Doctor;
import com.myapp.hospitalmanagement.entity.Patient;
import com.myapp.hospitalmanagement.entity.dto.AppointmentUpdateDTO;
import com.myapp.hospitalmanagement.entity.enumaration.AppointmentStatus;
import com.myapp.hospitalmanagement.repository.AppointmentRepository;
import com.myapp.hospitalmanagement.repository.DoctorRepository;
import com.myapp.hospitalmanagement.repository.PatientRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Stream;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public Appointment createAppointment(Appointment appointment, Long patientId, Long doctorId) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));

        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        return appointmentRepository.save(appointment);
    }

    public List<Map<String, Object>> getAllAppoinments() {
        return appointmentRepository.getAllAppoinments();
    }

    public Appointment updateAppointmentStatus(AppointmentUpdateDTO appointmentDto, Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                                                        .orElseThrow(() -> new RuntimeException("No appointment found"));

        if (appointmentDto.getAppointmentdatetime() != null) {
            appointment.setAppointmentdatetime(appointmentDto.getAppointmentdatetime());
        }

        if (appointmentDto.getStatus() != null) {
            appointment.setStatus(appointmentDto.getStatus());
        }

        if (appointmentDto.getPatientId() != null) {
            Patient patient = patientRepository.findById(appointmentDto.getPatientId())
                                                .orElseThrow(() -> new RuntimeException("Patient not found."));
            appointment.setPatient(patient);
        }

        if (appointmentDto.getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(appointmentDto.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found."));
            appointment.setDoctor(doctor);
        }
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
        appointmentRepository.delete(appointment);
    }

    public List<Appointment> getAppointmentByDoctor(Long id) {
        return appointmentRepository.findByDoctorId(id);
    }

    public List<com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO> filterAppointments(
            String date, String status, Long patientId, String patientName, Long doctorId, String doctorName
    ) {
        Specification<Appointment> specification = (root, query, cb) -> {
            // Fetch patient and doctor to avoid lazy loading issues
            root.fetch("patient", jakarta.persistence.criteria.JoinType.LEFT);
            root.fetch("doctor", jakarta.persistence.criteria.JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(date).ifPresent(d -> {
                LocalDate appointmentDate = parseDate(d);

                LocalDateTime startOfDay = appointmentDate.atStartOfDay();
                LocalDateTime endOfDay = appointmentDate.atTime(LocalTime.MAX);
                predicates.add(cb.between(root.get("appointmentdatetime"), startOfDay, endOfDay));
            });

            Optional.ofNullable(status).ifPresent(s -> {
                AppointmentStatus parsedStatus = parseStatus(s);
                predicates.add(cb.equal(root.get("status"), parsedStatus));
            });

            Optional.ofNullable(patientId).ifPresent(pid ->
                predicates.add(cb.equal(root.join("patient").get("id"), pid))
            );

            Optional.ofNullable(patientName).ifPresent(pName ->
                predicates.add(cb.like(cb.lower(root.join("patient").get("patientName")),
                        "%" + pName.toLowerCase() + "%"))
            );

            Optional.ofNullable(doctorId).ifPresent(did ->
                predicates.add(cb.equal(root.join("doctor").get("id"), did))
            );

            Optional.ofNullable(doctorName).ifPresent(dName ->
                predicates.add(cb.like(cb.lower(root.join("doctor").get("name")),
                        "%" + dName.toLowerCase() + "%"))
            );

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
       List<Appointment> appointments = appointmentRepository.findAll(specification);
       return appointments.stream()
               .map(this::toResponseDTO)
               .toList();
    }

    private com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO toResponseDTO(Appointment appointment) {
        com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO dto = new com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO();
        dto.setId(appointment.getId());
        dto.setAppointmentdatetime(appointment.getAppointmentdatetime());
        dto.setStatus(appointment.getStatus());

        if (appointment.getPatient() != null) {
            com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO.PatientInfo patientInfo =
                new com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO.PatientInfo(
                    appointment.getPatient().getId(),
                    appointment.getPatient().getPatientName()
                );
            dto.setPatient(patientInfo);
        }

        if (appointment.getDoctor() != null) {
            com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO.DoctorInfo doctorInfo =
                new com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO.DoctorInfo(
                    appointment.getDoctor().getId(),
                    appointment.getDoctor().getName()
                );
            dto.setDoctor(doctorInfo);
        }

        return dto;
    }

    private AppointmentStatus parseStatus(String status) {
        if (status == null) return null;

        try {
            // Try as enum name first (SCHEDULE, CANCEL, DONE)
            return AppointmentStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            try {
                // Try as ordinal (0, 1, 2)
                int ordinal = Integer.parseInt(status);
                AppointmentStatus[] values = AppointmentStatus.values();
                if (ordinal >= 0 && ordinal < values.length) {
                    return values[ordinal];
                }
                throw new IllegalArgumentException("Invalid status ordinal: " + status);
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Invalid status. Use: SCHEDULE, CANCEL, DONE or 0, 1, 2");
            }
        }
    }

    private LocalDate parseDate(String date) {
        // Try multiple date formats
        List<DateTimeFormatter> formatters = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),   // ISO format: 2026-05-10
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),   // Slash format: 2026/05/10
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),   // US format: 05/10/2026
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),   // Day first: 10-05-2026
            DateTimeFormatter.ofPattern("dd/MM/yyyy")    // Slash day first: 10/05/2026
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
                // Try next format
            }
        }

        throw new IllegalArgumentException("Invalid date format. Supported formats: yyyy-MM-dd, yyyy/MM/dd, MM/dd/yyyy, dd-MM-yyyy, dd/MM/yyyy");
    }
}
