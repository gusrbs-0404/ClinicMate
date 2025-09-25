package com.example.ClinicMate.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/users/action")
public class UsesActionController {

	// POST: 회원가입 액션
	@PostMapping("/signup")
	public String actionSignup(
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam(value = "hospitalName", required = false) String hospitalName,
			@RequestParam(value = "department", required = false) String department,
			@RequestParam("name") String name,
			@RequestParam("email") String email,
			@RequestParam("phone") String phone
	) {
		// TODO: 유효성 검사 및 회원 등록 처리
		return "OK";
	}

	// POST: 로그인 액션 (이메일 인증코드 입력은 추후 구현)
	@PostMapping("/signin")
	public String actionSignin(
			@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam(value = "code", required = false) String code,
			HttpSession session
	) {
		// TODO: 인증 처리 및 세션 저장
		return "OK";
	}

	// DELETE: 로그아웃
	@DeleteMapping("/signout")
	public String actionSignout(HttpSession session) {
		// TODO: 세션 무효화
		return "OK";
	}

	// PUT: 회원정보 수정
	@PutMapping("/edit")
	public String actionEdit(
			@RequestParam(value = "password", required = false) String password,
			@RequestParam(value = "hospitalName", required = false) String hospitalName,
			@RequestParam(value = "department", required = false) String department,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "phone", required = false) String phone,
			HttpSession session
	) {
		// TODO: 사용자 정보 수정 처리
		return "OK";
	}

	// POST: 회원 탈퇴 신청
	@PostMapping("/withdraw")
	public String actionWithdraw(HttpSession session) {
		// TODO: 탈퇴 신청 등록 (관리자 승인 대기)
		return "OK";
	}
}
