package com.example.ClinicMate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.ClinicMate.entity.User;
import com.example.ClinicMate.entity.Reservation;
import com.example.ClinicMate.service.UserService;
import com.example.ClinicMate.service.ReservationService;

import java.util.List;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;
    private final ReservationService reservationService;

	// GET: 회원가입 페이지
	@GetMapping("/signup")
	public String getSignupPage() {
		return "user/signup"; // -> /WEB-INF/views/user/signup.html
	}

	// GET: 로그인 페이지
	@GetMapping("/signin")
	public String getSigninPage() {
		return "user/signin"; // -> /WEB-INF/views/user/signin.html
	}

	// GET: 내 정보 수정 페이지 (마이페이지)
	@GetMapping("/me")
	public String getMyPage(Model model, HttpSession session) {
		// 세션에서 사용자 ID 조회
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			// 로그인하지 않은 경우 로그인 페이지로 리다이렉트
			return "redirect:/users/signin";
		}
		
		try {
			// 데이터베이스에서 사용자 정보 조회
			User user = userService.findById(userId)
					.orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
			
			// 사용자의 예약 목록 조회
			List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
			
			// LocalDateTime을 Date로 변환하여 전달
			java.util.Date createdAtDate = java.sql.Timestamp.valueOf(user.getCreatedAt());
			model.addAttribute("user", user);
			model.addAttribute("createdAtDate", createdAtDate);
			model.addAttribute("reservations", reservations);
		} catch (Exception e) {
			// 사용자 정보 조회 실패 시 로그인 페이지로 리다이렉트
			return "redirect:/users/signin";
		}
		
		return "user/mypage";
	}

	// GET: 회원정보 수정 페이지
	@GetMapping("/edit-profile")
	public String getEditProfilePage(Model model, HttpSession session) {
		// 세션에서 사용자 ID 조회
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			// 로그인하지 않은 경우 로그인 페이지로 리다이렉트
			return "redirect:/users/signin";
		}
		
		try {
			// 데이터베이스에서 사용자 정보 조회
			User user = userService.findById(userId)
					.orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
			model.addAttribute("user", user);
		} catch (Exception e) {
			// 사용자 정보 조회 실패 시 로그인 페이지로 리다이렉트
			return "redirect:/users/signin";
		}
		
		return "user/edit-profile";
	}

	// GET: 회원 탈퇴 페이지
	@GetMapping("/withdraw")
	public String getWithdrawPage(Model model, HttpSession session) {
		// 세션에서 사용자 ID 조회
		Long userId = (Long) session.getAttribute("userId");
		if (userId == null) {
			// 로그인하지 않은 경우 로그인 페이지로 리다이렉트
			return "redirect:/users/signin";
		}
		
		try {
			// 데이터베이스에서 사용자 정보 조회
			User user = userService.findById(userId)
					.orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));
			model.addAttribute("user", user);
		} catch (Exception e) {
			// 사용자 정보 조회 실패 시 로그인 페이지로 리다이렉트
			return "redirect:/users/signin";
		}
		
		return "user/withdraw";
	}
}
