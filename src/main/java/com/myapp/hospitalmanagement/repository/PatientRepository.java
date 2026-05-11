package com.myapp.hospitalmanagement.repository;

import com.myapp.hospitalmanagement.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient>, PatientRepositoryCustom {
    Patient findByInsuranceId(Long id);

    @EntityGraph(attributePaths = {"insurance"})
    List<Patient> findAll();

    @EntityGraph(attributePaths = {"insurance"})
    Optional<Patient> findById(Long id);
}
