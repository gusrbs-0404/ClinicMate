package com.example.ClinicMate.repository;

import com.example.ClinicMate.entity.Reservation;
import com.example.ClinicMate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // 사용자별 예약 목록 조회
    List<Reservation> findByUserOrderByCreatedAtDesc(User user);
    
    // 사용자별 예약 목록 조회 (상태별)
    List<Reservation> findByUserAndStatusOrderByCreatedAtDesc(User user, Reservation.ReservationStatus status);
    
    // 병원별 예약 목록 조회
    List<Reservation> findByHospitalHospitalIdOrderByResDateAsc(Long hospitalId);
    
    // 의사별 예약 목록 조회
    List<Reservation> findByDoctorDoctorIdOrderByResDateAsc(Long doctorId);
    
    // 특정 날짜의 예약 목록 조회
    @Query("SELECT r FROM Reservation r WHERE r.resDate BETWEEN :startDate AND :endDate ORDER BY r.resDate ASC")
    List<Reservation> findByResDateBetween(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    // 사용자 ID로 예약 목록 조회
    List<Reservation> findByUserUserIdOrderByCreatedAtDesc(Long userId);
    
    // 예약 ID로 예약 조회 (결제 정보 포함)
    @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.payment WHERE r.resId = :resId")
    Optional<Reservation> findByIdWithPayment(@Param("resId") Long resId);

    // 사용자 ID로 예약 목록 조회 (연관 엔티티 포함)
    @Query("SELECT r FROM Reservation r " +
           "LEFT JOIN FETCH r.hospital " +
           "LEFT JOIN FETCH r.department " +
           "LEFT JOIN FETCH r.doctor " +
           "WHERE r.user.userId = :userId " +
           "ORDER BY r.createdAt DESC")
    List<Reservation> findByUserUserIdWithDetailsOrderByCreatedAtDesc(@Param("userId") Long userId);
}
