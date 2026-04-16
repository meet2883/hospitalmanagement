package com.myapp.hospitalmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.myapp.hospitalmanagement.entity.enumaration.Specialization;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@ToString
@Getter
@Setter
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private Specialization specialization;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Appointment> appointment;
}
