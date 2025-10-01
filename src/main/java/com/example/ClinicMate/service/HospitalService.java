package com.example.ClinicMate.service;

import com.example.ClinicMate.entity.Hospital;
import com.example.ClinicMate.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HospitalService {
    
    private final HospitalRepository hospitalRepository;
    
    // 모든 병원 조회
    public List<Hospital> getAllHospitals() {
        return hospitalRepository.findAllOrderByName();
    }
    
    // 병원 ID로 조회
    public Optional<Hospital> getHospitalById(Long hospitalId) {
        return hospitalRepository.findById(hospitalId);
    }
    
    // 병원명으로 검색
    public List<Hospital> searchHospitalsByName(String hospitalName) {
        return hospitalRepository.findByHospitalNameContaining(hospitalName);
    }
    
    // 주소로 검색
    public List<Hospital> searchHospitalsByAddress(String address) {
        return hospitalRepository.findByAddressContaining(address);
    }
    
    // 주변 병원 검색 (위도, 경도 범위)
    public List<Hospital> getNearbyHospitals(Double userLat, Double userLng, Double radiusKm) {
        // 1km = 약 0.009도 (대략적 계산)
        Double latRange = radiusKm * 0.009;
        Double lngRange = radiusKm * 0.011; // 경도는 위도에 따라 다르지만 대략적
        
        Double minLat = userLat - latRange;
        Double maxLat = userLat + latRange;
        Double minLng = userLng - lngRange;
        Double maxLng = userLng + lngRange;
        
        return hospitalRepository.findNearbyHospitals(minLat, maxLat, minLng, maxLng);
    }
    
    // 병원 등록 (관리자용)
    @Transactional
    public Hospital createHospital(Hospital hospital) {
        return hospitalRepository.save(hospital);
    }
    
    // 병원 정보 수정 (관리자용)
    @Transactional
    public Hospital updateHospital(Long hospitalId, Hospital hospitalDetails) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다: " + hospitalId));
        
        hospital.setHospitalName(hospitalDetails.getHospitalName());
        hospital.setAddress(hospitalDetails.getAddress());
        hospital.setPhone(hospitalDetails.getPhone());
        hospital.setLat(hospitalDetails.getLat());
        hospital.setLng(hospitalDetails.getLng());
        
        return hospitalRepository.save(hospital);
    }
    
    // 병원 삭제 (관리자용)
    @Transactional
    public void deleteHospital(Long hospitalId) {
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new RuntimeException("병원을 찾을 수 없습니다: " + hospitalId);
        }
        hospitalRepository.deleteById(hospitalId);
    }
    
    // 관리자용 메서드들
    public long getTotalHospitals() {
        return hospitalRepository.count();
    }
}
