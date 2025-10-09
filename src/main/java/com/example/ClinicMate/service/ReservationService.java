package com.example.ClinicMate.service;

import com.example.ClinicMate.entity.*;
import com.example.ClinicMate.repository.ReservationRepository;
import com.example.ClinicMate.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
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
public class ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private final HospitalService hospitalService;
    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    
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
        
        return reservationRepository.save(reservation);
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
        
        // 예약 상태를 취소로 변경
        reservation.setStatus(Reservation.ReservationStatus.취소);
        reservation = reservationRepository.save(reservation);
        
        // 결제가 있다면 결제도 취소
        if (reservation.getPayment() != null) {
            Payment payment = reservation.getPayment();
            payment.setStatus(Payment.PaymentStatus.취소);
            paymentRepository.save(payment);
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
}
