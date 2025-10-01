package com.example.ClinicMate.controller;

import com.example.ClinicMate.entity.Hospital;
import com.example.ClinicMate.entity.Department;
import com.example.ClinicMate.entity.Doctor;
import com.example.ClinicMate.service.HospitalService;
import com.example.ClinicMate.service.DepartmentService;
import com.example.ClinicMate.service.DoctorService;
import com.example.ClinicMate.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {
    
    private final HospitalService hospitalService;
    private final DepartmentService departmentService;
    private final DoctorService doctorService;
    private final ReservationService reservationService;
    
    // 예약 페이지 메인
    @GetMapping
    public String reservationPage(
            @RequestParam(required = false) Long hospitalId,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long doctorId,
            Model model) {
        
        // 병원 정보 로드
        if (hospitalId != null) {
            Optional<Hospital> hospital = hospitalService.getHospitalById(hospitalId);
            if (hospital.isPresent()) {
                model.addAttribute("selectedHospital", hospital.get());
                
                // 진료과 목록 로드
                List<Department> departments = departmentService.getDepartmentsByHospital(hospitalId);
                model.addAttribute("departments", departments);
                
                // 특정 진료과가 선택된 경우 의사 목록 로드
                if (deptId != null) {
                    List<Doctor> doctors = doctorService.getDoctorsByHospitalAndDepartment(hospitalId, deptId);
                    model.addAttribute("selectedDeptId", deptId);
                    model.addAttribute("doctors", doctors);
                    
                    // 특정 의사가 선택된 경우
                    if (doctorId != null) {
                        Optional<Doctor> doctor = doctorService.getDoctorById(doctorId);
                        if (doctor.isPresent()) {
                            model.addAttribute("selectedDoctor", doctor.get());
                        }
                    }
                }
            }
        }
        
        return "reservation/reservation";
    }
    
    // 특정 의사의 특정 날짜에 예약된 시간 조회 API
    @GetMapping("/api/booked-times")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBookedTimes(
            @RequestParam Long doctorId,
            @RequestParam String date) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 날짜 파싱
            LocalDateTime dateTime = LocalDateTime.parse(date + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // 예약된 시간 목록 조회
            List<String> bookedTimes = reservationService.getBookedTimesForDoctorAndDate(doctorId, dateTime);
            
            response.put("success", true);
            response.put("bookedTimes", bookedTimes);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "예약된 시간 조회 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
