package com.example.ClinicMate.service;

import com.example.ClinicMate.entity.Department;
import com.example.ClinicMate.entity.Hospital;
import com.example.ClinicMate.repository.DepartmentRepository;
import com.example.ClinicMate.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentService {
    
    private final DepartmentRepository departmentRepository;
    private final HospitalRepository hospitalRepository;
    
    // 모든 진료과 조회
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
    
    // 진료과 ID로 조회
    public Optional<Department> getDepartmentById(Long deptId) {
        return departmentRepository.findById(deptId);
    }
    
    // 병원별 진료과 조회
    public List<Department> getDepartmentsByHospital(Long hospitalId) {
        return departmentRepository.findByHospitalIdOrderByName(hospitalId);
    }
    
    // 진료과명으로 검색
    public List<Department> searchDepartmentsByName(String deptName) {
        return departmentRepository.findByDeptNameContaining(deptName);
    }
    
    // 모든 진료과명 조회 (중복 제거)
    public List<String> getAllDistinctDeptNames() {
        return departmentRepository.findAllDistinctDeptNames();
    }
    
    // 진료과 등록 (관리자용)
    @Transactional
    public Department createDepartment(Long hospitalId, String deptName) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다: " + hospitalId));
        
        // 중복 체크
        if (departmentRepository.findByDeptNameContaining(deptName).stream()
                .anyMatch(dept -> dept.getDeptName().equals(deptName))) {
            throw new RuntimeException("이미 존재하는 진료과입니다: " + deptName);
        }
        
        Department department = Department.builder()
                .hospital(hospital)
                .deptName(deptName)
                .build();
        
        return departmentRepository.save(department);
    }
    
    // 진료과 정보 수정 (관리자용)
    @Transactional
    public Department updateDepartment(Long deptId, String deptName) {
        Department department = departmentRepository.findById(deptId)
                .orElseThrow(() -> new RuntimeException("진료과를 찾을 수 없습니다: " + deptId));
        
        department.setDeptName(deptName);
        return departmentRepository.save(department);
    }
    
    // 진료과 삭제 (관리자용)
    @Transactional
    public void deleteDepartment(Long deptId) {
        if (!departmentRepository.existsById(deptId)) {
            throw new RuntimeException("진료과를 찾을 수 없습니다: " + deptId);
        }
        departmentRepository.deleteById(deptId);
    }
}
