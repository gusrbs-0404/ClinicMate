package com.example.ClinicMate.service;

import com.example.ClinicMate.entity.Notification;
import com.example.ClinicMate.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    // 알림 로깅 (성공)
    public Notification logNotificationSuccess(
            Notification.NotificationType type,
            String recipient,
            Notification.RecipientType recipientType,
            String subject,
            String content,
            Long userId,
            Long resId,
            Long payId) {
        
        Notification notification = Notification.builder()
                .type(type)
                .recipient(recipient)
                .recipientType(recipientType)
                .subject(subject)
                .content(content)
                .status(Notification.NotificationStatus.성공)
                .userId(userId)
                .resId(resId)
                .payId(payId)
                .sentAt(LocalDateTime.now())
                .build();
        
        return notificationRepository.save(notification);
    }
    
    // 알림 로깅 (실패)
    public Notification logNotificationFailure(
            Notification.NotificationType type,
            String recipient,
            Notification.RecipientType recipientType,
            String subject,
            String content,
            String errorMessage,
            Long userId,
            Long resId,
            Long payId) {
        
        Notification notification = Notification.builder()
                .type(type)
                .recipient(recipient)
                .recipientType(recipientType)
                .subject(subject)
                .content(content)
                .status(Notification.NotificationStatus.실패)
                .errorMessage(errorMessage)
                .userId(userId)
                .resId(resId)
                .payId(payId)
                .sentAt(LocalDateTime.now())
                .build();
        
        return notificationRepository.save(notification);
    }
    
    // 모든 알림 조회 (페이징)
    @Transactional(readOnly = true)
    public Map<String, Object> getAllNotificationsWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository.findAllByOrderBySentAtDesc(pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", notificationPage.getContent());
        result.put("totalElements", notificationPage.getTotalElements());
        result.put("totalPages", notificationPage.getTotalPages());
        result.put("currentPage", page);
        result.put("size", size);
        result.put("hasNext", notificationPage.hasNext());
        result.put("hasPrevious", notificationPage.hasPrevious());
        
        return result;
    }
    
    // 실패한 알림만 조회 (페이징)
    @Transactional(readOnly = true)
    public Map<String, Object> getFailedNotificationsWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notificationPage = notificationRepository.findByStatusOrderBySentAtDesc(
                Notification.NotificationStatus.실패, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", notificationPage.getContent());
        result.put("totalElements", notificationPage.getTotalElements());
        result.put("totalPages", notificationPage.getTotalPages());
        result.put("currentPage", page);
        result.put("size", size);
        result.put("hasNext", notificationPage.hasNext());
        result.put("hasPrevious", notificationPage.hasPrevious());
        
        return result;
    }
    
    // 알림 타입별 조회
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByType(Notification.NotificationType type) {
        return notificationRepository.findByTypeOrderBySentAtDesc(type);
    }
    
    // 수신자별 조회
    @Transactional(readOnly = true)
    public List<Notification> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipientOrderBySentAtDesc(recipient);
    }
    
    // 모든 알림 조회 (페이징 없이)
    @Transactional(readOnly = true)
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllByOrderBySentAtDesc();
    }
    
    // 알림 재발송
    @Transactional
    public void resendNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
        
        if (notification.getStatus() != Notification.NotificationStatus.실패) {
            throw new RuntimeException("실패한 알림만 재발송할 수 있습니다.");
        }
        
        // 재발송 로직 (실제로는 MailService를 통해 재발송)
        // 여기서는 단순히 상태만 업데이트
        notification.setStatus(Notification.NotificationStatus.성공);
        notification.setErrorMessage(null);
        notification.setSentAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
    }
    
    // 알림 통계
    @Transactional(readOnly = true)
    public Map<String, Object> getNotificationStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 전체 알림 수
        long totalNotifications = notificationRepository.count();
        stats.put("totalNotifications", totalNotifications);
        
        // 성공한 알림 수
        long successNotifications = notificationRepository.countByStatus(Notification.NotificationStatus.성공);
        stats.put("successNotifications", successNotifications);
        
        // 실패한 알림 수
        long failedNotifications = notificationRepository.countByStatus(Notification.NotificationStatus.실패);
        stats.put("failedNotifications", failedNotifications);
        
        // 성공률 계산
        double successRate = totalNotifications > 0 ? (double) successNotifications / totalNotifications * 100 : 0;
        stats.put("successRate", Math.round(successRate * 100.0) / 100.0);
        
        // 타입별 통계
        Map<String, Long> typeStats = new HashMap<>();
        for (Notification.NotificationType type : Notification.NotificationType.values()) {
            long count = notificationRepository.countByType(type);
            typeStats.put(type.name(), count);
        }
        stats.put("typeStatistics", typeStats);
        
        return stats;
    }
    
    // 알림 삭제
    @Transactional
    public void deleteNotification(Long notiId) {
        notificationRepository.deleteById(notiId);
    }
    
    // 실패한 알림 재전송을 위한 정보 조회
    @Transactional(readOnly = true)
    public Notification getNotificationById(Long notiId) {
        return notificationRepository.findById(notiId).orElse(null);
    }
}