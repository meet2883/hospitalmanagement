package com.myapp.hospitalmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@ToString
@Getter
@Setter
@NamedEntityGraph(
        name = "Patient.insurance"
)
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

    @ManyToOne
    @JoinColumn(name = "insurance_id")
    private Insurance insurance;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Appointment> appointments;
}
