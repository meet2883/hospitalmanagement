package com.myapp.hospitalmanagement.controller;

import com.myapp.hospitalmanagement.entity.MedicalRecord;
import com.myapp.hospitalmanagement.entity.dto.MedicalRecordDTO;
import com.myapp.hospitalmanagement.entity.dto.PrescriptionDTO;
import com.myapp.hospitalmanagement.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;
import utils.ApiResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/medical-record")
public class MedicalRecordController {
    @Autowired
    private MedicalRecordService medicalRecordService;

    @PostMapping("/create/{pId}/{dId}")
    public ResponseEntity<ApiResponse<MedicalRecordDTO>> create(
            @RequestBody MedicalRecord record,
            @PathVariable Long pId,
            @PathVariable Long dId
    ) {
        try {
            MedicalRecordDTO createdRecord = medicalRecordService.createRecord(record, pId, dId);

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(true, "Medical record created successfully.", createdRecord)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }
}
