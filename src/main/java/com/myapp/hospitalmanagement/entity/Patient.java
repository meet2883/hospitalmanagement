package com.myapp.hospitalmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@ToString
@Getter
@Setter
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patientname")
    private String patientName;

    private String gender;

    @Column(name = "phonenumber")
    private String phoneNumber;

    @Column(name = "bloodgroup")
    private String bloodGroup;

    private String address;

    private Integer age;

    @Column(name = "dateofbirth")
    private LocalDate dateOfBirth;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "insurance_id", referencedColumnName = "id")
    private Insurance insurance;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Appointment> appointments;
}
