package com.myapp.hospitalmanagement.repository;

import com.myapp.hospitalmanagement.entity.Appointment;
import com.myapp.hospitalmanagement.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query(
            value = """
            SELECT 
                    a.id,
                    a.date, 
                    a.status, 
                    p.patientname AS patient_name, 
                    d.name AS doctor_name 
            FROM appointment AS a
            JOIN patient AS p ON a.patient_id = p.id
            JOIN doctor AS d ON a.doctor_id = d.id 
            """, nativeQuery = true
    )
    List<Map<String, Object>> getAllAppoinments();
}
