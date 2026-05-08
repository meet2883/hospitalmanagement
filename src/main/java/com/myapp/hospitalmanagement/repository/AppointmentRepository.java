package com.myapp.hospitalmanagement.repository;

import com.myapp.hospitalmanagement.entity.Appointment;
import com.myapp.hospitalmanagement.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    @Query(
            value = """
            SELECT
                    a.id,
                    a.date,
                    a.status,
                    a.type,
                    p.patientname AS patient_name,
                    d.name AS doctor_name
            FROM appointment AS a
            JOIN patient AS p ON a.patient_id = p.id
            JOIN doctor AS d ON a.doctor_id = d.id
            """, nativeQuery = true
    )
    List<Map<String, Object>> getAllAppoinments();

    List<Appointment> findByDoctorId(Long id);

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.medicalRecord WHERE a.doctor.id = :doctorId ORDER BY a.appointmentdatetime DESC")
    List<Appointment> findByDoctorIdWithMedicalRecord(@Param("doctorId") Long doctorId);

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.medicalRecord WHERE a.patient.id = :patientId ORDER BY a.appointmentdatetime DESC")
    List<Appointment> findByPatientIdWithMedicalRecord(@Param("patientId") Long patientId);

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.medicalRecord WHERE a.id = :id")
    Optional<Appointment> findByIdWithMedicalRecord(@Param("id") Long id);
}
