package com.example.ClinicMate.repository;

import com.example.ClinicMate.entity.Payment;
import com.example.ClinicMate.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
