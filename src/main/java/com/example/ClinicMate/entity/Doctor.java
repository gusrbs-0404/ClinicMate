package com.example.ClinicMate.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "DOCTOR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long doctorId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id", nullable = false)
    private Hospital hospital;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;
    
    @Column(name = "name", length = 50, nullable = false)
    private String name;
    
    @Column(name = "available_time", length = 255, nullable = false)
    private String availableTime;
}
