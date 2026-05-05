package com.myapp.hospitalmanagement.repository;

import com.myapp.hospitalmanagement.entity.Prescription;
import org.hibernate.boot.models.JpaAnnotations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
}
