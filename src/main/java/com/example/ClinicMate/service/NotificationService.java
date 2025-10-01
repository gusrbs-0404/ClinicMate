package com.example.ClinicMate.service;

import com.example.ClinicMate.entity.Notification;
import com.example.ClinicMate.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    // 모든 알림 조회
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
    
    // 알림 ID로 조회
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }
    
    // 예약 ID로 알림 조회
    public List<Notification> getNotificationsByReservationId(Long resId) {
        return notificationRepository.findByResId(resId);
    }
    
    // 알림 생성 및 발송 (Mock)
    public Notification createAndSendNotification(Long resId, Notification.NotificationType type, String recipient) {
        Notification notification = new Notification(resId, type, recipient);
        
        // Mock 발송 로직 (실제로는 이메일/SMS 발송)
        boolean sendSuccess = mockSendNotification(notification);
        notification.setStatus(sendSuccess ? Notification.NotificationStatus.성공 : Notification.NotificationStatus.실패);
        
        return notificationRepository.save(notification);
    }
    
    // 알림 재발송
    public Notification resendNotification(Long notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            
            // Mock 재발송 로직
            boolean sendSuccess = mockSendNotification(notification);
            notification.setStatus(sendSuccess ? Notification.NotificationStatus.성공 : Notification.NotificationStatus.실패);
            notification.setSentAt(LocalDateTime.now());
            
            return notificationRepository.save(notification);
        }
        return null;
    }
    
    // 실패한 알림 조회
    public List<Notification> getFailedNotifications() {
        return notificationRepository.findFailedNotifications();
    }
    
    // 알림 통계 조회
    public List<Object[]> getNotificationStatsByType() {
        return notificationRepository.getNotificationStatsByType();
    }
    
    public List<Object[]> getNotificationStatsByStatus() {
        return notificationRepository.getNotificationStatsByStatus();
    }
    
    // 특정 기간 알림 조회
    public List<Notification> getNotificationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return notificationRepository.findBySentAtBetween(startDate, endDate);
    }
    
    // 알림 삭제
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
    
    // Mock 알림 발송 (실제 구현에서는 이메일/SMS 서비스 연동)
    private boolean mockSendNotification(Notification notification) {
        // Mock 로직: 90% 성공률
        return Math.random() > 0.1;
    }
    
    // 예약 확정 알림 발송
    public Notification sendReservationConfirmation(Long resId, String recipient) {
        return createAndSendNotification(resId, Notification.NotificationType.예약확정, recipient);
    }
    
    // 예약 취소 알림 발송
    public Notification sendReservationCancellation(Long resId, String recipient) {
        return createAndSendNotification(resId, Notification.NotificationType.예약취소, recipient);
    }
}
