package com.myapp.hospitalmanagement.controller;

import com.myapp.hospitalmanagement.entity.Appointment;
import com.myapp.hospitalmanagement.entity.Patient;
import com.myapp.hospitalmanagement.entity.dto.AppointmentUpdateDTO;
import com.myapp.hospitalmanagement.entity.dto.AppointmentWithMedicalRecordDTO;
import com.myapp.hospitalmanagement.service.AppointmentService;
import com.myapp.hospitalmanagement.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.ApiResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final MedicalRecordService medicalRecordService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, MedicalRecordService medicalRecordService) {
        this.appointmentService = appointmentService;
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllAppointment() {
        try {
            List<Map<String, Object>> appointmentList = appointmentService.getAllAppoinments();
            String message = appointmentList.isEmpty()
                    ? "No Appointments found"
                    : "Appointment list fetched successfully";

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, message, appointmentList)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @PostMapping("/create/{pId}/{dId}")
    public ResponseEntity<ApiResponse<com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO>> scheduleAppointment(
            @RequestBody Appointment appointment,
            @PathVariable Long pId,
            @PathVariable Long dId
    ) {
        try {
            com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO res = appointmentService.createAppointmentDTO(appointment, pId, dId);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(true, "Appointment created successfully.", res)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO>> updateAppointmetn(
            @PathVariable Long id,
            @RequestBody AppointmentUpdateDTO appointment
    ) {
        try {
            com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO updatedAppointment = appointmentService.updateAppointmentDTO(appointment, id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Appointment updated successfully", updatedAppointment)
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

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteAppointment(@PathVariable Long id) {
//        appointmentService.deleteAppointment(id);
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Appointment deleted successfully", null)
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

    @GetMapping("/get-appointment-by-doctor/{id}")
    public ResponseEntity<ApiResponse<List<com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO>>> getAppointmentByDoctor(@PathVariable Long id) {
        try {
            List<com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO> appointmentList = appointmentService.getAppointmentsDTOByDoctor(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Appointment list", appointmentList)
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

    /**
     * Get doctor's appointments with medical record status
     * This is the key endpoint for the appointment list page
     */
    @GetMapping("/doctor/{doctorId}/with-medical-status")
    public ResponseEntity<ApiResponse<List<AppointmentWithMedicalRecordDTO>>> getAppointmentsWithMedicalStatus(
            @PathVariable Long doctorId
    ) {
        try {
            List<AppointmentWithMedicalRecordDTO> appointments =
                    medicalRecordService.getAppointmentsWithMedicalStatus(doctorId);

            String message = appointments.isEmpty()
                    ? "No appointments found for this doctor"
                    : "Appointments with medical status fetched successfully";

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, message, appointments)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO>>> filterAppointments(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String doctorName
    ) {
        try {
            List<com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO> appointments = appointmentService.filterAppointments(
                    date, status, patientId, patientName, doctorId, doctorName, type
            );
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Filtered appointments", appointments)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO>> getAppointmentById(@PathVariable Long id) {
        try {
            com.myapp.hospitalmanagement.entity.dto.AppointmentResponseDTO appointment = appointmentService.getAppointmentDTOById(id);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Appointment fetched successfully", appointment)
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
