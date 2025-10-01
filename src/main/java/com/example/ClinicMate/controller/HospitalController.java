package com.example.ClinicMate.controller;

import com.example.ClinicMate.entity.Hospital;
import com.example.ClinicMate.entity.Department;
import com.example.ClinicMate.entity.Doctor;
import com.example.ClinicMate.service.HospitalService;
import com.example.ClinicMate.service.DepartmentService;
import com.example.ClinicMate.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {
    
    private final HospitalService hospitalService;
    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    
    // 모든 병원 조회
    @GetMapping
    public ResponseEntity<List<Hospital>> getAllHospitals() {
        try {
            List<Hospital> hospitals = hospitalService.getAllHospitals();
            return ResponseEntity.ok(hospitals);
        } catch (Exception e) {
            System.err.println("병원 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 병원 ID로 조회
    @GetMapping("/{hospitalId}")
    public ResponseEntity<Hospital> getHospitalById(@PathVariable Long hospitalId) {
        try {
            Hospital hospital = hospitalService.getHospitalById(hospitalId)
                    .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다"));
            return ResponseEntity.ok(hospital);
        } catch (Exception e) {
            System.err.println("병원 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 병원명으로 검색
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchHospitals(@RequestParam String name) {
        try {
            List<Hospital> hospitals = hospitalService.searchHospitalsByName(name);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", hospitals);
            response.put("count", hospitals.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "병원 검색 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 주변 병원 검색
    @GetMapping("/nearby")
    public ResponseEntity<Map<String, Object>> getNearbyHospitals(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "5.0") Double radius) {
        try {
            List<Hospital> hospitals = hospitalService.getNearbyHospitals(lat, lng, radius);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", hospitals);
            response.put("count", hospitals.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "주변 병원 검색 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 병원의 진료과 조회
    @GetMapping("/{hospitalId}/departments")
    public ResponseEntity<List<Department>> getHospitalDepartments(@PathVariable Long hospitalId) {
        try {
            List<Department> departments = departmentService.getDepartmentsByHospital(hospitalId);
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            System.err.println("진료과 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 병원의 특정 진료과 의사 조회
    @GetMapping("/{hospitalId}/departments/{deptId}/doctors")
    public ResponseEntity<List<Doctor>> getHospitalDepartmentDoctors(
            @PathVariable Long hospitalId,
            @PathVariable Long deptId) {
        try {
            List<Doctor> doctors = doctorService.getDoctorsByHospitalAndDepartment(hospitalId, deptId);
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            System.err.println("의사 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
