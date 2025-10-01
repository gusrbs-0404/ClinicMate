package com.example.ClinicMate.controller;

import com.example.ClinicMate.entity.Payment;
import com.example.ClinicMate.entity.Reservation;
import com.example.ClinicMate.service.PaymentService;
import com.example.ClinicMate.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentActionController {
    
    private final PaymentService paymentService;
    private final ReservationService reservationService;
    
    // 결제 생성 (결제 요청)
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPayment(
            @RequestParam Long resId,
            @RequestParam Integer amount,
            @RequestParam String method,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 세션에서 사용자 ID 가져오기
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 예약 정보 조회 및 소유자 확인
            Optional<Reservation> reservationOpt = reservationService.getReservationById(resId);
            if (reservationOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "예약 정보를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            Reservation reservation = reservationOpt.get();
            if (!reservation.getUser().getUserId().equals(userId)) {
                response.put("success", false);
                response.put("message", "본인의 예약만 결제할 수 있습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 예약 상태 확인
            if (reservation.getStatus() != Reservation.ReservationStatus.예약중) {
                response.put("success", false);
                response.put("message", "예약중인 상태에서만 결제할 수 있습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 결제 방법 검증
            Payment.PaymentMethod paymentMethod;
            try {
                paymentMethod = Payment.PaymentMethod.valueOf(method);
            } catch (IllegalArgumentException e) {
                response.put("success", false);
                response.put("message", "올바른 결제 방법을 선택해주세요.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 결제 생성
            Payment payment = paymentService.createPayment(resId, amount, paymentMethod);
            
            response.put("success", true);
            response.put("message", "결제가 요청되었습니다.");
            response.put("paymentId", payment.getPayId());
            response.put("payment", Map.of(
                "payId", payment.getPayId(),
                "amount", payment.getAmount(),
                "method", payment.getMethod().name(),
                "status", payment.getStatus().name()
            ));
            
            log.info("결제 생성 성공: 예약 ID {}, 결제 ID {}", resId, payment.getPayId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "결제 생성 실패: " + e.getMessage());
            
            log.error("결제 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 결제 완료 처리
    @PostMapping("/{payId}/complete")
    public ResponseEntity<Map<String, Object>> completePayment(
            @PathVariable Long payId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 세션에서 사용자 ID 가져오기
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 결제 정보 조회
            Optional<Payment> paymentOpt = paymentService.getPaymentById(payId);
            if (paymentOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "결제 정보를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            Payment payment = paymentOpt.get();
            
            // 결제 소유자 확인
            if (!payment.getReservation().getUser().getUserId().equals(userId)) {
                response.put("success", false);
                response.put("message", "본인의 결제만 처리할 수 있습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 결제 상태 확인
            if (payment.getStatus() != Payment.PaymentStatus.대기) {
                response.put("success", false);
                response.put("message", "대기 상태인 결제만 완료할 수 있습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 결제 완료 처리
            Payment completedPayment = paymentService.completePayment(payId);
            
            response.put("success", true);
            response.put("message", "결제가 완료되었습니다.");
            response.put("paymentId", completedPayment.getPayId());
            response.put("reservationId", completedPayment.getReservation().getResId());
            
            log.info("결제 완료 성공: 결제 ID {}", payId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "결제 완료 실패: " + e.getMessage());
            
            log.error("결제 완료 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 결제 취소 처리
    @PostMapping("/{payId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelPayment(
            @PathVariable Long payId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 세션에서 사용자 ID 가져오기
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 결제 정보 조회
            Optional<Payment> paymentOpt = paymentService.getPaymentById(payId);
            if (paymentOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "결제 정보를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            Payment payment = paymentOpt.get();
            
            // 결제 소유자 확인
            if (!payment.getReservation().getUser().getUserId().equals(userId)) {
                response.put("success", false);
                response.put("message", "본인의 결제만 취소할 수 있습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 결제 취소 처리
            Payment cancelledPayment = paymentService.cancelPayment(payId);
            
            response.put("success", true);
            response.put("message", "결제가 취소되었습니다.");
            response.put("paymentId", cancelledPayment.getPayId());
            response.put("reservationId", cancelledPayment.getReservation().getResId());
            
            log.info("결제 취소 성공: 결제 ID {}", payId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "결제 취소 실패: " + e.getMessage());
            
            log.error("결제 취소 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 결제 정보 조회
    @GetMapping("/{payId}")
    public ResponseEntity<Map<String, Object>> getPayment(
            @PathVariable Long payId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 세션에서 사용자 ID 가져오기
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 결제 정보 조회
            Optional<Payment> paymentOpt = paymentService.getPaymentById(payId);
            if (paymentOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "결제 정보를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            Payment payment = paymentOpt.get();
            
            // 결제 소유자 확인
            if (!payment.getReservation().getUser().getUserId().equals(userId)) {
                response.put("success", false);
                response.put("message", "본인의 결제 정보만 조회할 수 있습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            response.put("success", true);
            response.put("payment", payment);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "결제 정보 조회 실패: " + e.getMessage());
            
            log.error("결제 정보 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
