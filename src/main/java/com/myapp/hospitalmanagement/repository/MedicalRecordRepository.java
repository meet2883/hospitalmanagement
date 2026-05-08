package com.myapp.hospitalmanagement.repository;

import com.myapp.hospitalmanagement.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    Optional<MedicalRecord> findByAppointmentId(Long appointmentId);

    boolean existsByAppointmentId(Long appointmentId);

    List<MedicalRecord> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patient.id = :patientId ORDER BY mr.createdAt DESC")
    Page<MedicalRecord> findByPatientIdOrderByCreatedAtDesc(@Param("patientId") Long patientId, Pageable pageable);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.doctor.id = :doctorId ORDER BY mr.createdAt DESC")
    List<MedicalRecord> findByDoctorIdOrderByCreatedAtDesc(@Param("doctorId") Long doctorId);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.appointment.id IN :appointmentIds")
    List<MedicalRecord> findAllByAppointmentIds(@Param("appointmentIds") List<Long> appointmentIds);

    @Query("SELECT mr FROM MedicalRecord mr JOIN mr.appointment a WHERE a.doctor.id = :doctorId AND mr.patient.id = :patientId ORDER BY mr.createdAt DESC")
    List<MedicalRecord> findByDoctorAndPatient(@Param("doctorId") Long doctorId, @Param("patientId") Long patientId);
}
