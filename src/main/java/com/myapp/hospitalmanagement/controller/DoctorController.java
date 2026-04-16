package com.myapp.hospitalmanagement.controller;

import com.myapp.hospitalmanagement.entity.Doctor;
import com.myapp.hospitalmanagement.entity.Patient;
import com.myapp.hospitalmanagement.entity.dto.DoctorUpdateDTO;
import com.myapp.hospitalmanagement.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.ApiResponse;

import javax.print.Doc;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {
    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Doctor>>> getAllDoctors() {
//        return doctorService.getAllDoctorList();
        try {
            List<Doctor> doctorsList = doctorService.getAllDoctorList();
            String message = doctorsList.isEmpty()
                    ? "No doctors found"
                    : "Doctors list fetched successfully";

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, message, doctorsList)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Doctor>> getDoctorById(@PathVariable Long id) {
//        return doctorService.getDoctorById(id);
        try {
            Doctor doctor = doctorService.getDoctorById(id)
                    .orElseThrow(() -> new RuntimeException("Doctor not found with this id"));

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Doctor fetched successfully", doctor)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<?>> createDoctor(@RequestBody Doctor doctor) {
//        return doctorService.createDoctor(doctor);
        try {
            Doctor res = doctorService.createDoctor(doctor);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(true, "Doctor created successfully.", res)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> updateRecord(@RequestBody DoctorUpdateDTO doctor, @PathVariable Long id) {
//        return doctorService.updateDoctor(doctor, id);
        try {
            Doctor updatedDoctor = doctorService.updateDoctor(doctor, id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Doctor updated successfully", updatedDoctor)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<?>> deleteDoctor(@PathVariable Long id) {
//        doctorService.deleteDoctor(id);
        try {
            doctorService.deleteDoctor(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Record deleted successfully", null)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }
}
