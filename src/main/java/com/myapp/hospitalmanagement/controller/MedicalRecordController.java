package com.myapp.hospitalmanagement.controller;

import com.myapp.hospitalmanagement.entity.MedicalRecord;
import com.myapp.hospitalmanagement.entity.dto.AppointmentWithMedicalRecordDTO;
import com.myapp.hospitalmanagement.entity.dto.MedicalRecordDTO;
import com.myapp.hospitalmanagement.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/medical-record")
public class MedicalRecordController {
    @Autowired
    private MedicalRecordService medicalRecordService;

    /**
     * Create a new medical record for an appointment
     * POST /api/medical-record/appointment/{appointmentId}
     *
     * Request Body:
     * {
     *   "diagnosis": "string",
     *   "remarks": "string",
     *   "keypoints": "string",
     *   "prescriptions": [
     *     {
     *       "medicineName": "string",
     *       "dosage": "string",
     *       "frequency": "string",
     *       "duration": "string",
     *       "instructions": "string",
     *       "status": "ACTIVE",
     *       "notes": "string"
     *     }
     *   ]
     * }
     */
    @PostMapping("/appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<MedicalRecordDTO>> createForAppointment(
            @RequestBody MedicalRecord record,
            @PathVariable Long appointmentId
    ) {
        try {
            MedicalRecordDTO createdRecord = medicalRecordService.createRecordForAppointment(record, appointmentId);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(true, "Medical record created successfully.", createdRecord)
            );
        } catch (RuntimeException e) {
            System.err.println("Error creating medical record: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(false, "Failed to create medical record: " + e.getMessage(), null)
            );
        } catch (Exception e) {
            System.err.println("Unexpected error creating medical record: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "An unexpected error occurred: " + e.getMessage(), null)
            );
        }
    }

    /**
     * Update an existing medical record
     * PUT /api/medical-record/{recordId}
     */
    @PutMapping("/{recordId}")
    public ResponseEntity<ApiResponse<MedicalRecordDTO>> update(
            @PathVariable Long recordId,
            @RequestBody MedicalRecord record
    ) {
        try {
            MedicalRecordDTO updatedRecord = medicalRecordService.updateMedicalRecord(recordId, record);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Medical record updated successfully.", updatedRecord)
            );
        } catch (RuntimeException e) {
            System.err.println("Error updating medical record: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(false, "Failed to update medical record: " + e.getMessage(), null)
            );
        } catch (Exception e) {
            System.err.println("Unexpected error updating medical record: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "An unexpected error occurred: " + e.getMessage(), null)
            );
        }
    }

    /**
     * Get medical record by appointment ID
     * GET /api/medical-record/appointment/{appointmentId}
     */
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<MedicalRecordDTO>> getByAppointmentId(
            @PathVariable Long appointmentId
    ) {
        try {
            MedicalRecordDTO record = medicalRecordService.getMedicalRecordByAppointmentId(appointmentId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Medical record fetched successfully.", record)
            );
        } catch (RuntimeException e) {
            System.err.println("Error fetching medical record: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "An unexpected error occurred: " + e.getMessage(), null)
            );
        }
    }

    /**
     * Get patient's complete medical history
     * GET /api/medical-record/patient/{patientId}/history
     */
    @GetMapping("/patient/{patientId}/history")
    public ResponseEntity<ApiResponse<List<MedicalRecordDTO>>> getPatientHistory(
            @PathVariable Long patientId
    ) {
        try {
            List<MedicalRecordDTO> history = medicalRecordService.getPatientMedicalHistory(patientId);
            String message = history.isEmpty()
                    ? "No medical records found for this patient"
                    : "Medical history fetched successfully (" + history.size() + " records)";

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, message, history)
            );
        } catch (RuntimeException e) {
            System.err.println("Error fetching patient history: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "An unexpected error occurred: " + e.getMessage(), null)
            );
        }
    }

    /**
     * Get doctor's appointments with medical record status
     * GET /api/medical-record/doctor/{doctorId}/appointments-with-status
     */
    @GetMapping("/doctor/{doctorId}/appointments-with-status")
    public ResponseEntity<ApiResponse<List<AppointmentWithMedicalRecordDTO>>> getAppointmentsWithStatus(
            @PathVariable Long doctorId
    ) {
        try {
            List<AppointmentWithMedicalRecordDTO> appointments =
                    medicalRecordService.getAppointmentsWithMedicalStatus(doctorId);

            String message = appointments.isEmpty()
                    ? "No appointments found for this doctor"
                    : "Appointments with medical status fetched successfully (" + appointments.size() + " appointments)";

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, message, appointments)
            );
        } catch (RuntimeException e) {
            System.err.println("Error fetching appointments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "An unexpected error occurred: " + e.getMessage(), null)
            );
        }
    }

    /**
     * Check if appointment has a medical record
     * GET /api/medical-record/appointment/{appointmentId}/exists
     */
    @GetMapping("/appointment/{appointmentId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> hasMedicalRecord(
            @PathVariable Long appointmentId
    ) {
        try {
            boolean hasRecord = medicalRecordService.hasMedicalRecord(appointmentId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Check completed successfully.", hasRecord)
            );
        } catch (Exception e) {
            System.err.println("Error checking medical record: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null)
            );
        }
    }
}
