package com.example.ClinicMate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATION")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noti_id")
    private Long notiId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "res_id")
    private Long resId;
    
    @Column(name = "pay_id")
    private Long payId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;
    
    @Column(name = "recipient", nullable = false, length = 100)
    private String recipient;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type")
    private RecipientType recipientType = RecipientType.USER;
    
    @Column(name = "subject", length = 200)
    private String subject;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status = NotificationStatus.성공;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    // Enum 정의
    public enum NotificationType {
        회원가입, 예약완료, 예약취소, 결제완료, 결제취소, 
        탈퇴신청, 탈퇴승인, 탈퇴취소, 
        관리자_예약취소, 관리자_결제취소, 관리자_탈퇴신청, 관리자_탈퇴취소
    }
    
    public enum RecipientType {
        USER, ADMIN
    }
    
    public enum NotificationStatus {
        성공, 실패
    }
}
