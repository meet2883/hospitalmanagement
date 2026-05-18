package com.myapp.hospitalmanagement.controller;

import com.myapp.hospitalmanagement.entity.Patient;
import com.myapp.hospitalmanagement.entity.dto.PaginationResponseDTO;
import com.myapp.hospitalmanagement.entity.dto.PatientResponseDTO;
import com.myapp.hospitalmanagement.entity.dto.PatientUpdateDTO;
import com.myapp.hospitalmanagement.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.ApiResponse;

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
    public ResponseEntity<ApiResponse<PaginationResponseDTO<Patient>>> getAllPatient(
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        try {
            if (pageNum == null) pageNum = 0;
            if (pageSize == null) pageSize = 5;

            Page<Patient> patientList = patientService.getAllPatient(PageRequest.of(pageNum, pageSize));
            String message = patientList.isEmpty()
                                ? "No patients found"
                                : "Patient list fetched successfully";

            PaginationResponseDTO<Patient> response = PaginationResponseDTO.from(patientList);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, message, response)
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
    public ResponseEntity<ApiResponse<PaginationResponseDTO<PatientResponseDTO>>> filter(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String bloodgroup,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize
    ) {
        try {
            if (pageNum == null) pageNum = 0;
            if (pageSize == null) pageSize = 5;

            Page<PatientResponseDTO> patients =  patientService.filterPatients(
                    name,
                    phoneNumber,
                    gender,
                    bloodgroup,
                    PageRequest.of(pageNum, pageSize)
            );

            PaginationResponseDTO<PatientResponseDTO> pageData = PaginationResponseDTO.from(patients);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(
                            true,
                            "Filtered patients",
                            pageData
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }
}