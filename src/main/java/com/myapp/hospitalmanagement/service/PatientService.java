package com.myapp.hospitalmanagement.service;

import com.myapp.hospitalmanagement.entity.Insurance;
import com.myapp.hospitalmanagement.entity.Patient;
import com.myapp.hospitalmanagement.entity.dto.PatientResponseDTO;
import com.myapp.hospitalmanagement.entity.dto.PatientUpdateDTO;
import com.myapp.hospitalmanagement.repository.InsuranceRepository;
import com.myapp.hospitalmanagement.repository.PatientRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
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
        if (dto.getInsurance().getId() != null) {
            Insurance insurance = insuranceRepository
                                    .findById(dto.getInsurance().getId())
                                    .orElseThrow(() -> new RuntimeException("Insurance not found with this Id."));
            patient.setInsurance(insurance);
        }
        return patientRepository.save(patient);
    }

    public void removePatient(Long id) {
        Patient existingPatient = patientRepository.findById(id).orElseThrow(() -> new RuntimeException("Patient not found"));
        patientRepository.delete(existingPatient);
    }

    public List<PatientResponseDTO> filterPatients(
            String name,
            String phoneNumber,
            String gender,
            String bloodgroup
    ) {
        Specification<Patient> specification = (root, query, cb) -> {
            root.fetch("insurance", jakarta.persistence.criteria.JoinType.LEFT);

            List<Predicate> predicates = new ArrayList<>();

            Optional.ofNullable(name).ifPresent(pName ->
                    predicates.add(cb.like(cb.lower(root.get("patientName")),
                            "%" + pName.toLowerCase() + "%"))
            );

            Optional.ofNullable(phoneNumber).ifPresent(pn ->
                    predicates.add(cb.like(cb.lower(root.get("phoneNumber")),
                            "%" + pn.toLowerCase() + "%"))
            );

            Optional.ofNullable(gender).ifPresent(pn ->
                    predicates.add(cb.like(cb.lower(root.get("gender")), pn.toLowerCase()))
            );

            Optional.ofNullable(bloodgroup).ifPresent(pn -> {
                String searchTerm = EscapeCharacter.DEFAULT.escape(bloodgroup);
                String pattern = "%" + searchTerm + "%";

                predicates.add(cb.equal(
                        cb.lower(root.get("bloodGroup")),
                        pattern.toLowerCase()
                ));
            });

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        List<Patient> patientList = patientRepository.findAll(specification);
        return patientList.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private PatientResponseDTO toResponseDTO(Patient patient) {
        PatientResponseDTO dto = new PatientResponseDTO();
        dto.setId(patient.getId());
        dto.setAge(patient.getAge());
        dto.setPatientName(patient.getPatientName());
        dto.setGender(patient.getGender());
        dto.setPhoneNumber(patient.getPhoneNumber());
        dto.setBloodGroup(patient.getBloodGroup());

        if (patient.getInsurance() != null) {
            PatientResponseDTO.InsuranceInfo insuranceInfo = new PatientResponseDTO.InsuranceInfo(
                    patient.getInsurance().getId(),
                    patient.getInsurance().getPolicyName()
            );
            dto.setInsurance(insuranceInfo);
        }

        return dto;
    }
}
