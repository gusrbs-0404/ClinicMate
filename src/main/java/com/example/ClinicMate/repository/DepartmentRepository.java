package com.example.ClinicMate.repository;

import com.example.ClinicMate.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    // 병원별 진료과 조회
    List<Department> findByHospitalHospitalId(Long hospitalId);
    
    // 진료과명으로 검색
    List<Department> findByDeptNameContaining(String deptName);
    
    // 특정 병원의 진료과 조회 (연관 엔티티 포함)
    @Query("SELECT d FROM Department d JOIN FETCH d.hospital h WHERE d.hospital.hospitalId = :hospitalId ORDER BY d.deptName")
    List<Department> findByHospitalIdOrderByName(@Param("hospitalId") Long hospitalId);
    
    // 모든 진료과 조회 (중복 제거)
    @Query("SELECT DISTINCT d.deptName FROM Department d ORDER BY d.deptName")
    List<String> findAllDistinctDeptNames();
    
    // 정확한 진료과명으로 조회
    List<Department> findByDeptName(String deptName);
    
    // 특정 병원에서 정확한 진료과명으로 조회
    List<Department> findByHospitalHospitalIdAndDeptName(Long hospitalId, String deptName);
    
    // 진료과 ID로 조회 (연관 엔티티 포함)
    @Query("SELECT d FROM Department d JOIN FETCH d.hospital h WHERE d.deptId = :deptId")
    Optional<Department> findByIdWithHospital(@Param("deptId") Long deptId);
    
    // 모든 진료과 조회 (연관 엔티티 포함)
    @Query("SELECT d FROM Department d JOIN FETCH d.hospital h ORDER BY d.deptName")
    List<Department> findAllWithHospital();
}
