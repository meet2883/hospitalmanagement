package com.myapp.hospitalmanagement.service;

import com.myapp.hospitalmanagement.entity.Doctor;
import com.myapp.hospitalmanagement.entity.Patient;
import com.myapp.hospitalmanagement.entity.dto.DoctorUpdateDTO;
import com.myapp.hospitalmanagement.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

@Service
public class DoctorService {
    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public List<Doctor> getAllDoctorList() {
        return doctorRepository.findAll();
    }

    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }

    public Doctor updateDoctor(DoctorUpdateDTO doctorDto, Long id) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (doctorDto.getName() != null) {
            doctor.setName(doctorDto.getName());
        }

        if (doctorDto.getEmail() != null) {
            doctor.setEmail(doctorDto.getEmail());
        }

        if (doctorDto.getSpecialization() != null) {
            doctor.setSpecialization(doctorDto.getSpecialization());
        }
        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
        doctorRepository.delete(doctor);
    }
}
