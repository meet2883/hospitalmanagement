package com.myapp.hospitalmanagement.repository;

import com.myapp.hospitalmanagement.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByInsuranceId(Long id);
}
