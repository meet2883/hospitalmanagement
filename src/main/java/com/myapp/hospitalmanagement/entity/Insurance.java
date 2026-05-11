package com.myapp.hospitalmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Getter
@Setter
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policyname")
    private String policyName;

    @Column(name = "policyprovider")
    private String policyProvider;

    @OneToMany(mappedBy = "insurance", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Patient> patient = new ArrayList<>();
}
