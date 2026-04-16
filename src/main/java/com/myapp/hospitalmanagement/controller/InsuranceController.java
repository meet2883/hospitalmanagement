package com.myapp.hospitalmanagement.controller;

import com.myapp.hospitalmanagement.entity.Insurance;
import com.myapp.hospitalmanagement.entity.Patient;
import com.myapp.hospitalmanagement.entity.dto.InsuranceUpdateDTO;
import com.myapp.hospitalmanagement.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utils.ApiResponse;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/insurance")
public class InsuranceController {
    private final InsuranceService insuranceService;

    @Autowired
    public InsuranceController(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Insurance>>> getAllInsuranceList() {
//        return insuranceService.getAllInsuranceList();
        try {
            List<Insurance> insuranceList = insuranceService.getAllInsuranceList();
            String message = insuranceList.isEmpty()
                    ? "No Insurance found"
                    : "Insurance list fetched successfully";

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, message, insuranceList)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Insurance>> getInsuranceById(@PathVariable Long id) {
//        return insuranceService.getInsuranceById(id);
        try {
            Insurance insurance = insuranceService.getInsuranceById(id)
                    .orElseThrow(() -> new RuntimeException("Insurance not found with this id"));

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Insurance fetched successfully", insurance)
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
    public ResponseEntity<ApiResponse<?>> createInsurance(@RequestBody Insurance insurance) {
//        return insuranceService.createInsuranceRecord(insurance);
        try {
            Insurance insuranceRes = insuranceService.createInsuranceRecord(insurance);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(true, "Insurance created successfully.", insuranceRes)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ApiResponse<>(false, e.getMessage(), null)
            );
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<?>> updateInsurance(@RequestBody InsuranceUpdateDTO insurance, @PathVariable Long id) {
//        return insuranceService.updateInsuranceDetails(insurance, id);
        try {
            Insurance updatedInsuranceDetails = insuranceService.updateInsuranceDetails(insurance, id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ApiResponse<>(true, "Insurance updated successfully", updatedInsuranceDetails)
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
    public ResponseEntity<ApiResponse<?>> remove(@PathVariable Long id) {
//        insuranceService.removeInsurance(id);
        try {
            insuranceService.removeInsurance(id);
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
