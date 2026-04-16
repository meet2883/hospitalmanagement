package com.myapp.hospitalmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

    @OneToOne(mappedBy = "insurance", fetch = FetchType.LAZY)
    @JsonIgnore
    private Patient patient;
}
