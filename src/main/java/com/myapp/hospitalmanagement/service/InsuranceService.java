package com.myapp.hospitalmanagement.service;

import com.myapp.hospitalmanagement.entity.Insurance;
import com.myapp.hospitalmanagement.entity.Patient;
import com.myapp.hospitalmanagement.entity.dto.InsuranceUpdateDTO;
import com.myapp.hospitalmanagement.repository.InsuranceRepository;
import com.myapp.hospitalmanagement.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InsuranceService {
    private final InsuranceRepository insuranceRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public InsuranceService(
            InsuranceRepository insuranceRepository,
            PatientRepository patientRepository
    ) {
        this.insuranceRepository = insuranceRepository;
        this.patientRepository = patientRepository;
    }

    public Insurance createInsuranceRecord(Insurance insurance) {
        return insuranceRepository.save(insurance);
    }

    public List<Insurance> getAllInsuranceList() {
        return insuranceRepository.findAll();
    }

    public Optional<Insurance> getInsuranceById(Long id) {
        return insuranceRepository.findById(id);
    }

    public Insurance updateInsuranceDetails(InsuranceUpdateDTO insuranceDto, Long id) {
        Insurance insurance = insuranceRepository.findById(id).orElseThrow(() -> new RuntimeException("No insurance found with this id."));

        if (insuranceDto.getPolicyName() != null) {
            insurance.setPolicyName(insuranceDto.getPolicyName());
        }

        if (insuranceDto.getPolicyProvider() != null) {
            insurance.setPolicyProvider(insuranceDto.getPolicyProvider());
        }

        return insuranceRepository.save(insurance);
    }

    public void removeInsurance(Long id) {
        Insurance existingInsuranceDetails = insuranceRepository.findById(id).orElseThrow(() -> new RuntimeException("No insurance found with this id."));
        Patient patient = patientRepository.findByInsuranceId(existingInsuranceDetails.getId());
        if (patient != null) {
            patient.setInsurance(null);
            insuranceRepository.delete(existingInsuranceDetails);
        }
    }
}
