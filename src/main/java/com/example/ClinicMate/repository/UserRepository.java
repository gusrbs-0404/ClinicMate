package com.example.ClinicMate.repository;

import com.example.ClinicMate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // username으로 사용자 찾기 (로그인 ID로 사용)
    Optional<User> findByUsername(String username);
    
    // 이메일로 사용자 찾기
    Optional<User> findByEmail(String email);
    
    // 전화번호로 사용자 찾기
    Optional<User> findByPhone(String phone);
    
    // username 중복 확인
    boolean existsByUsername(String username);
    
    // 이메일 중복 확인
    boolean existsByEmail(String email);
    
    // 전화번호 중복 확인
    boolean existsByPhone(String phone);
    
    // username과 비밀번호로 로그인 확인
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.password = :password")
    Optional<User> findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);
    
    // 탈퇴 상태별 사용자 조회
    List<User> findByWithdrawalStatus(User.WithdrawalStatus withdrawalStatus);
}
