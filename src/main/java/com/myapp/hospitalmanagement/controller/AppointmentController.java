package com.myapp.hospitalmanagement.controller;

import com.myapp.hospitalmanagement.entity.Appointment;
import com.myapp.hospitalmanagement.entity.dto.AppointmentUpdateDTO;
import com.myapp.hospitalmanagement.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.ApiResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
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
    public ResponseEntity<ApiResponse<?>> scheduleAppointment(
            @RequestBody Appointment appointment,
            @PathVariable Long pId,
            @PathVariable Long dId
    ) {
        try {
            Appointment res = appointmentService.createAppointment(appointment, pId, dId);
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
    public ResponseEntity<ApiResponse<?>> updateAppointmetn(
            @PathVariable Long id,
            @RequestBody AppointmentUpdateDTO appointment
    ) {
//        return appointmentService.updateAppointmentStatus(appointment, id);
        try {
            Appointment updatedAppointment = appointmentService.updateAppointmentStatus(appointment, id);
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
    public ResponseEntity<ApiResponse<List<Appointment>>> getAppointmentByDoctor(@PathVariable Long id) {
        try {
            List<Appointment> appointmentList = appointmentService.getAppointmentByDoctor(id);
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
}
