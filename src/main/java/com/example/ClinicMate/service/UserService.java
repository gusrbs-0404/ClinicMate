package com.example.ClinicMate.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ClinicMate.entity.User;
import com.example.ClinicMate.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final MailService mailService;
    
    @Value("${admin.email}")
    private String adminEmail;
    
    // 회원가입
    @Transactional
    public User signup(User user) {
        // 중복 검사
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("이미 사용 중인 아이디입니다.");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }
        
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new RuntimeException("이미 사용 중인 전화번호입니다.");
        }
        
        // 기본 역할 설정
        if (user.getRole() == null) {
            user.setRole(User.UserRole.PATIENT);
        }
        
        // 기본 탈퇴 상태 설정
        if (user.getWithdrawalStatus() == null) {
            user.setWithdrawalStatus(User.WithdrawalStatus.ACTIVE);
        }
        
        User savedUser = userRepository.save(user);
        
        // 회원가입 환영 이메일 발송
        try {
            mailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getName(), savedUser.getUserId());
            log.info("회원가입 환영 이메일 발송 성공: {}", savedUser.getEmail());
        } catch (Exception e) {
            log.error("회원가입 환영 이메일 발송 실패: {}", e.getMessage());
            // 이메일 발송 실패해도 회원가입은 성공으로 처리
        }
        
        return savedUser;
    }
    
    // 로그인
    public Optional<User> signin(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password);
    }
    
    // 사용자 정보 조회
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }
    
    // username으로 사용자 조회
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // 이메일로 사용자 조회
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // 회원정보 수정
    @Transactional
    public User updateUser(Long userId, User updateUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        // 수정 가능한 필드만 업데이트
        if (updateUser.getName() != null && !updateUser.getName().trim().isEmpty()) {
            user.setName(updateUser.getName());
        }
        
        if (updateUser.getPhone() != null && !updateUser.getPhone().trim().isEmpty()) {
            // 전화번호 중복 검사
            if (!user.getPhone().equals(updateUser.getPhone()) && 
                userRepository.existsByPhone(updateUser.getPhone())) {
                throw new RuntimeException("이미 사용 중인 전화번호입니다.");
            }
            user.setPhone(updateUser.getPhone());
        }
        
        if (updateUser.getEmail() != null && !updateUser.getEmail().trim().isEmpty()) {
            // 이메일 중복 검사
            if (!user.getEmail().equals(updateUser.getEmail()) && 
                userRepository.existsByEmail(updateUser.getEmail())) {
                throw new RuntimeException("이미 사용 중인 이메일입니다.");
            }
            user.setEmail(updateUser.getEmail());
        }
        
        if (updateUser.getPassword() != null && !updateUser.getPassword().trim().isEmpty()) {
            user.setPassword(updateUser.getPassword());
        }
        
        return userRepository.save(user);
    }
    
    // 회원 탈퇴
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        userRepository.delete(user);
    }
    
    // 중복 검사 메서드들
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }
    
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
    
    public boolean isPhoneAvailable(String phone) {
        return !userRepository.existsByPhone(phone);
    }
    
    // 관리자용 메서드들
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public long getTotalUsers() {
        return userRepository.count();
    }
    
    @Transactional
    public User updateUserRole(Long userId, User.UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        user.setRole(role);
        return userRepository.save(user);
    }
    
    // 탈퇴 요청
    @Transactional
    public User requestWithdrawal(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        if (user.getWithdrawalStatus() != User.WithdrawalStatus.ACTIVE) {
            throw new RuntimeException("이미 탈퇴 요청이 처리되었거나 탈퇴된 사용자입니다.");
        }
        
        user.setWithdrawalStatus(User.WithdrawalStatus.WITHDRAWAL_REQUESTED);
        User savedUser = userRepository.save(user);
        
        // 탈퇴 신청 확인 이메일 발송 (사용자에게)
        try {
            mailService.sendWithdrawalRequestEmail(savedUser.getEmail(), savedUser.getName(), savedUser.getUserId());
            log.info("탈퇴 신청 확인 이메일 발송 성공: {}", savedUser.getEmail());
        } catch (Exception e) {
            log.error("탈퇴 신청 확인 이메일 발송 실패: {}", e.getMessage());
            // 이메일 발송 실패해도 탈퇴 신청은 성공으로 처리
        }
        
        // 관리자에게 탈퇴 신청 알림 이메일 발송
        try {
            mailService.sendWithdrawalRequestNotificationToAdmin(
                adminEmail, 
                savedUser.getName(), 
                savedUser.getEmail(), 
                savedUser.getPhone(),
                savedUser.getUserId()
            );
            log.info("관리자 탈퇴 신청 알림 이메일 발송 성공: {}", adminEmail);
        } catch (Exception e) {
            log.error("관리자 탈퇴 신청 알림 이메일 발송 실패: {}", e.getMessage());
            // 이메일 발송 실패해도 탈퇴 신청은 성공으로 처리
        }
        
        return savedUser;
    }
    
    // 탈퇴 승인 (관리자용)
    @Transactional
    public void approveWithdrawal(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        if (user.getWithdrawalStatus() != User.WithdrawalStatus.WITHDRAWAL_REQUESTED) {
            throw new RuntimeException("탈퇴 요청이 없는 사용자입니다.");
        }
        
        // 탈퇴 승인 완료 이메일 발송 (삭제 전에 발송)
        try {
            mailService.sendWithdrawalApprovalEmail(user.getEmail(), user.getName(), user.getUserId());
            log.info("탈퇴 승인 완료 이메일 발송 성공: {}", user.getEmail());
        } catch (Exception e) {
            log.error("탈퇴 승인 완료 이메일 발송 실패: {}", e.getMessage());
            // 이메일 발송 실패해도 탈퇴 승인은 진행
        }
        
        userRepository.delete(user);
    }
    
    // 탈퇴 요청 취소
    @Transactional
    public User cancelWithdrawalRequest(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        if (user.getWithdrawalStatus() != User.WithdrawalStatus.WITHDRAWAL_REQUESTED) {
            throw new RuntimeException("탈퇴 요청이 없는 사용자입니다.");
        }
        
        user.setWithdrawalStatus(User.WithdrawalStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        
        // 탈퇴 취소 이메일 발송 (사용자에게)
        try {
            mailService.sendWithdrawalCancellationEmail(savedUser.getEmail(), savedUser.getName(), savedUser.getUserId());
            log.info("탈퇴 취소 이메일 발송 성공: {}", savedUser.getEmail());
        } catch (Exception e) {
            log.error("탈퇴 취소 이메일 발송 실패: {}", e.getMessage());
            // 이메일 발송 실패해도 탈퇴 취소는 성공으로 처리
        }
        
        // 관리자에게 탈퇴 취소 알림 이메일 발송
        try {
            mailService.sendWithdrawalCancellationNotificationToAdmin(
                adminEmail,
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getUserId()
            );
            log.info("관리자 탈퇴 취소 알림 이메일 발송 성공: {}", adminEmail);
        } catch (Exception e) {
            log.error("관리자 탈퇴 취소 알림 이메일 발송 실패: {}", e.getMessage());
            // 이메일 발송 실패해도 탈퇴 취소는 성공으로 처리
        }
        
        return savedUser;
    }
    
    // 탈퇴 요청된 사용자 목록 조회 (관리자용)
    public List<User> getUsersWithWithdrawalRequest() {
        return userRepository.findByWithdrawalStatus(User.WithdrawalStatus.WITHDRAWAL_REQUESTED);
    }
    
    // 페이징된 회원 목록 조회
    public Map<String, Object> getUsersWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", userPage.getContent());
        result.put("totalElements", userPage.getTotalElements());
        result.put("totalPages", userPage.getTotalPages());
        result.put("currentPage", page);
        result.put("size", size);
        result.put("hasNext", userPage.hasNext());
        result.put("hasPrevious", userPage.hasPrevious());
        
        return result;
    }
}
