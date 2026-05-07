package com.myapp.hospitalmanagement.controller;

import com.myapp.hospitalmanagement.entity.Patient;
import com.myapp.hospitalmanagement.entity.dto.PatientResponseDTO;
import com.myapp.hospitalmanagement.entity.dto.PatientUpdateDTO;
import com.myapp.hospitalmanagement.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class PatientController {
    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createPatient(@RequestBody Patient patient) {
        try {
            Patient patientRes = patientService.createPatient(patient);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(true, "Patient created successfully.", patientRes)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Patient>>> getAllPatient() {
        try {
            List<Patient> patientList = patientService.getAllPatient();
            String message = patientList.isEmpty()
                                ? "No patients found"
                                : "Patient list fetched successfully";

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, message, patientList)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Patient>> getPatientById(@PathVariable Long id) {
        try {
            Patient patient = patientService.getPatientById(id)
                                            .orElseThrow(() -> new RuntimeException("Patient not found with this id"));

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Patient fetched successfully", patient)
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

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> updatePatient(
            @PathVariable Long id,
            @RequestBody PatientUpdateDTO patient
    ) {
        try {
            Patient updatedPatient = patientService.updatePatient(patient, id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Patient updated successfully", updatedPatient)
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
    public ResponseEntity<ApiResponse<?>> remove(@PathVariable Long id) {
        try {
            patientService.removePatient(id);
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

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<PatientResponseDTO>>> filter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNumber
    ) {
        try {
            List<PatientResponseDTO> patients = patientService.filterPatients(name, phoneNumber);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Filtered patients", patients)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }
}