package com.example.ClinicMate.service;

import com.example.ClinicMate.entity.Doctor;
import com.example.ClinicMate.entity.Hospital;
import com.example.ClinicMate.entity.Department;
import com.example.ClinicMate.repository.DoctorRepository;
import com.example.ClinicMate.repository.HospitalRepository;
import com.example.ClinicMate.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorService {
    
    private final DoctorRepository doctorRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    
    // 모든 의사 조회
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAllWithHospitalAndDepartment();
    }
    
    // 의사 ID로 조회
    public Optional<Doctor> getDoctorById(Long doctorId) {
        return doctorRepository.findById(doctorId);
    }
    
    // 병원별 의사 조회
    public List<Doctor> getDoctorsByHospital(Long hospitalId) {
        return doctorRepository.findByHospitalHospitalId(hospitalId);
    }
    
    // 진료과별 의사 조회
    public List<Doctor> getDoctorsByDepartment(Long deptId) {
        return doctorRepository.findByDepartmentDeptId(deptId);
    }
    
    // 병원과 진료과로 의사 조회
    public List<Doctor> getDoctorsByHospitalAndDepartment(Long hospitalId, Long deptId) {
        return doctorRepository.findByHospitalAndDepartment(hospitalId, deptId);
    }
    
    // 의사명으로 검색
    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepository.findByNameContaining(name);
    }
    
    // 의사 등록 (관리자용)
    @Transactional
    public Doctor createDoctor(Long hospitalId, Long deptId, String name, String availableTime) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다: " + hospitalId));
        
        Department department = departmentRepository.findById(deptId)
                .orElseThrow(() -> new RuntimeException("진료과를 찾을 수 없습니다: " + deptId));
        
        Doctor doctor = Doctor.builder()
                .hospital(hospital)
                .department(department)
                .name(name)
                .availableTime(availableTime)
                .build();
        
        return doctorRepository.save(doctor);
    }
    
    // 의사 정보 수정 (관리자용)
    @Transactional
    public Doctor updateDoctor(Long doctorId, Long hospitalId, Long deptId, String name, String availableTime) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("의사를 찾을 수 없습니다: " + doctorId));
        
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다: " + hospitalId));
        
        Department department = departmentRepository.findById(deptId)
                .orElseThrow(() -> new RuntimeException("진료과를 찾을 수 없습니다: " + deptId));
        
        doctor.setHospital(hospital);
        doctor.setDepartment(department);
        doctor.setName(name);
        doctor.setAvailableTime(availableTime);
        
        return doctorRepository.save(doctor);
    }
    
    // 의사 삭제 (관리자용)
    @Transactional
    public void deleteDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new RuntimeException("의사를 찾을 수 없습니다: " + doctorId);
        }
        doctorRepository.deleteById(doctorId);
    }
}
