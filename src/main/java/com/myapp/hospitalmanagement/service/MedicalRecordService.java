package com.myapp.hospitalmanagement.service;

import com.myapp.hospitalmanagement.entity.Doctor;
import com.myapp.hospitalmanagement.entity.MedicalRecord;
import com.myapp.hospitalmanagement.entity.Patient;
import com.myapp.hospitalmanagement.entity.Prescription;
import com.myapp.hospitalmanagement.entity.dto.MedicalRecordDTO;
import com.myapp.hospitalmanagement.entity.dto.PrescriptionDTO;
import com.myapp.hospitalmanagement.repository.DoctorRepository;
import com.myapp.hospitalmanagement.repository.MedicalRecordRepository;
import com.myapp.hospitalmanagement.repository.PatientRepository;
import com.myapp.hospitalmanagement.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            PrescriptionRepository prescriptionRepository
    ) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    public MedicalRecordDTO createRecord(MedicalRecord request, Long patientId, Long doctorId) {
        Patient patient = patientRepository.findById(patientId).orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor not found"));

        System.out.println("Medical record request ::::: {}" + request.toString());

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setPatient(patient);
        medicalRecord.setDoctor(doctor);
        medicalRecord.setRemarks(request.getRemarks());
        medicalRecord.setKeypoints(request.getKeypoints());
        medicalRecord.setDiagnosis(request.getDiagnosis());

        if (request.getPrescriptions() != null && !request.getPrescriptions().isEmpty()) {
            List<Prescription> plist = new ArrayList<>();
            for (Prescription preRequest : request.getPrescriptions()) {
                Prescription prescription = new Prescription();
                prescription.setMedicineName(preRequest.getMedicineName());
                prescription.setDosage(preRequest.getDosage());
                prescription.setFrequency(preRequest.getFrequency());
                prescription.setDuration(preRequest.getDuration());
                prescription.setInstructions(preRequest.getInstructions());
                prescription.setStatus(preRequest.getStatus());
                prescription.setNotes(preRequest.getNotes());

                prescription.setMedicalRecord(medicalRecord);
                plist.add(prescription);
            }
            medicalRecord.setPrescriptions(plist);

            MedicalRecord createdRecord = medicalRecordRepository.save(medicalRecord);

            MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO();
            medicalRecordDTO.setId(createdRecord.getId());
            medicalRecordDTO.setPatientId(createdRecord.getPatient().getId());
            medicalRecordDTO.setPatientName(createdRecord.getPatient().getPatientName());
            medicalRecordDTO.setDoctorId(createdRecord.getDoctor().getId());
            medicalRecordDTO.setDoctorName(createdRecord.getDoctor().getName());
            medicalRecordDTO.setRemarks(createdRecord.getRemarks());
            medicalRecordDTO.setDiagnosis(createdRecord.getDiagnosis());
            medicalRecordDTO.setCreatedAt(createdRecord.getCreatedAt());

            List<PrescriptionDTO> prescriptionDTOList = createdRecord.getPrescriptions()
                    .stream()
                    .map(p -> {
                        PrescriptionDTO prescriptionDTO = new PrescriptionDTO();
                        prescriptionDTO.setId(p.getId());
                        prescriptionDTO.setMedicineName(p.getMedicineName());
                        prescriptionDTO.setDosage(p.getDosage());
                        prescriptionDTO.setStatus(p.getStatus());
                        prescriptionDTO.setPrescribedAt(p.getPrescribedAt());
                        prescriptionDTO.setDuration(p.getDuration());
                        prescriptionDTO.setFrequency(p.getFrequency());
                        prescriptionDTO.setInstructions(p.getInstructions());
                        return prescriptionDTO;
                    })
                    .collect(Collectors.toList());
            medicalRecordDTO.setPrescriptions(prescriptionDTOList);
            return medicalRecordDTO;
        }
        MedicalRecord createdRecord = medicalRecordRepository.save(medicalRecord);

        MedicalRecordDTO medicalRecordDTO = new MedicalRecordDTO();
        medicalRecordDTO.setId(createdRecord.getId());
        medicalRecordDTO.setPatientId(createdRecord.getPatient().getId());
        medicalRecordDTO.setPatientName(createdRecord.getPatient().getPatientName());
        medicalRecordDTO.setDoctorId(createdRecord.getDoctor().getId());
        medicalRecordDTO.setDoctorName(createdRecord.getDoctor().getName());
        medicalRecordDTO.setRemarks(createdRecord.getRemarks());
        medicalRecordDTO.setDiagnosis(createdRecord.getDiagnosis());
        medicalRecordDTO.setCreatedAt(createdRecord.getCreatedAt());

        return medicalRecordDTO;
    }
}
