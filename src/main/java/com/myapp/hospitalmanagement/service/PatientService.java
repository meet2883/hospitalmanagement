package com.myapp.hospitalmanagement.service;

import com.myapp.hospitalmanagement.entity.Insurance;
import com.myapp.hospitalmanagement.entity.Patient;
import com.myapp.hospitalmanagement.entity.dto.PatientUpdateDTO;
import com.myapp.hospitalmanagement.repository.InsuranceRepository;
import com.myapp.hospitalmanagement.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final InsuranceRepository insuranceRepository;

    @Autowired
    public PatientService(
            PatientRepository patientRepository,
            InsuranceRepository insuranceRepository,
            ObjectMapper objectMapper
    ) {
        this.patientRepository = patientRepository;
        this.insuranceRepository = insuranceRepository;
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatient() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Patient updatePatient(PatientUpdateDTO dto, Long id) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));

        if (dto.getPatientName() != null) {
            patient.setPatientName(dto.getPatientName());
        }
        if (dto.getGender() != null) {
            patient.setGender(dto.getGender());
        }
        if (dto.getPhoneNumber() != null) {
            patient.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getBloodGroup() != null) {
            patient.setBloodGroup(dto.getBloodGroup());
        }
        if (dto.getAddress() != null) {
            patient.setAddress(dto.getAddress());
        }
        if (dto.getAge() != null) {
            patient.setAge(dto.getAge());
        }
        if (dto.getDateOfBirth() != null) {
            patient.setDateOfBirth(dto.getDateOfBirth());
        }
        if (dto.getInsuranceId() != null) {
            Insurance insurance = insuranceRepository
                                    .findById(dto.getInsuranceId())
                                    .orElseThrow(() -> new RuntimeException("Insurance not found with this Id."));
            patient.setInsurance(insurance);
        }
        return patientRepository.save(patient);
    }

    public void removePatient(Long id) {
        Patient existingPatient = patientRepository.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));
        patientRepository.delete(existingPatient);
    }
}
