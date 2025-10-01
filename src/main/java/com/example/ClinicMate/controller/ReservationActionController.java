package com.example.ClinicMate.controller;

import com.example.ClinicMate.entity.Reservation;
import com.example.ClinicMate.service.ReservationService;
import com.example.ClinicMate.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationActionController {
    
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    
    // 예약 생성
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReservation(
            @RequestParam Long hospitalId,
            @RequestParam Long doctorId,
            @RequestParam Long deptId,
            @RequestParam String resDate,
            @RequestParam String resTime,
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
            
            // 날짜와 시간을 합쳐서 LocalDateTime 생성
            String dateTimeStr = resDate + " " + resTime + ":00";
            LocalDateTime resDateTime = LocalDateTime.parse(dateTimeStr, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // 예약 생성
            Reservation reservation = reservationService.createReservation(
                userId, hospitalId, doctorId, deptId, resDateTime);
            
            response.put("success", true);
            response.put("message", "예약이 성공적으로 생성되었습니다.");
            response.put("reservationId", reservation.getResId());
            response.put("reservation", Map.of(
                "resId", reservation.getResId(),
                "resDate", reservation.getResDate(),
                "status", reservation.getStatus().name()
            ));
            
            log.info("예약 생성 성공: 사용자 ID {}, 예약 ID {}", userId, reservation.getResId());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "예약 생성 실패: " + e.getMessage());
            
            log.error("예약 생성 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 사용자별 예약 목록 조회
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyReservations(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 세션에서 사용자 ID 가져오기
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "로그인이 필요합니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 사용자별 예약 목록 조회
            List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
            
            response.put("success", true);
            response.put("reservations", reservations);
            response.put("count", reservations.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "예약 목록 조회 실패: " + e.getMessage());
            
            log.error("예약 목록 조회 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 예약 취소
    @DeleteMapping("/{resId}")
    public ResponseEntity<Map<String, Object>> cancelReservation(
            @PathVariable Long resId,
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
                response.put("message", "본인의 예약만 취소할 수 있습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 이미 취소된 예약인지 확인
            if (reservation.getStatus() == Reservation.ReservationStatus.취소) {
                response.put("success", false);
                response.put("message", "이미 취소된 예약입니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 예약 취소 (결제도 함께 취소)
            Reservation cancelledReservation = reservationService.cancelReservation(resId);
            
            response.put("success", true);
            response.put("message", "예약이 취소되었습니다. 결제도 함께 취소되었습니다.");
            response.put("reservationId", cancelledReservation.getResId());
            
            log.info("예약 취소 성공: 예약 ID {}", resId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "예약 취소 실패: " + e.getMessage());
            
            log.error("예약 취소 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
