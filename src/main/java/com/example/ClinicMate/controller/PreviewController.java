package com.example.ClinicMate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/preview")
public class PreviewController {

	@GetMapping("/signup")
	public String previewSignup() {
		return "user/signup";
	}

	@GetMapping("/signin")
	public String previewSignin() {
		return "user/signin";
	}

	@GetMapping("/mypage")
	public String previewMypage() {
		return "user/mypage";
	}

	@GetMapping("/edit-profile")
	public String previewEditProfile() {
		return "user/edit-profile";
	}

	@GetMapping("/withdraw")
	public String previewWithdraw() {
		return "user/withdraw";
	}
}
