package com.example.ClinicMate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UsersController {

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
		// TODO: 세션에서 사용자 정보 조회 후 모델에 추가
		return "user/mypage"; // -> /WEB-INF/views/user/mypage.html
	}
}
