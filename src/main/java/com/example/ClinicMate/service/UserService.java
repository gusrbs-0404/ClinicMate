package com.example.ClinicMate.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ClinicMate.entity.User;
import com.example.ClinicMate.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    
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
        
        return userRepository.save(user);
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
}
