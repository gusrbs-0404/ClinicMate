package com.example.ClinicMate.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ClinicMate.entity.Reservation;
import com.example.ClinicMate.entity.User;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // 사용자별 예약 목록 조회
    List<Reservation> findByUserOrderByCreatedAtDesc(User user);
    
    // 사용자별 예약 목록 조회 (상태별)
    List<Reservation> findByUserAndStatusOrderByCreatedAtDesc(User user, Reservation.ReservationStatus status);
    
    // 병원별 예약 목록 조회 (연관 엔티티 포함)
    @Query("SELECT r FROM Reservation r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.hospital " +
           "LEFT JOIN FETCH r.department " +
           "LEFT JOIN FETCH r.doctor " +
           "WHERE r.hospital.hospitalId = :hospitalId " +
           "ORDER BY r.resDate ASC")
    List<Reservation> findByHospitalHospitalIdOrderByResDateAsc(@Param("hospitalId") Long hospitalId);
    
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
    
    // 모든 예약 조회 (연관 엔티티 포함) - 관리자용
    @Query("SELECT r FROM Reservation r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.hospital " +
           "LEFT JOIN FETCH r.department " +
           "LEFT JOIN FETCH r.doctor " +
           "ORDER BY r.createdAt DESC")
    List<Reservation> findAllWithDetailsOrderByCreatedAtDesc();
    
    // 특정 의사의 특정 날짜 범위 예약 조회 (연관 엔티티 포함)
    @Query("SELECT r FROM Reservation r " +
           "LEFT JOIN FETCH r.user " +
           "LEFT JOIN FETCH r.hospital " +
           "LEFT JOIN FETCH r.department " +
           "LEFT JOIN FETCH r.doctor " +
           "WHERE r.doctor.doctorId = :doctorId " +
           "AND r.resDate BETWEEN :startDate AND :endDate " +
           "ORDER BY r.resDate ASC")
    List<Reservation> findByDoctorAndDateRange(@Param("doctorId") Long doctorId, 
                                               @Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate);
    
    // 월별 예약 통계
    @Query("SELECT FUNCTION('YEAR', r.resDate), " +
           "FUNCTION('MONTH', r.resDate), " +
           "COUNT(r) " +
           "FROM Reservation r " +
           "WHERE FUNCTION('YEAR', r.resDate) = :year " +
           "AND (:hospitalId IS NULL OR r.hospital.hospitalId = :hospitalId) " +
           "GROUP BY FUNCTION('YEAR', r.resDate), FUNCTION('MONTH', r.resDate) " +
           "ORDER BY FUNCTION('MONTH', r.resDate)")
    List<Object[]> getMonthlyReservations(@Param("year") String year, @Param("hospitalId") Long hospitalId);
    
    // 일별 예약 통계
    @Query("SELECT FUNCTION('DAY', r.resDate), " +
           "COUNT(r) " +
           "FROM Reservation r " +
           "WHERE FUNCTION('YEAR', r.resDate) = :year " +
           "AND FUNCTION('MONTH', r.resDate) = :month " +
           "AND (:hospitalId IS NULL OR r.hospital.hospitalId = :hospitalId) " +
           "GROUP BY FUNCTION('DAY', r.resDate) " +
           "ORDER BY FUNCTION('DAY', r.resDate)")
    List<Object[]> getDailyReservations(@Param("year") String year, @Param("month") String month, @Param("hospitalId") Long hospitalId);
    
    // 진료과별 예약 통계
    @Query("SELECT d.deptName, " +
           "COUNT(r) " +
           "FROM Reservation r " +
           "JOIN r.department d " +
           "WHERE (:hospitalId IS NULL OR r.hospital.hospitalId = :hospitalId) " +
           "GROUP BY d.deptName " +
           "ORDER BY COUNT(r) DESC")
    List<Object[]> getDepartmentReservations(@Param("hospitalId") Long hospitalId);
}
