package com.example.ClinicMate.repository;

import com.example.ClinicMate.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // 모든 알림 조회 (최신순)
    Page<Notification> findAllByOrderBySentAtDesc(Pageable pageable);
    
    // 모든 알림 조회 (최신순, 페이징 없이)
    List<Notification> findAllByOrderBySentAtDesc();
    
    // 상태별 알림 조회 (최신순)
    Page<Notification> findByStatusOrderBySentAtDesc(Notification.NotificationStatus status, Pageable pageable);
    
    // 타입별 알림 조회 (최신순)
    List<Notification> findByTypeOrderBySentAtDesc(Notification.NotificationType type);
    
    // 수신자별 알림 조회 (최신순)
    List<Notification> findByRecipientOrderBySentAtDesc(String recipient);
    
    // 수신자 타입별 알림 조회 (최신순)
    List<Notification> findByRecipientTypeOrderBySentAtDesc(Notification.RecipientType recipientType);
    
    // 사용자별 알림 조회 (최신순)
    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);
    
    // 예약별 알림 조회 (최신순)
    List<Notification> findByResIdOrderBySentAtDesc(Long resId);
    
    // 결제별 알림 조회 (최신순)
    List<Notification> findByPayIdOrderBySentAtDesc(Long payId);
    
    // 상태별 알림 수 조회
    long countByStatus(Notification.NotificationStatus status);
    
    // 타입별 알림 수 조회
    long countByType(Notification.NotificationType type);
    
    // 수신자별 알림 수 조회
    long countByRecipient(String recipient);
    
    // 수신자 타입별 알림 수 조회
    long countByRecipientType(Notification.RecipientType recipientType);
    
    // 특정 기간 내 알림 조회
    @Query("SELECT n FROM Notification n WHERE n.sentAt BETWEEN :startDate AND :endDate ORDER BY n.sentAt DESC")
    List<Notification> findBySentAtBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                         @Param("endDate") java.time.LocalDateTime endDate);
    
    // 실패한 알림 중 특정 타입 조회
    List<Notification> findByStatusAndTypeOrderBySentAtDesc(Notification.NotificationStatus status, 
                                                           Notification.NotificationType type);
    
    // 최근 N일간의 알림 조회
    @Query("SELECT n FROM Notification n WHERE n.sentAt >= :since ORDER BY n.sentAt DESC")
    List<Notification> findRecentNotifications(@Param("since") java.time.LocalDateTime since);
    
    // 알림 통계 쿼리
    @Query("SELECT n.type, COUNT(n) FROM Notification n GROUP BY n.type")
    List<Object[]> getNotificationCountByType();
    
    @Query("SELECT n.status, COUNT(n) FROM Notification n GROUP BY n.status")
    List<Object[]> getNotificationCountByStatus();
    
    @Query("SELECT n.recipientType, COUNT(n) FROM Notification n GROUP BY n.recipientType")
    List<Object[]> getNotificationCountByRecipientType();
}