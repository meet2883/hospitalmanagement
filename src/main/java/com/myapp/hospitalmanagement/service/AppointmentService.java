package com.myapp.hospitalmanagement.service;

import com.myapp.hospitalmanagement.entity.Appointment;
import com.myapp.hospitalmanagement.entity.Doctor;
import com.myapp.hospitalmanagement.entity.Patient;
import com.myapp.hospitalmanagement.entity.dto.AppointmentUpdateDTO;
import com.myapp.hospitalmanagement.repository.AppointmentRepository;
import com.myapp.hospitalmanagement.repository.DoctorRepository;
import com.myapp.hospitalmanagement.repository.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
}
