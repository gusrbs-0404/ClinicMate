package com.example.ClinicMate.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ClinicMate.entity.User;
import com.example.ClinicMate.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users/action")
@RequiredArgsConstructor
@Slf4j
public class UsesActionController {
    
    private final UserService userService;

	// POST: 회원가입 액션
	@PostMapping("/signup")
	public ResponseEntity<Map<String, Object>> actionSignup(
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("name") String name,
			@RequestParam("email") String email,
			@RequestParam("phone") String phone
	) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			// User 객체 생성
			User user = User.builder()
					.username(username)
					.password(password)
					.name(name)
					.email(email)
					.phone(phone)
					.role(User.UserRole.PATIENT)
					.build();
			
			// 회원가입 처리
			User savedUser = userService.signup(user);
			
			response.put("success", true);
			response.put("message", "회원가입이 완료되었습니다.");
			response.put("userId", savedUser.getUserId());
			
			log.info("회원가입 성공: {}", username);
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", e.getMessage());
			
			log.error("회원가입 실패: {}", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

	// POST: 로그인 액션 (이메일 인증코드 입력은 추후 구현)
	@PostMapping("/signin")
	public ResponseEntity<Map<String, Object>> actionSignin(
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam(value = "code", required = false) String code,
			HttpSession session
	) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			// 로그인 처리
			User user = userService.signin(username, password)
					.orElseThrow(() -> new RuntimeException("아이디 또는 비밀번호가 올바르지 않습니다."));
			
			// 세션에 사용자 정보 저장
			session.setAttribute("authUser", user.getUsername());
			session.setAttribute("userId", user.getUserId());
			session.setAttribute("role", user.getRole().name());
			
			// 사용자 권한에 따른 리다이렉트 URL 설정
			String redirectUrl;
			if (user.getRole() == User.UserRole.ADMIN) {
				redirectUrl = "/admin";
			} else {
				redirectUrl = "/main";
			}
			
			response.put("success", true);
			response.put("message", "로그인되었습니다.");
			response.put("redirectUrl", redirectUrl);
			response.put("user", Map.of(
					"userId", user.getUserId(),
					"username", user.getUsername(),
					"name", user.getName(),
					"email", user.getEmail(),
					"phone", user.getPhone() != null ? user.getPhone() : "",
					"role", user.getRole().name()
			));
			
			log.info("로그인 성공: {} (권한: {})", username, user.getRole());
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", e.getMessage());
			
			log.error("로그인 실패: {}", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

	// DELETE: 로그아웃
	@DeleteMapping("/signout")
	public ResponseEntity<Map<String, Object>> actionSignout(HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			String username = (String) session.getAttribute("authUser");
			
			// 세션 무효화
			session.invalidate();
			
			response.put("success", true);
			response.put("message", "로그아웃되었습니다.");
			
			log.info("로그아웃 성공: {}", username);
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "로그아웃 중 오류가 발생했습니다.");
			
			log.error("로그아웃 실패: {}", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

            // PUT: 회원정보 수정
            @PutMapping("/edit")
            public ResponseEntity<Map<String, Object>> actionEdit(
                    @RequestParam("currentPassword") String currentPassword,
                    @RequestParam(value = "password", required = false) String password,
                    @RequestParam(value = "name", required = false) String name,
                    @RequestParam(value = "phone", required = false) String phone,
                    @RequestParam(value = "email", required = false) String email,
                    HttpSession session
            ) {
                Map<String, Object> response = new HashMap<>();

                try {
                    // 세션에서 사용자 ID 조회
                    Long userId = (Long) session.getAttribute("userId");
                    if (userId == null) {
                        response.put("success", false);
                        response.put("message", "로그인이 필요합니다.");
                        return ResponseEntity.badRequest().body(response);
                    }

                    // 현재 사용자 정보 조회
                    User currentUser = userService.findById(userId)
                            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

                    // 현재 비밀번호 검증 (항상 필수)
                    if (currentPassword == null || currentPassword.trim().isEmpty()) {
                        response.put("success", false);
                        response.put("message", "현재 비밀번호를 입력해주세요.");
                        return ResponseEntity.badRequest().body(response);
                    }

                    if (!currentUser.getPassword().equals(currentPassword)) {
                        response.put("success", false);
                        response.put("message", "현재 비밀번호가 일치하지 않습니다.");
                        return ResponseEntity.badRequest().body(response);
                    }
			
			// 수정할 사용자 정보 생성
			User updateUser = User.builder()
					.password(password)
					.name(name)
					.phone(phone)
					.email(email)
					.build();
			
			// 회원정보 수정 처리
			User updatedUser = userService.updateUser(userId, updateUser);
			
			response.put("success", true);
			response.put("message", "회원정보가 수정되었습니다.");
			response.put("user", Map.of(
					"userId", updatedUser.getUserId(),
					"username", updatedUser.getUsername(),
					"name", updatedUser.getName(),
					"email", updatedUser.getEmail(),
					"phone", updatedUser.getPhone(),
					"role", updatedUser.getRole().name()
			));
			
			log.info("회원정보 수정 성공: {}", updatedUser.getUsername());
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", e.getMessage());
			
			log.error("회원정보 수정 실패: {}", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

            // POST: 회원 탈퇴 신청
            @PostMapping("/withdraw")
            public ResponseEntity<Map<String, Object>> actionWithdraw(
                    @RequestParam("password") String password,
                    HttpSession session
            ) {
                Map<String, Object> response = new HashMap<>();

                try {
                    // 세션에서 사용자 ID 조회
                    Long userId = (Long) session.getAttribute("userId");
                    if (userId == null) {
                        response.put("success", false);
                        response.put("message", "로그인이 필요합니다.");
                        return ResponseEntity.badRequest().body(response);
                    }

                    // 사용자 정보 조회
                    User user = userService.findById(userId)
                            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

                    // 비밀번호 확인 (필수)
                    if (password == null || password.trim().isEmpty()) {
                        response.put("success", false);
                        response.put("message", "비밀번호를 입력해주세요.");
                        return ResponseEntity.badRequest().body(response);
                    }

                    if (!user.getPassword().equals(password)) {
                        response.put("success", false);
                        response.put("message", "비밀번호가 일치하지 않습니다.");
                        return ResponseEntity.badRequest().body(response);
                    }
			
			// 회원 탈퇴 처리 (데이터베이스에서 삭제)
			userService.deleteUser(userId);
			
			// 세션 무효화
			session.invalidate();
			
			response.put("success", true);
			response.put("message", "회원 탈퇴가 완료되었습니다.");
			
			log.info("회원 탈퇴 성공: {}", user.getUsername());
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", e.getMessage());
			
			log.error("회원 탈퇴 실패: {}", e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}
}
