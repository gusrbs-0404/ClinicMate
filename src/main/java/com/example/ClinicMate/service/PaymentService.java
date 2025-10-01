package com.example.ClinicMate.service;

import com.example.ClinicMate.entity.*;
import com.example.ClinicMate.repository.PaymentRepository;
import com.example.ClinicMate.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;
    
    // 결제 생성
    public Payment createPayment(Long resId, Integer amount, Payment.PaymentMethod method) {
        // 예약 정보 조회
        Reservation reservation = reservationRepository.findById(resId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));
        
        // 이미 결제가 있는지 확인
        Optional<Payment> existingPayment = paymentRepository.findByReservation(reservation);
        if (existingPayment.isPresent()) {
            throw new RuntimeException("이미 결제 정보가 존재합니다.");
        }
        
        // 결제 생성
        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(amount)
                .method(method)
                .status(Payment.PaymentStatus.대기)
                .build();
        
        return paymentRepository.save(payment);
    }
    
    // 결제 완료 처리
    public Payment completePayment(Long payId) {
        Payment payment = paymentRepository.findById(payId)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다."));
        
        // 결제 상태를 완료로 변경
        payment.setStatus(Payment.PaymentStatus.완료);
        Payment savedPayment = paymentRepository.save(payment);
        
        // 예약 상태도 완료로 변경
        reservationService.completeReservation(payment.getReservation().getResId());
        
        return savedPayment;
    }
    
    // 결제 취소 처리 (예약 취소와 별도로 호출)
    public Payment cancelPayment(Long payId) {
        Payment payment = paymentRepository.findById(payId)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다."));
        
        // 결제 상태를 취소로 변경
        payment.setStatus(Payment.PaymentStatus.취소);
        return paymentRepository.save(payment);
    }
    
    // 결제 조회
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentById(Long payId) {
        return paymentRepository.findById(payId);
    }
    
    // 예약별 결제 조회
    @Transactional(readOnly = true)
    public Optional<Payment> getPaymentByReservationId(Long resId) {
        return paymentRepository.findByReservationResId(resId);
    }
    
    // 결제 상태별 결제 목록 조회
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByStatusOrderByCreatedAtDesc(status);
    }
    
    // 관리자용 메서드들
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAllWithReservationOrderByCreatedAtDesc();
    }
    
    @Transactional(readOnly = true)
    public long getTotalPayments() {
        return paymentRepository.count();
    }
    
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByHospital(Long hospitalId) {
        // 병원별 결제 조회는 예약을 통해 필터링
        List<Payment> allPayments = paymentRepository.findAll();
        return allPayments.stream()
                .filter(payment -> payment.getReservation() != null && 
                                 payment.getReservation().getHospital() != null &&
                                 payment.getReservation().getHospital().getHospitalId().equals(hospitalId))
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Transactional
    public Payment updatePaymentStatus(Long payId, Payment.PaymentStatus status) {
        Payment payment = paymentRepository.findById(payId)
                .orElseThrow(() -> new RuntimeException("결제 정보를 찾을 수 없습니다."));
        
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }
}
