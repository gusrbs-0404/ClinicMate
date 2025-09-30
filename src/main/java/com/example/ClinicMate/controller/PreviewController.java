package com.example.ClinicMate.controller;

import com.example.ClinicMate.entity.Hospital;
import com.example.ClinicMate.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/preview")
@RequiredArgsConstructor
public class PreviewController {
    
    private final HospitalService hospitalService;

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
	
	@GetMapping("/main")
	public String previewMain(Model model) {
		try {
			// 모든 병원 정보를 가져와서 지도에 표시
			List<Hospital> hospitals = hospitalService.getAllHospitals();
			model.addAttribute("hospitals", hospitals);
			
			return "main/main";
		} catch (Exception e) {
			model.addAttribute("error", "병원 정보를 불러오는데 실패했습니다: " + e.getMessage());
			return "main/main";
		}
	}
}
