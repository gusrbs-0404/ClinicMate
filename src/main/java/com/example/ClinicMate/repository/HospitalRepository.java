package com.example.ClinicMate.repository;

import com.example.ClinicMate.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    
    // 병원명으로 검색
    List<Hospital> findByHospitalNameContaining(String hospitalName);
    
    // 주소로 검색
    List<Hospital> findByAddressContaining(String address);
    
    // 위도, 경도 범위로 검색 (주변 병원 찾기)
    @Query("SELECT h FROM Hospital h WHERE h.lat BETWEEN :minLat AND :maxLat AND h.lng BETWEEN :minLng AND :maxLng")
    List<Hospital> findNearbyHospitals(@Param("minLat") Double minLat, 
                                     @Param("maxLat") Double maxLat,
                                     @Param("minLng") Double minLng, 
                                     @Param("maxLng") Double maxLng);
    
    // 모든 병원 조회 (지도 표시용)
    @Query("SELECT h FROM Hospital h ORDER BY h.hospitalName")
    List<Hospital> findAllOrderByName();
}
