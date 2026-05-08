package com.myapp.hospitalmanagement.service;

import com.myapp.hospitalmanagement.entity.*;
import com.myapp.hospitalmanagement.entity.dto.AppointmentWithMedicalRecordDTO;
import com.myapp.hospitalmanagement.entity.dto.MedicalRecordDTO;
import com.myapp.hospitalmanagement.entity.dto.PrescriptionDTO;
import com.myapp.hospitalmanagement.entity.enumaration.PrescriptionStatus;
import com.myapp.hospitalmanagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;

    @Autowired
    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            PrescriptionRepository prescriptionRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /**
     * Create a new medical record for an appointment
     */
    @Transactional
    public MedicalRecordDTO createRecordForAppointment(MedicalRecord request, Long appointmentId) {
        try {
            // Step 1: Validate and fetch appointment
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId));

            // Step 2: Check for existing medical record
            if (medicalRecordRepository.existsByAppointmentId(appointmentId)) {
                throw new RuntimeException("Medical record already exists for appointment ID: " + appointmentId + ". Use update instead.");
            }

            // Step 3: Create medical record
            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setAppointment(appointment);
            medicalRecord.setPatient(appointment.getPatient());
            medicalRecord.setDoctor(appointment.getDoctor());
            medicalRecord.setRemarks(request.getRemarks());
            medicalRecord.setKeypoints(request.getKeypoints());
            medicalRecord.setDiagnosis(request.getDiagnosis());

            // Step 4: Save medical record first (without prescriptions)
            MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
            System.out.println("Saved medical record with ID: " + savedRecord.getId());

            // Step 5: Handle prescriptions separately
            if (request.getPrescriptions() != null && !request.getPrescriptions().isEmpty()) {
                List<Prescription> savedPrescriptions = new ArrayList<>();
                for (Prescription preRequest : request.getPrescriptions()) {
                    Prescription prescription = new Prescription();
                    prescription.setMedicalRecord(savedRecord);
                    prescription.setMedicineName(preRequest.getMedicineName());
                    prescription.setDosage(preRequest.getDosage());
                    prescription.setFrequency(preRequest.getFrequency());
                    prescription.setDuration(preRequest.getDuration());
                    prescription.setInstructions(preRequest.getInstructions());
                    // Set default status if null
                    prescription.setStatus(preRequest.getStatus() != null ? preRequest.getStatus() : PrescriptionStatus.ACTIVE);
                    prescription.setNotes(preRequest.getNotes());

                    // Save each prescription
                    Prescription savedPrescription = prescriptionRepository.save(prescription);
                    savedPrescriptions.add(savedPrescription);
                    System.out.println("Saved prescription: " + savedPrescription.getId());
                }
                savedRecord.setPrescriptions(savedPrescriptions);
            }

            // Step 6: Refresh and return
            MedicalRecord finalRecord = medicalRecordRepository.findById(savedRecord.getId()).orElse(savedRecord);
            return mapToDTO(finalRecord);

        } catch (RuntimeException e) {
            System.err.println("Error creating medical record: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error creating medical record: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create medical record: " + e.getMessage(), e);
        }
    }

    /**
     * Update an existing medical record
     */
    @Transactional
    public MedicalRecordDTO updateMedicalRecord(Long recordId, MedicalRecord request) {
        try {
            MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId)
                    .orElseThrow(() -> new RuntimeException("Medical record not found with ID: " + recordId));

            medicalRecord.setRemarks(request.getRemarks());
            medicalRecord.setKeypoints(request.getKeypoints());
            medicalRecord.setDiagnosis(request.getDiagnosis());

            // Update prescriptions - modify the existing collection
            if (request.getPrescriptions() != null) {
                // Clear existing prescriptions (orphanRemoval will delete them from DB)
                medicalRecord.getPrescriptions().clear();

                // Add new prescriptions to the SAME collection
                for (Prescription preRequest : request.getPrescriptions()) {
                    Prescription prescription = new Prescription();
                    prescription.setMedicalRecord(medicalRecord);
                    prescription.setMedicineName(preRequest.getMedicineName());
                    prescription.setDosage(preRequest.getDosage());
                    prescription.setFrequency(preRequest.getFrequency());
                    prescription.setDuration(preRequest.getDuration());
                    prescription.setInstructions(preRequest.getInstructions());
                    prescription.setStatus(preRequest.getStatus() != null ? preRequest.getStatus() : PrescriptionStatus.ACTIVE);
                    prescription.setNotes(preRequest.getNotes());

                    // Add to the existing collection (don't create a new one)
                    medicalRecord.getPrescriptions().add(prescription);
                }
            }

            // Save once - cascade will handle the prescriptions
            MedicalRecord updatedRecord = medicalRecordRepository.save(medicalRecord);
            return mapToDTO(updatedRecord);

        } catch (RuntimeException e) {
            System.err.println("Error updating medical record: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error updating medical record: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update medical record: " + e.getMessage(), e);
        }
    }

    /**
     * Get medical record by appointment ID
     */
    public MedicalRecordDTO getMedicalRecordByAppointmentId(Long appointmentId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("No medical record found for appointment ID: " + appointmentId));
        return mapToDTO(medicalRecord);
    }

    /**
     * Get patient's complete medical history
     */
    public List<MedicalRecordDTO> getPatientMedicalHistory(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));

        List<MedicalRecord> records = medicalRecordRepository
                .findByPatientIdOrderByCreatedAtDesc(patientId);

        return records.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get appointments with medical record status for a doctor
     */
    public List<AppointmentWithMedicalRecordDTO> getAppointmentsWithMedicalStatus(Long doctorId) {
        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdWithMedicalRecord(doctorId);

        // Get all appointment IDs
        List<Long> appointmentIds = appointments.stream()
                .map(Appointment::getId)
                .collect(Collectors.toList());

        // Fetch all medical records for these appointments
        List<MedicalRecord> medicalRecords = appointmentIds.isEmpty()
                ? List.of()
                : medicalRecordRepository.findAllByAppointmentIds(appointmentIds);

        // Create a map for quick lookup
        Map<Long, MedicalRecord> recordMap = medicalRecords.stream()
                .collect(Collectors.toMap(
                        mr -> mr.getAppointment().getId(),
                        mr -> mr
                ));

        return appointments.stream()
                .map(apt -> mapToAppointmentWithMedicalDTO(apt, recordMap.get(apt.getId())))
                .collect(Collectors.toList());
    }

    /**
     * Check if appointment has a medical record
     */
    public boolean hasMedicalRecord(Long appointmentId) {
        return medicalRecordRepository.existsByAppointmentId(appointmentId);
    }

    // Helper methods

    private MedicalRecordDTO mapToDTO(MedicalRecord record) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(record.getId());
        dto.setAppointmentId(record.getAppointment().getId());
        dto.setAppointmentDateTime(record.getAppointment().getAppointmentdatetime());
        dto.setPatientId(record.getPatient().getId());
        dto.setPatientName(record.getPatient().getPatientName());
        dto.setDoctorId(record.getDoctor().getId());
        dto.setDoctorName(record.getDoctor().getName());
        dto.setRemarks(record.getRemarks());
        dto.setKeyPoints(record.getKeypoints());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setCreatedAt(record.getCreatedAt());
        dto.setUpdatedAt(record.getUpdatedAt());

        if (record.getPrescriptions() != null && !record.getPrescriptions().isEmpty()) {
            List<PrescriptionDTO> prescriptionDTOList = record.getPrescriptions()
                    .stream()
                    .map(this::mapPrescriptionToDTO)
                    .collect(Collectors.toList());
            dto.setPrescriptions(prescriptionDTOList);
        }

        return dto;
    }

    private PrescriptionDTO mapPrescriptionToDTO(Prescription p) {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId(p.getId());
        dto.setMedicineName(p.getMedicineName());
        dto.setDosage(p.getDosage());
        dto.setStatus(p.getStatus());
        dto.setPrescribedAt(p.getPrescribedAt());
        dto.setDuration(p.getDuration());
        dto.setFrequency(p.getFrequency());
        dto.setInstructions(p.getInstructions());
        dto.setNotes(p.getNotes());
        return dto;
    }

    private AppointmentWithMedicalRecordDTO mapToAppointmentWithMedicalDTO(
            Appointment apt, MedicalRecord medicalRecord) {

        AppointmentWithMedicalRecordDTO dto = new AppointmentWithMedicalRecordDTO();
        dto.setId(apt.getId());
        dto.setAppointmentdatetime(apt.getAppointmentdatetime());
        dto.setStatus(apt.getStatus());

        // Patient info
        AppointmentWithMedicalRecordDTO.PatientInfo patientInfo =
                new AppointmentWithMedicalRecordDTO.PatientInfo(
                        apt.getPatient().getId(),
                        apt.getPatient().getPatientName()
                );
        dto.setPatient(patientInfo);

        // Doctor info
        AppointmentWithMedicalRecordDTO.DoctorInfo doctorInfo =
                new AppointmentWithMedicalRecordDTO.DoctorInfo(
                        apt.getDoctor().getId(),
                        apt.getDoctor().getName()
                );
        dto.setDoctor(doctorInfo);

        // Medical record status
        dto.setHasMedicalRecord(medicalRecord != null);
        if (medicalRecord != null) {
            AppointmentWithMedicalRecordDTO.MedicalRecordSummary summary =
                    new AppointmentWithMedicalRecordDTO.MedicalRecordSummary(
                            medicalRecord.getId(),
                            medicalRecord.getDiagnosis(),
                            medicalRecord.getCreatedAt()
                    );
            dto.setMedicalRecord(summary);
        }

        return dto;
    }
}
