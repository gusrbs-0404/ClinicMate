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
@RequestMapping("/main")
@RequiredArgsConstructor
public class MainController {
    
    private final HospitalService hospitalService;
    
    // 메인 화면 (지도 + 병원 정보)
    @GetMapping
    public String mainPage(Model model) {
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
