package com.example.ClinicMate.service;

import com.example.ClinicMate.entity.*;
import com.example.ClinicMate.repository.ReservationRepository;
import com.example.ClinicMate.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final HospitalService hospitalService;
    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    private final MailService mailService;
    
    @Value("${admin.email}")
    private String adminEmail;
    
    // 예약 생성
    public Reservation createReservation(Long userId, Long hospitalId, Long doctorId, 
                                       Long deptId, LocalDateTime resDate) {
        
        // 사용자, 병원, 의사, 진료과 정보 조회
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Hospital hospital = hospitalService.getHospitalById(hospitalId)
                .orElseThrow(() -> new RuntimeException("병원을 찾을 수 없습니다."));
        
        Doctor doctor = doctorService.getDoctorById(doctorId)
                .orElseThrow(() -> new RuntimeException("의사를 찾을 수 없습니다."));
        
        Department department = departmentService.getDepartmentById(deptId)
                .orElseThrow(() -> new RuntimeException("진료과를 찾을 수 없습니다."));
        
        // 예약 생성
        Reservation reservation = Reservation.builder()
                .user(user)
                .hospital(hospital)
                .doctor(doctor)
                .department(department)
                .resDate(resDate)
                .status(Reservation.ReservationStatus.예약중)
                .build();
        
        Reservation savedReservation = reservationRepository.save(reservation);
        
        // 예약 완료 이메일 발송
        try {
            String reservationDate = resDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String reservationTime = resDate.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            
            mailService.sendReservationConfirmationEmail(
                user.getEmail(),
                user.getName(),
                hospital.getHospitalName(),
                department.getDeptName(),
                doctor.getName(),
                reservationDate,
                reservationTime,
                user.getUserId(),
                savedReservation.getResId()
            );
            log.info("예약 완료 이메일 발송 성공: {}", user.getEmail());
        } catch (Exception e) {
            log.error("예약 완료 이메일 발송 실패: {}", e.getMessage());
            // 이메일 발송 실패해도 예약은 성공으로 처리
        }
        
        return savedReservation;
    }
    
    // 예약 조회
    @Transactional(readOnly = true)
    public Optional<Reservation> getReservationById(Long resId) {
        return reservationRepository.findByIdWithPayment(resId);
    }
    
    // 사용자별 예약 목록 조회
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserUserIdWithDetailsOrderByCreatedAtDesc(userId);
    }
    
    // 예약 상태 업데이트
    public Reservation updateReservationStatus(Long resId, Reservation.ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(resId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));
        
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }
    
    // 예약 취소 (결제도 함께 취소)
    @Transactional
    public Reservation cancelReservation(Long resId) {
        Reservation reservation = reservationRepository.findById(resId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));
        
        // 결제가 완료된 상태라면 결제 관련 이메일들을 먼저 발송
        if (reservation.getPayment() != null && reservation.getPayment().getStatus() == Payment.PaymentStatus.완료) {
            Payment payment = reservation.getPayment();
            String refundAmount = payment.getAmount().toString(); // 환불 금액 (전액 환불)
            
            // 1. 결제 취소 이메일 발송 (사용자에게)
            try {
                mailService.sendPaymentCancellationEmail(
                    reservation.getUser().getEmail(),
                    reservation.getUser().getName(),
                    reservation.getHospital().getHospitalName(),
                    payment.getAmount().toString(),
                    refundAmount,
                    reservation.getUser().getUserId(),
                    payment.getPayId(),
                    reservation.getResId()
                );
                log.info("결제 취소 이메일 발송 성공: {}", reservation.getUser().getEmail());
            } catch (Exception e) {
                log.error("결제 취소 이메일 발송 실패: {}", e.getMessage());
                // 이메일 발송 실패해도 예약 취소는 성공으로 처리
            }
            
            // 2. 관리자에게 결제 취소 알림 이메일 발송
            try {
                mailService.sendPaymentCancellationNotificationToAdmin(
                    adminEmail,
                    reservation.getUser().getName(),
                    reservation.getUser().getEmail(),
                    reservation.getHospital().getHospitalName(),
                    payment.getAmount().toString(),
                    refundAmount,
                    reservation.getUser().getUserId(),
                    payment.getPayId(),
                    reservation.getResId()
                );
                log.info("관리자 결제 취소 알림 이메일 발송 성공: {}", adminEmail);
            } catch (Exception e) {
                log.error("관리자 결제 취소 알림 이메일 발송 실패: {}", e.getMessage());
                // 이메일 발송 실패해도 예약 취소는 성공으로 처리
            }
        } else {
            log.info("결제 관련 이메일 발송 건너뜀 - 결제 상태: {}", 
                reservation.getPayment() != null ? reservation.getPayment().getStatus() : "결제 없음");
        }
        
        // 예약 상태를 취소로 변경
        reservation.setStatus(Reservation.ReservationStatus.취소);
        reservation = reservationRepository.save(reservation);
        
        // 결제가 있다면 결제도 취소
        if (reservation.getPayment() != null) {
            Payment payment = reservation.getPayment();
            payment.setStatus(Payment.PaymentStatus.취소);
            paymentRepository.save(payment);
        }
        
        // 3. 예약 취소 이메일 발송 (사용자에게)
        try {
            String reservationDate = reservation.getResDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            mailService.sendReservationCancellationEmail(
                reservation.getUser().getEmail(),
                reservation.getUser().getName(),
                reservation.getHospital().getHospitalName(),
                reservation.getDepartment().getDeptName(),
                reservation.getDoctor().getName(),
                reservationDate,
                reservation.getUser().getUserId(),
                reservation.getResId()
            );
            log.info("예약 취소 이메일 발송 성공: {}", reservation.getUser().getEmail());
        } catch (Exception e) {
            log.error("예약 취소 이메일 발송 실패: {}", e.getMessage());
            // 이메일 발송 실패해도 예약 취소는 성공으로 처리
        }
        
        // 4. 관리자에게 예약 취소 알림 이메일 발송
        try {
            String reservationDate = reservation.getResDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            
            mailService.sendReservationCancellationNotificationToAdmin(
                adminEmail,
                reservation.getUser().getName(),
                reservation.getUser().getEmail(),
                reservation.getHospital().getHospitalName(),
                reservation.getDepartment().getDeptName(),
                reservation.getDoctor().getName(),
                reservationDate,
                reservation.getUser().getUserId(),
                reservation.getResId()
            );
            log.info("관리자 예약 취소 알림 이메일 발송 성공: {}", adminEmail);
        } catch (Exception e) {
            log.error("관리자 예약 취소 알림 이메일 발송 실패: {}", e.getMessage());
            // 이메일 발송 실패해도 예약 취소는 성공으로 처리
        }
        
        return reservation;
    }
    
    // 예약 완료 (결제 완료 후)
    public Reservation completeReservation(Long resId) {
        return updateReservationStatus(resId, Reservation.ReservationStatus.완료);
    }
    
    // 결제 정보와 함께 예약 조회
    @Transactional(readOnly = true)
    public Optional<Reservation> getReservationWithPayment(Long resId) {
        return reservationRepository.findByIdWithPayment(resId);
    }

    // 특정 의사의 특정 날짜에 예약된 시간 목록 조회
    @Transactional(readOnly = true)
    public List<String> getBookedTimesForDoctorAndDate(Long doctorId, LocalDateTime date) {
        List<Reservation> reservations = reservationRepository.findByDoctorDoctorIdOrderByResDateAsc(doctorId);
        
        // 해당 날짜의 예약된 시간들을 "HH:mm" 형식으로 반환
        return reservations.stream()
                .filter(reservation -> {
                    LocalDateTime resDate = reservation.getResDate();
                    return resDate.toLocalDate().equals(date.toLocalDate()) &&
                           (reservation.getStatus() == Reservation.ReservationStatus.예약중 ||
                            reservation.getStatus() == Reservation.ReservationStatus.완료);
                })
                .map(reservation -> reservation.getResDate().toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")))
                .collect(java.util.stream.Collectors.toList());
    }
    
    // 관리자용 메서드들
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAllWithDetailsOrderByCreatedAtDesc();
    }
    
    @Transactional(readOnly = true)
    public long getTotalReservations() {
        return reservationRepository.count();
    }
    
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByHospital(Long hospitalId) {
        return reservationRepository.findByHospitalHospitalIdOrderByResDateAsc(hospitalId);
    }
    
    // 특정 의사의 특정 날짜 예약 목록 조회
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByDoctorAndDate(Long doctorId, String date) {
        LocalDateTime startDate = LocalDateTime.parse(date + " 00:00:00", 
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endDate = LocalDateTime.parse(date + " 23:59:59", 
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        return reservationRepository.findByDoctorAndDateRange(doctorId, startDate, endDate);
    }
    
    // 월별 예약 통계
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMonthlyReservations(String year, Long hospitalId) {
        List<Object[]> results = reservationRepository.getMonthlyReservations(year, hospitalId);
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("year", row[0]);
            map.put("month", row[1]);
            map.put("count", row[2]);
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    // 일별 예약 통계
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDailyReservations(String year, String month, Long hospitalId) {
        List<Object[]> results = reservationRepository.getDailyReservations(year, month, hospitalId);
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("day", row[0]);
            map.put("count", row[1]);
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    // 진료과별 예약 통계
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDepartmentReservations(Long hospitalId) {
        List<Object[]> results = reservationRepository.getDepartmentReservations(hospitalId);
        return results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("departmentName", row[0]);
            map.put("count", row[1]);
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    // 페이징된 예약 목록 조회
    public Map<String, Object> getReservationsWithPaging(int page, int size, Long hospitalId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservationPage;
        
        if (hospitalId != null) {
            reservationPage = reservationRepository.findByHospitalHospitalIdWithPaging(hospitalId, pageable);
        } else {
            reservationPage = reservationRepository.findAllWithDetailsPaging(pageable);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", reservationPage.getContent());
        result.put("totalElements", reservationPage.getTotalElements());
        result.put("totalPages", reservationPage.getTotalPages());
        result.put("currentPage", page);
        result.put("size", size);
        result.put("hasNext", reservationPage.hasNext());
        result.put("hasPrevious", reservationPage.hasPrevious());
        
        return result;
    }
}
