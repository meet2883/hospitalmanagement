package com.myapp.hospitalmanagement.repository;

import com.myapp.hospitalmanagement.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PatientRepositoryCustom {
    Page<Patient> findAllWithInsurance(Specification<Patient> specification, Pageable pageable);
}
