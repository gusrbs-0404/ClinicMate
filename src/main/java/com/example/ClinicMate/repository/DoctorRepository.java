package com.example.ClinicMate.repository;

import com.example.ClinicMate.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    // 병원별 의사 조회 (연관 엔티티 포함)
    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital h JOIN FETCH d.department dept WHERE d.hospital.hospitalId = :hospitalId ORDER BY d.name")
    List<Doctor> findByHospitalHospitalId(@Param("hospitalId") Long hospitalId);
    
    // 진료과별 의사 조회 (연관 엔티티 포함)
    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital h JOIN FETCH d.department dept WHERE d.department.deptId = :deptId ORDER BY d.name")
    List<Doctor> findByDepartmentDeptId(@Param("deptId") Long deptId);
    
    // 병원과 진료과로 의사 조회 (연관 엔티티 포함)
    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital h JOIN FETCH d.department dept WHERE d.hospital.hospitalId = :hospitalId AND d.department.deptId = :deptId ORDER BY d.name")
    List<Doctor> findByHospitalHospitalIdAndDepartmentDeptId(@Param("hospitalId") Long hospitalId, @Param("deptId") Long deptId);
    
    // 의사명으로 검색
    List<Doctor> findByNameContaining(String name);
    
    // 특정 병원의 특정 진료과 의사 조회 (연관 엔티티 포함)
    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital h JOIN FETCH d.department dept WHERE d.hospital.hospitalId = :hospitalId AND d.department.deptId = :deptId ORDER BY d.name")
    List<Doctor> findByHospitalAndDepartment(@Param("hospitalId") Long hospitalId, @Param("deptId") Long deptId);
    
    // 모든 의사 조회 (병원명, 진료과명 포함)
    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital h JOIN FETCH d.department dept ORDER BY h.hospitalName, dept.deptName, d.name")
    List<Doctor> findAllWithHospitalAndDepartment();
    
    // 페이징된 의사 조회 (병원명, 진료과명 포함)
    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital h JOIN FETCH d.department dept ORDER BY h.hospitalName, dept.deptName, d.name")
    Page<Doctor> findAllWithHospitalAndDepartment(Pageable pageable);
    
    // 의사 ID로 조회 (연관 엔티티 포함)
    @Query("SELECT d FROM Doctor d JOIN FETCH d.hospital h JOIN FETCH d.department dept WHERE d.doctorId = :doctorId")
    Optional<Doctor> findByIdWithHospitalAndDepartment(@Param("doctorId") Long doctorId);
}
