package com.example.ClinicMate.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.ClinicMate.entity.Notification;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    
    private final JavaMailSender mailSender;
    private final NotificationService notificationService;
    
    // 간단한 텍스트 이메일 발송
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("gusrbs7534@gmail.com");
            
            mailSender.send(message);
            log.info("이메일 발송 성공: {}", to);
        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("이메일 발송에 실패했습니다: " + e.getMessage(), e);
        }
    }
    
    // HTML 이메일 발송 (간단한 HTML)
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("gusrbs7534@gmail.com");
            helper.setText(htmlContent, true);
            
            mailSender.send(message);
            log.info("HTML 이메일 발송 성공: {}", to);
        } catch (MessagingException e) {
            log.error("HTML 이메일 발송 실패: {}", e.getMessage(), e);
            throw new RuntimeException("HTML 이메일 발송에 실패했습니다: " + e.getMessage(), e);
        }
    }
    
    // 회원가입 환영 이메일
    public void sendWelcomeEmail(String to, String userName, Long userId) {
        String subject = "[ClinicMate] 회원가입을 환영합니다!";
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                    <div style="background-color: #007bff; color: #ffffff; padding: 20px; text-align: center; font-size: 24px; font-weight: bold;">
                        ClinicMate
                    </div>
                    <div style="padding: 30px; font-size: 16px;">
                        <h2 style="color: #007bff; text-align: center;">회원가입을 환영합니다!</h2>
                        <p>안녕하세요, <strong>%s</strong>님!</p>
                        <p>ClinicMate에 가입해주셔서 감사합니다.</p>
                        <p>이제 편리한 병원 예약 서비스를 이용하실 수 있습니다.</p>
                        <div style="text-align: center; margin-top: 30px;">
                            <a href="http://localhost:8080" style="background-color: #007bff; color: #ffffff; padding: 12px 25px; border-radius: 5px; text-decoration: none; font-weight: bold;">서비스 이용하기</a>
                        </div>
                    </div>
                    <div style="background-color: #f0f0f0; color: #777; text-align: center; padding: 20px; font-size: 12px;">
                        <p>© 2025 ClinicMate. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName);
        
        try {
            sendHtmlEmail(to, subject, htmlContent);
            // 성공 로깅
            notificationService.logNotificationSuccess(
                Notification.NotificationType.회원가입,
                to,
                Notification.RecipientType.USER,
                subject,
                htmlContent,
                userId,
                null,
                null
            );
        } catch (Exception e) {
            // 실패 로깅
            notificationService.logNotificationFailure(
                Notification.NotificationType.회원가입,
                to,
                Notification.RecipientType.USER,
                subject,
                htmlContent,
                e.getMessage(),
                userId,
                null,
                null
            );
            throw e;
        }
    }
    
    // 탈퇴 신청 확인 이메일
    public void sendWithdrawalRequestEmail(String to, String userName, Long userId) {
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                    <div style="background-color: #dc3545; color: #ffffff; padding: 20px; text-align: center; font-size: 24px; font-weight: bold;">
                        ClinicMate
                    </div>
                    <div style="padding: 30px; font-size: 16px;">
                        <h2 style="color: #dc3545; text-align: center;">탈퇴 신청이 접수되었습니다</h2>
                        <p>안녕하세요, <strong>%s</strong>님!</p>
                        <p>회원 탈퇴 신청이 정상적으로 접수되었습니다.</p>
                        <p>관리자 검토 후 탈퇴 처리가 완료됩니다.</p>
                        <div style="background-color: #fff3f5; border-left: 5px solid #dc3545; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #dc3545; margin-top: 0;">처리 절차</h3>
                            <ol>
                                <li>관리자 검토 (1-2일 소요)</li>
                                <li>탈퇴 승인 처리</li>
                                <li>완료 알림 이메일 발송</li>
                            </ol>
                        </div>
                    </div>
                    <div style="background-color: #f0f0f0; color: #777; text-align: center; padding: 20px; font-size: 12px;">
                        <p>© 2025 ClinicMate. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName);
        
        try {
            sendHtmlEmail(to, "[ClinicMate] 탈퇴 신청이 접수되었습니다", htmlContent);
            notificationService.logNotificationSuccess(
                Notification.NotificationType.탈퇴신청,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 탈퇴 신청이 접수되었습니다",
                htmlContent,
                userId,
                null,
                null
            );
        } catch (Exception e) {
            notificationService.logNotificationFailure(
                Notification.NotificationType.탈퇴신청,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 탈퇴 신청이 접수되었습니다",
                htmlContent,
                e.getMessage(),
                userId,
                null,
                null
            );
            throw e;
        }
    }
    
    // 탈퇴 승인 완료 이메일
    public void sendWithdrawalApprovalEmail(String to, String userName, Long userId) {
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                    <div style="background-color: #6c757d; color: #ffffff; padding: 20px; text-align: center; font-size: 24px; font-weight: bold;">
                        ClinicMate
                    </div>
                    <div style="padding: 30px; font-size: 16px;">
                        <h2 style="color: #6c757d; text-align: center;">탈퇴 처리가 완료되었습니다</h2>
                        <p>안녕하세요, <strong>%s</strong>님!</p>
                        <p>회원 탈퇴 처리가 완료되었습니다.</p>
                        <p>그동안 ClinicMate를 이용해주셔서 감사합니다.</p>
                        <div style="background-color: #e2e6ea; border-left: 5px solid #6c757d; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #6c757d; margin-top: 0;">처리 완료</h3>
                            <ul>
                                <li>회원 정보 삭제 완료</li>
                                <li>예약 내역 삭제 완료</li>
                                <li>개인정보 보호 정책에 따라 안전하게 처리됨</li>
                            </ul>
                        </div>
                    </div>
                    <div style="background-color: #f0f0f0; color: #777; text-align: center; padding: 20px; font-size: 12px;">
                        <p>© 2025 ClinicMate. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName);
        
        try {
            sendHtmlEmail(to, "[ClinicMate] 탈퇴 처리가 완료되었습니다", htmlContent);
            notificationService.logNotificationSuccess(
                Notification.NotificationType.탈퇴승인,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 탈퇴 처리가 완료되었습니다",
                htmlContent,
                userId,
                null,
                null
            );
        } catch (Exception e) {
            notificationService.logNotificationFailure(
                Notification.NotificationType.탈퇴승인,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 탈퇴 처리가 완료되었습니다",
                htmlContent,
                e.getMessage(),
                userId,
                null,
                null
            );
            throw e;
        }
    }
    
    // 관리자에게 탈퇴 신청 알림 이메일
    public void sendWithdrawalRequestNotificationToAdmin(String adminEmail, String userName, String userEmail, String userPhone, Long userId) {
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                    <div style="background-color: #ffc107; color: #000000; padding: 20px; text-align: center; font-size: 24px; font-weight: bold;">
                        ClinicMate 관리자 알림
                    </div>
                    <div style="padding: 30px; font-size: 16px;">
                        <h2 style="color: #ffc107; text-align: center;">회원 탈퇴 신청 알림</h2>
                        <p>안녕하세요, 관리자님!</p>
                        <p>새로운 회원 탈퇴 신청이 접수되었습니다.</p>
                        
                        <div style="background-color: #fff3cd; border-left: 5px solid #ffc107; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #856404; margin-top: 0;">탈퇴 신청 회원 정보</h3>
                            <ul style="list-style: none; padding: 0; margin: 0;">
                                <li style="margin-bottom: 8px;"><strong>이름:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>이메일:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>전화번호:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>신청일시:</strong> %s</li>
                            </ul>
                        </div>
                        
                        <div style="background-color: #e2e3e5; border-left: 5px solid #6c757d; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #495057; margin-top: 0;">처리 방법</h3>
                            <ol>
                                <li>관리자 페이지에 로그인</li>
                                <li>회원 관리 → 탈퇴 신청 목록 확인</li>
                                <li>해당 회원의 탈퇴 승인 또는 거부 처리</li>
                            </ol>
                        </div>
                        
                        <div style="text-align: center; margin-top: 30px;">
                            <a href="http://localhost:8080/admin" style="background-color: #ffc107; color: #000000; padding: 12px 25px; border-radius: 5px; text-decoration: none; font-weight: bold;">관리자 페이지로 이동</a>
                        </div>
                    </div>
                    <div style="background-color: #f0f0f0; color: #777; text-align: center; padding: 20px; font-size: 12px;">
                        <p>© 2025 ClinicMate. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, userEmail, userPhone, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        try {
            sendHtmlEmail(adminEmail, "[ClinicMate] 회원 탈퇴 신청 알림 - " + userName, htmlContent);
            notificationService.logNotificationSuccess(
                Notification.NotificationType.관리자_탈퇴신청,
                adminEmail,
                Notification.RecipientType.ADMIN,
                "[ClinicMate] 회원 탈퇴 신청 알림 - " + userName,
                htmlContent,
                userId,
                null,
                null
            );
        } catch (Exception e) {
            notificationService.logNotificationFailure(
                Notification.NotificationType.관리자_탈퇴신청,
                adminEmail,
                Notification.RecipientType.ADMIN,
                "[ClinicMate] 회원 탈퇴 신청 알림 - " + userName,
                htmlContent,
                e.getMessage(),
                userId,
                null,
                null
            );
            throw e;
        }
    }
    
    // 예약 완료 이메일 (사용자에게)
    public void sendReservationConfirmationEmail(String to, String userName, String hospitalName, String departmentName, String doctorName, String reservationDate, String reservationTime, Long userId, Long resId) {
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                    <div style="background-color: #28a745; color: #ffffff; padding: 20px; text-align: center; font-size: 24px; font-weight: bold;">
                        ClinicMate
                    </div>
                    <div style="padding: 30px; font-size: 16px;">
                        <h2 style="color: #28a745; text-align: center;">예약이 완료되었습니다!</h2>
                        <p>안녕하세요, <strong>%s</strong>님!</p>
                        <p>병원 예약이 성공적으로 완료되었습니다.</p>
                        
                        <div style="background-color: #d4edda; border-left: 5px solid #28a745; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #155724; margin-top: 0;">예약 정보</h3>
                            <ul style="list-style: none; padding: 0; margin: 0;">
                                <li style="margin-bottom: 8px;"><strong>병원:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>진료과:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>의사:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>예약일:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>예약시간:</strong> %s</li>
                            </ul>
                        </div>
                        
                        <div style="background-color: #e2e3e5; border-left: 5px solid #6c757d; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #495057; margin-top: 0;">안내사항</h3>
                            <ul>
                                <li>예약 시간 10분 전까지 병원에 도착해주세요</li>
                                <li>예약 변경이나 취소는 예약일 하루 전까지 가능합니다</li>
                                <li>문의사항이 있으시면 병원에 직접 연락해주세요</li>
                            </ul>
                        </div>
                    </div>
                    <div style="background-color: #f0f0f0; color: #777; text-align: center; padding: 20px; font-size: 12px;">
                        <p>© 2025 ClinicMate. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, hospitalName, departmentName, doctorName, reservationDate, reservationTime);
        
        try {
            sendHtmlEmail(to, "[ClinicMate] 예약 완료 - " + hospitalName, htmlContent);
            notificationService.logNotificationSuccess(
                Notification.NotificationType.예약완료,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 예약 완료 - " + hospitalName,
                htmlContent,
                userId,
                resId,
                null
            );
        } catch (Exception e) {
            notificationService.logNotificationFailure(
                Notification.NotificationType.예약완료,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 예약 완료 - " + hospitalName,
                htmlContent,
                e.getMessage(),
                userId,
                resId,
                null
            );
            throw e;
        }
    }
    
    // 결제 완료 이메일 (사용자에게)
    public void sendPaymentConfirmationEmail(String to, String userName, String hospitalName, String departmentName, String doctorName, String reservationDate, String amount, String paymentMethod, Long userId, Long payId, Long resId) {
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                    <div style="background-color: #17a2b8; color: #ffffff; padding: 20px; text-align: center; font-size: 24px; font-weight: bold;">
                        ClinicMate
                    </div>
                    <div style="padding: 30px; font-size: 16px;">
                        <h2 style="color: #17a2b8; text-align: center;">결제가 완료되었습니다!</h2>
                        <p>안녕하세요, <strong>%s</strong>님!</p>
                        <p>예약에 대한 결제가 성공적으로 완료되었습니다.</p>
                        
                        <div style="background-color: #d1ecf1; border-left: 5px solid #17a2b8; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #0c5460; margin-top: 0;">결제 정보</h3>
                            <ul style="list-style: none; padding: 0; margin: 0;">
                                <li style="margin-bottom: 8px;"><strong>병원:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>진료과:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>의사:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>예약일:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>결제금액:</strong> %s원</li>
                                <li style="margin-bottom: 8px;"><strong>결제방법:</strong> %s</li>
                            </ul>
                        </div>
                        
                        <div style="background-color: #e2e3e5; border-left: 5px solid #6c757d; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #495057; margin-top: 0;">안내사항</h3>
                            <ul>
                                <li>결제 영수증은 이메일로 별도 발송됩니다</li>
                                <li>예약 취소 시 환불 정책에 따라 처리됩니다</li>
                                <li>문의사항이 있으시면 고객센터로 연락해주세요</li>
                            </ul>
                        </div>
                    </div>
                    <div style="background-color: #f0f0f0; color: #777; text-align: center; padding: 20px; font-size: 12px;">
                        <p>© 2025 ClinicMate. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, hospitalName, departmentName, doctorName, reservationDate, amount, paymentMethod);
        
        try {
            sendHtmlEmail(to, "[ClinicMate] 결제 완료 - " + hospitalName, htmlContent);
            notificationService.logNotificationSuccess(
                Notification.NotificationType.결제완료,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 결제 완료 - " + hospitalName,
                htmlContent,
                userId,
                resId,
                payId
            );
        } catch (Exception e) {
            notificationService.logNotificationFailure(
                Notification.NotificationType.결제완료,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 결제 완료 - " + hospitalName,
                htmlContent,
                e.getMessage(),
                userId,
                resId,
                payId
            );
            throw e;
        }
    }
    
    // 예약 취소 이메일 (사용자에게)
    public void sendReservationCancellationEmail(String to, String userName, String hospitalName, String departmentName, String doctorName, String reservationDate, Long userId, Long resId) {
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                    <div style="background-color: #ffc107; color: #000000; padding: 20px; text-align: center; font-size: 24px; font-weight: bold;">
                        ClinicMate
                    </div>
                    <div style="padding: 30px; font-size: 16px;">
                        <h2 style="color: #856404; text-align: center;">예약이 취소되었습니다</h2>
                        <p>안녕하세요, <strong>%s</strong>님!</p>
                        <p>요청하신 예약이 성공적으로 취소되었습니다.</p>
                        
                        <div style="background-color: #fff3cd; border-left: 5px solid #ffc107; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #856404; margin-top: 0;">취소된 예약 정보</h3>
                            <ul style="list-style: none; padding: 0; margin: 0;">
                                <li style="margin-bottom: 8px;"><strong>병원:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>진료과:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>의사:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>예약일:</strong> %s</li>
                            </ul>
                        </div>
                        
                        <div style="background-color: #e2e3e5; border-left: 5px solid #6c757d; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #495057; margin-top: 0;">안내사항</h3>
                            <ul>
                                <li>결제가 완료된 예약의 경우 환불 절차가 진행됩니다</li>
                                <li>환불은 영업일 기준 3-5일 소요됩니다</li>
                                <li>새로운 예약은 언제든지 가능합니다</li>
                            </ul>
                        </div>
                    </div>
                    <div style="background-color: #f0f0f0; color: #777; text-align: center; padding: 20px; font-size: 12px;">
                        <p>© 2025 ClinicMate. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, hospitalName, departmentName, doctorName, reservationDate);
        
        try {
            sendHtmlEmail(to, "[ClinicMate] 예약 취소 - " + hospitalName, htmlContent);
            notificationService.logNotificationSuccess(
                Notification.NotificationType.예약취소,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 예약 취소 - " + hospitalName,
                htmlContent,
                userId,
                resId,
                null
            );
        } catch (Exception e) {
            notificationService.logNotificationFailure(
                Notification.NotificationType.예약취소,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 예약 취소 - " + hospitalName,
                htmlContent,
                e.getMessage(),
                userId,
                resId,
                null
            );
            throw e;
        }
    }
    
    // 결제 취소 이메일 (사용자에게)
    public void sendPaymentCancellationEmail(String to, String userName, String hospitalName, String amount, String refundAmount, Long userId, Long payId, Long resId) {
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                    <div style="background-color: #6c757d; color: #ffffff; padding: 20px; text-align: center; font-size: 24px; font-weight: bold;">
                        ClinicMate
                    </div>
                    <div style="padding: 30px; font-size: 16px;">
                        <h2 style="color: #495057; text-align: center;">결제가 취소되었습니다</h2>
                        <p>안녕하세요, <strong>%s</strong>님!</p>
                        <p>요청하신 결제 취소가 처리되었습니다.</p>
                        
                        <div style="background-color: #e2e6ea; border-left: 5px solid #6c757d; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #495057; margin-top: 0;">취소 정보</h3>
                            <ul style="list-style: none; padding: 0; margin: 0;">
                                <li style="margin-bottom: 8px;"><strong>병원:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>결제금액:</strong> %s원</li>
                                <li style="margin-bottom: 8px;"><strong>환불금액:</strong> %s원</li>
                                <li style="margin-bottom: 8px;"><strong>취소일시:</strong> %s</li>
                            </ul>
                        </div>
                        
                        <div style="background-color: #e2e3e5; border-left: 5px solid #6c757d; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #495057; margin-top: 0;">환불 안내</h3>
                            <ul>
                                <li>환불은 영업일 기준 3-5일 소요됩니다</li>
                                <li>카드 결제의 경우 다음 달 카드 대금에서 차감됩니다</li>
                                <li>문의사항이 있으시면 고객센터로 연락해주세요</li>
                            </ul>
                        </div>
                    </div>
                    <div style="background-color: #f0f0f0; color: #777; text-align: center; padding: 20px; font-size: 12px;">
                        <p>© 2025 ClinicMate. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, hospitalName, amount, refundAmount, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        try {
            sendHtmlEmail(to, "[ClinicMate] 결제 취소 - " + hospitalName, htmlContent);
            notificationService.logNotificationSuccess(
                Notification.NotificationType.결제취소,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 결제 취소 - " + hospitalName,
                htmlContent,
                userId,
                resId,
                payId
            );
        } catch (Exception e) {
            notificationService.logNotificationFailure(
                Notification.NotificationType.결제취소,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 결제 취소 - " + hospitalName,
                htmlContent,
                e.getMessage(),
                userId,
                resId,
                payId
            );
            throw e;
        }
    }
    
    // 탈퇴 취소 이메일 (사용자에게)
    public void sendWithdrawalCancellationEmail(String to, String userName, Long userId) {
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                    <div style="background-color: #28a745; color: #ffffff; padding: 20px; text-align: center; font-size: 24px; font-weight: bold;">
                        ClinicMate
                    </div>
                    <div style="padding: 30px; font-size: 16px;">
                        <h2 style="color: #28a745; text-align: center;">탈퇴 요청이 취소되었습니다</h2>
                        <p>안녕하세요, <strong>%s</strong>님!</p>
                        <p>회원 탈퇴 요청이 성공적으로 취소되었습니다.</p>
                        
                        <div style="background-color: #d4edda; border-left: 5px solid #28a745; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #155724; margin-top: 0;">계정 상태</h3>
                            <ul style="list-style: none; padding: 0; margin: 0;">
                                <li style="margin-bottom: 8px;"><strong>상태:</strong> 정상 이용 가능</li>
                                <li style="margin-bottom: 8px;"><strong>취소일시:</strong> %s</li>
                            </ul>
                        </div>
                        
                        <div style="background-color: #e2e3e5; border-left: 5px solid #6c757d; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #495057; margin-top: 0;">안내사항</h3>
                            <ul>
                                <li>이제 정상적으로 모든 서비스를 이용하실 수 있습니다</li>
                                <li>예약, 결제 등 모든 기능이 정상 작동합니다</li>
                                <li>문의사항이 있으시면 언제든 연락해주세요</li>
                            </ul>
                        </div>
                        
                        <div style="text-align: center; margin-top: 30px;">
                            <a href="http://localhost:8080" style="background-color: #28a745; color: #ffffff; padding: 12px 25px; border-radius: 5px; text-decoration: none; font-weight: bold;">서비스 이용하기</a>
                        </div>
                    </div>
                    <div style="background-color: #f0f0f0; color: #777; text-align: center; padding: 20px; font-size: 12px;">
                        <p>© 2025 ClinicMate. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        try {
            sendHtmlEmail(to, "[ClinicMate] 탈퇴 요청 취소", htmlContent);
            notificationService.logNotificationSuccess(
                Notification.NotificationType.탈퇴취소,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 탈퇴 요청 취소",
                htmlContent,
                userId,
                null,
                null
            );
        } catch (Exception e) {
            notificationService.logNotificationFailure(
                Notification.NotificationType.탈퇴취소,
                to,
                Notification.RecipientType.USER,
                "[ClinicMate] 탈퇴 요청 취소",
                htmlContent,
                e.getMessage(),
                userId,
                null,
                null
            );
            throw e;
        }
    }
    
    // 관리자에게 예약 취소 알림 이메일
    public void sendReservationCancellationNotificationToAdmin(String adminEmail, String userName, String userEmail, String hospitalName, String departmentName, String doctorName, String reservationDate, Long userId, Long resId) {
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                    <div style="background-color: #ffc107; color: #000000; padding: 20px; text-align: center; font-size: 24px; font-weight: bold;">
                        ClinicMate 관리자 알림
                    </div>
                    <div style="padding: 30px; font-size: 16px;">
                        <h2 style="color: #ffc107; text-align: center;">예약 취소 알림</h2>
                        <p>안녕하세요, 관리자님!</p>
                        <p>회원이 예약을 취소했습니다.</p>
                        
                        <div style="background-color: #fff3cd; border-left: 5px solid #ffc107; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #856404; margin-top: 0;">취소된 예약 정보</h3>
                            <ul style="list-style: none; padding: 0; margin: 0;">
                                <li style="margin-bottom: 8px;"><strong>회원명:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>이메일:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>병원:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>진료과:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>의사:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>예약일:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>취소일시:</strong> %s</li>
                            </ul>
                        </div>
                        
                        <div style="text-align: center; margin-top: 30px;">
                            <a href="http://localhost:8080/admin" style="background-color: #ffc107; color: #000000; padding: 12px 25px; border-radius: 5px; text-decoration: none; font-weight: bold;">관리자 페이지로 이동</a>
                        </div>
                    </div>
                    <div style="background-color: #f0f0f0; color: #777; text-align: center; padding: 20px; font-size: 12px;">
                        <p>© 2025 ClinicMate. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, userEmail, hospitalName, departmentName, doctorName, reservationDate, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        try {
            sendHtmlEmail(adminEmail, "[ClinicMate] 예약 취소 알림 - " + userName, htmlContent);
            notificationService.logNotificationSuccess(
                Notification.NotificationType.관리자_예약취소,
                adminEmail,
                Notification.RecipientType.ADMIN,
                "[ClinicMate] 예약 취소 알림 - " + userName,
                htmlContent,
                userId,
                resId,
                null
            );
        } catch (Exception e) {
            notificationService.logNotificationFailure(
                Notification.NotificationType.관리자_예약취소,
                adminEmail,
                Notification.RecipientType.ADMIN,
                "[ClinicMate] 예약 취소 알림 - " + userName,
                htmlContent,
                e.getMessage(),
                userId,
                resId,
                null
            );
            throw e;
        }
    }
    
    // 관리자에게 결제 취소 알림 이메일
    public void sendPaymentCancellationNotificationToAdmin(String adminEmail, String userName, String userEmail, String hospitalName, String amount, String refundAmount, Long userId, Long payId, Long resId) {
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                    <div style="background-color: #6c757d; color: #ffffff; padding: 20px; text-align: center; font-size: 24px; font-weight: bold;">
                        ClinicMate 관리자 알림
                    </div>
                    <div style="padding: 30px; font-size: 16px;">
                        <h2 style="color: #6c757d; text-align: center;">결제 취소 알림</h2>
                        <p>안녕하세요, 관리자님!</p>
                        <p>회원이 결제를 취소했습니다.</p>
                        
                        <div style="background-color: #e2e6ea; border-left: 5px solid #6c757d; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #495057; margin-top: 0;">취소된 결제 정보</h3>
                            <ul style="list-style: none; padding: 0; margin: 0;">
                                <li style="margin-bottom: 8px;"><strong>회원명:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>이메일:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>병원:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>결제금액:</strong> %s원</li>
                                <li style="margin-bottom: 8px;"><strong>환불금액:</strong> %s원</li>
                                <li style="margin-bottom: 8px;"><strong>취소일시:</strong> %s</li>
                            </ul>
                        </div>
                        
                        <div style="text-align: center; margin-top: 30px;">
                            <a href="http://localhost:8080/admin" style="background-color: #6c757d; color: #ffffff; padding: 12px 25px; border-radius: 5px; text-decoration: none; font-weight: bold;">관리자 페이지로 이동</a>
                        </div>
                    </div>
                    <div style="background-color: #f0f0f0; color: #777; text-align: center; padding: 20px; font-size: 12px;">
                        <p>© 2025 ClinicMate. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, userEmail, hospitalName, amount, refundAmount, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        try {
            sendHtmlEmail(adminEmail, "[ClinicMate] 결제 취소 알림 - " + userName, htmlContent);
            notificationService.logNotificationSuccess(
                Notification.NotificationType.관리자_결제취소,
                adminEmail,
                Notification.RecipientType.ADMIN,
                "[ClinicMate] 결제 취소 알림 - " + userName,
                htmlContent,
                userId,
                resId,
                payId
            );
        } catch (Exception e) {
            notificationService.logNotificationFailure(
                Notification.NotificationType.관리자_결제취소,
                adminEmail,
                Notification.RecipientType.ADMIN,
                "[ClinicMate] 결제 취소 알림 - " + userName,
                htmlContent,
                e.getMessage(),
                userId,
                resId,
                payId
            );
            throw e;
        }
    }
    
    // 관리자에게 탈퇴 취소 알림 이메일
    public void sendWithdrawalCancellationNotificationToAdmin(String adminEmail, String userName, String userEmail, Long userId) {
        String htmlContent = String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);">
                    <div style="background-color: #28a745; color: #ffffff; padding: 20px; text-align: center; font-size: 24px; font-weight: bold;">
                        ClinicMate 관리자 알림
                    </div>
                    <div style="padding: 30px; font-size: 16px;">
                        <h2 style="color: #28a745; text-align: center;">탈퇴 요청 취소 알림</h2>
                        <p>안녕하세요, 관리자님!</p>
                        <p>회원이 탈퇴 요청을 취소했습니다.</p>
                        
                        <div style="background-color: #d4edda; border-left: 5px solid #28a745; padding: 15px 20px; margin: 20px 0; border-radius: 5px;">
                            <h3 style="color: #155724; margin-top: 0;">회원 정보</h3>
                            <ul style="list-style: none; padding: 0; margin: 0;">
                                <li style="margin-bottom: 8px;"><strong>회원명:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>이메일:</strong> %s</li>
                                <li style="margin-bottom: 8px;"><strong>상태:</strong> 정상 이용</li>
                                <li style="margin-bottom: 8px;"><strong>취소일시:</strong> %s</li>
                            </ul>
                        </div>
                        
                        <div style="text-align: center; margin-top: 30px;">
                            <a href="http://localhost:8080/admin" style="background-color: #28a745; color: #ffffff; padding: 12px 25px; border-radius: 5px; text-decoration: none; font-weight: bold;">관리자 페이지로 이동</a>
                        </div>
                    </div>
                    <div style="background-color: #f0f0f0; color: #777; text-align: center; padding: 20px; font-size: 12px;">
                        <p>© 2025 ClinicMate. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, userEmail, java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        try {
            sendHtmlEmail(adminEmail, "[ClinicMate] 탈퇴 요청 취소 알림 - " + userName, htmlContent);
            notificationService.logNotificationSuccess(
                Notification.NotificationType.관리자_탈퇴취소,
                adminEmail,
                Notification.RecipientType.ADMIN,
                "[ClinicMate] 탈퇴 요청 취소 알림 - " + userName,
                htmlContent,
                userId,
                null,
                null
            );
        } catch (Exception e) {
            notificationService.logNotificationFailure(
                Notification.NotificationType.관리자_탈퇴취소,
                adminEmail,
                Notification.RecipientType.ADMIN,
                "[ClinicMate] 탈퇴 요청 취소 알림 - " + userName,
                htmlContent,
                e.getMessage(),
                userId,
                null,
                null
            );
            throw e;
        }
    }
}
