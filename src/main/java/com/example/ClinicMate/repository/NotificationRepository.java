package com.example.ClinicMate.repository;

import com.example.ClinicMate.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // 예약 ID로 알림 조회
    List<Notification> findByResId(Long resId);
    
    // 알림 타입으로 조회
    List<Notification> findByType(Notification.NotificationType type);
    
    // 발송 상태로 조회
    List<Notification> findByStatus(Notification.NotificationStatus status);
    
    // 수신자로 조회
    List<Notification> findByRecipient(String recipient);
    
    // 실패한 알림 조회
    @Query("SELECT n FROM Notification n WHERE n.status = '실패'")
    List<Notification> findFailedNotifications();
    
    // 특정 기간 내 알림 조회
    @Query("SELECT n FROM Notification n WHERE n.sentAt BETWEEN :startDate AND :endDate")
    List<Notification> findBySentAtBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                         @Param("endDate") java.time.LocalDateTime endDate);
    
    // 알림 통계 조회
    @Query("SELECT n.type, COUNT(n) FROM Notification n GROUP BY n.type")
    List<Object[]> getNotificationStatsByType();
    
    @Query("SELECT n.status, COUNT(n) FROM Notification n GROUP BY n.status")
    List<Object[]> getNotificationStatsByStatus();
}
