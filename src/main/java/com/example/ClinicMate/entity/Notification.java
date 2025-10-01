package com.example.ClinicMate.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATION")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noti_id")
    private Long notiId;
    
    @Column(name = "res_id", nullable = false)
    private Long resId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;
    
    @Column(name = "recipient", nullable = false, length = 100)
    private String recipient;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status = NotificationStatus.성공;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    // 생성자
    public Notification() {
        this.sentAt = LocalDateTime.now();
    }
    
    public Notification(Long resId, NotificationType type, String recipient) {
        this();
        this.resId = resId;
        this.type = type;
        this.recipient = recipient;
    }
    
    // Getters and Setters
    public Long getNotiId() {
        return notiId;
    }
    
    public void setNotiId(Long notiId) {
        this.notiId = notiId;
    }
    
    public Long getResId() {
        return resId;
    }
    
    public void setResId(Long resId) {
        this.resId = resId;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    public NotificationStatus getStatus() {
        return status;
    }
    
    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getSentAt() {
        return sentAt;
    }
    
    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
    
    // Enum 정의
    public enum NotificationType {
        예약확정, 예약취소
    }
    
    public enum NotificationStatus {
        성공, 실패
    }
}
