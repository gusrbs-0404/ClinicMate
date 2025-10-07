package com.example.ClinicMate.repository;

import com.example.ClinicMate.entity.Payment;
import com.example.ClinicMate.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    // 예약별 결제 정보 조회
    Optional<Payment> findByReservation(Reservation reservation);
    
    // 예약 ID로 결제 정보 조회
    Optional<Payment> findByReservationResId(Long resId);
    
    // 결제 상태별 결제 목록 조회
    List<Payment> findByStatusOrderByCreatedAtDesc(Payment.PaymentStatus status);
    
    // 결제 방법별 결제 목록 조회
    List<Payment> findByMethodOrderByCreatedAtDesc(Payment.PaymentMethod method);
    
    // 모든 결제 조회 (예약 정보 포함) - 관리자용
    @Query("SELECT p FROM Payment p " +
           "LEFT JOIN FETCH p.reservation r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.hospital " +
           "LEFT JOIN FETCH r.department " +
           "LEFT JOIN FETCH r.doctor " +
           "ORDER BY p.createdAt DESC")
    List<Payment> findAllWithReservationOrderByCreatedAtDesc();
    
    // 병원별 결제 조회 (예약 정보 포함) - 관리자용
    @Query("SELECT p FROM Payment p " +
           "LEFT JOIN FETCH p.reservation r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.hospital " +
           "LEFT JOIN FETCH r.department " +
           "LEFT JOIN FETCH r.doctor " +
           "WHERE r.hospital.hospitalId = :hospitalId " +
           "ORDER BY p.createdAt DESC")
    List<Payment> findByHospitalHospitalIdOrderByCreatedAtDesc(@Param("hospitalId") Long hospitalId);
}
