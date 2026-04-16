package com.myapp.hospitalmanagement.repository;

import com.myapp.hospitalmanagement.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
