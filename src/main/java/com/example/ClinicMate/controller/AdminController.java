package com.example.ClinicMate.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ClinicMate.entity.Department;
import com.example.ClinicMate.entity.Doctor;
import com.example.ClinicMate.entity.Hospital;
import com.example.ClinicMate.entity.Notification;
import com.example.ClinicMate.entity.Payment;
import com.example.ClinicMate.entity.Reservation;
import com.example.ClinicMate.entity.User;
import com.example.ClinicMate.service.DepartmentService;
import com.example.ClinicMate.service.DoctorService;
import com.example.ClinicMate.service.HospitalService;
import com.example.ClinicMate.service.NotificationService;
import com.example.ClinicMate.service.PaymentService;
import com.example.ClinicMate.service.ReservationService;
import com.example.ClinicMate.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private HospitalService hospitalService;
    
    @Autowired
    private DepartmentService departmentService;
    
    @Autowired
    private DoctorService doctorService;
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private NotificationService notificationService;
    
    // 관리자 권한 체크 메서드
    private boolean isAdmin(HttpSession session) {
        String userRole = (String) session.getAttribute("role");
        return userRole != null && userRole.equals("ADMIN");
    }
    
    // 권한 체크 실패 시 응답
    private ResponseEntity<Map<String, String>> unauthorizedResponse() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "관리자 권한이 필요합니다.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // 관리자 메인 페이지
    @GetMapping
    public String adminMain(Model model, HttpSession session) {
        // 관리자 권한 체크
        String userRole = (String) session.getAttribute("role");
        if (userRole == null || !userRole.equals("ADMIN")) {
            return "redirect:/users/signin";
        }
        
        try {
            // 실제 통계 데이터 가져오기
            long totalUsers = userService.getTotalUsers();
            long totalHospitals = hospitalService.getTotalHospitals();
            long totalReservations = reservationService.getTotalReservations();
            long totalPayments = paymentService.getTotalPayments();
            
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("totalHospitals", totalHospitals);
            model.addAttribute("totalReservations", totalReservations);
            model.addAttribute("totalPayments", totalPayments);
        } catch (Exception e) {
            // 에러 발생 시 기본값 설정
            model.addAttribute("totalUsers", 0);
            model.addAttribute("totalHospitals", 0);
            model.addAttribute("totalReservations", 0);
            model.addAttribute("totalPayments", 0);
        }
        
        return "admin/admin";
    }

    // 회원 관리
    @GetMapping("/users")
    @ResponseBody
    public ResponseEntity<?> getUsers(HttpSession session) {
        if (!isAdmin(session)) {
            return unauthorizedResponse();
        }
        
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/user/{id}")
    @ResponseBody
    public ResponseEntity<User> getUser(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            var user = userService.findById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/user/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            userService.updateUser(id, user);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "사용자 정보가 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "사용자 정보 업데이트에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/user/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return unauthorizedResponse();
        }
        
        try {
            // 사용자 정보 조회하여 탈퇴 요청 상태 확인
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
            
            if (user.getWithdrawalStatus() != User.WithdrawalStatus.WITHDRAWAL_REQUESTED) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "탈퇴 요청이 없는 사용자는 삭제할 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "사용자가 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "사용자 삭제에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/user")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody User user, HttpSession session) {
        if (!isAdmin(session)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        try {
            User createdUser = userService.signup(user);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "사용자가 등록되었습니다.");
            response.put("user", createdUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "사용자 등록에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/user/{id}/role")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateUserRole(@PathVariable Long id, @RequestParam String role, HttpSession session) {
        if (!isAdmin(session)) {
            return unauthorizedResponse();
        }
        
        try {
            User.UserRole userRole = User.UserRole.valueOf(role);
            userService.updateUserRole(id, userRole);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "사용자 권한이 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "사용자 권한 업데이트에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 탈퇴 요청된 사용자 목록 조회
    @GetMapping("/users/withdrawal-requests")
    @ResponseBody
    public ResponseEntity<List<User>> getUsersWithWithdrawalRequest(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            List<User> users = userService.getUsersWithWithdrawalRequest();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    // 탈퇴 승인
    @PostMapping("/user/{id}/approve-withdrawal")
    @ResponseBody
    public ResponseEntity<Map<String, String>> approveWithdrawal(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return unauthorizedResponse();
        }
        
        try {
            userService.approveWithdrawal(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "탈퇴가 승인되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "탈퇴 승인에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 병원 관리
    @GetMapping("/hospitals")
    @ResponseBody
    public ResponseEntity<List<Hospital>> getHospitals(HttpSession session) {
        // 임시로 권한 체크 비활성화 (테스트용)
        // if (!isAdmin(session)) {
        //     return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        // }
        
        try {
            List<Hospital> hospitals = hospitalService.getAllHospitals();
            System.out.println("병원 목록 조회: " + hospitals.size() + "개");
            return ResponseEntity.ok(hospitals);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/hospital/{id}")
    @ResponseBody
    public ResponseEntity<Hospital> getHospital(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            var hospital = hospitalService.getHospitalById(id);
            if (hospital.isPresent()) {
                return ResponseEntity.ok(hospital.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/hospital")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createHospital(@RequestBody Hospital hospital, HttpSession session) {
        if (!isAdmin(session)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        try {
            Hospital createdHospital = hospitalService.createHospital(hospital);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "병원이 등록되었습니다.");
            response.put("hospital", createdHospital);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "병원 등록에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/hospital/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateHospital(@PathVariable Long id, @RequestBody Hospital hospital, HttpSession session) {
        if (!isAdmin(session)) {
            return unauthorizedResponse();
        }
        
        try {
            hospitalService.updateHospital(id, hospital);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "병원 정보가 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "병원 정보 업데이트에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/hospital/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteHospital(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return unauthorizedResponse();
        }
        
        try {
            hospitalService.deleteHospital(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "병원이 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "병원 삭제에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/department/{id}")
    @ResponseBody
    public ResponseEntity<Department> getDepartment(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            var department = departmentService.getDepartmentById(id);
            if (department.isPresent()) {
                return ResponseEntity.ok(department.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/department")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createDepartment(@RequestBody Map<String, Object> request, HttpSession session) {
        if (!isAdmin(session)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        try {
            Long hospitalId = Long.valueOf(request.get("hospitalId").toString());
            String deptName = request.get("deptName").toString();
            
            Department createdDepartment = departmentService.createDepartment(hospitalId, deptName);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "진료과가 등록되었습니다.");
            response.put("department", createdDepartment);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "진료과 등록에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/department/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateDepartment(@PathVariable Long id, @RequestBody Map<String, String> request, HttpSession session) {
        if (!isAdmin(session)) {
            return unauthorizedResponse();
        }
        
        try {
            String deptName = request.get("deptName");
            departmentService.updateDepartment(id, deptName);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "진료과 정보가 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "진료과 정보 업데이트에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/department/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteDepartment(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return unauthorizedResponse();
        }
        
        try {
            departmentService.deleteDepartment(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "진료과가 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "진료과 삭제에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 진료과 관리
    @GetMapping("/departments")
    @ResponseBody
    public ResponseEntity<List<Department>> getDepartments(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            List<Department> departments = departmentService.getAllDepartments();
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/departments/hospital/{hospitalId}")
    @ResponseBody
    public ResponseEntity<List<Department>> getDepartmentsByHospital(@PathVariable Long hospitalId, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            List<Department> departments = departmentService.getDepartmentsByHospital(hospitalId);
            return ResponseEntity.ok(departments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 의사 관리
    @GetMapping("/doctors")
    @ResponseBody
    public ResponseEntity<List<Doctor>> getDoctors(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            return ResponseEntity.ok(doctors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/doctor/{id}")
    @ResponseBody
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            var doctor = doctorService.getDoctorById(id);
            if (doctor.isPresent()) {
                return ResponseEntity.ok(doctor.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/doctor")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createDoctor(@RequestBody Map<String, Object> request, HttpSession session) {
        if (!isAdmin(session)) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "관리자 권한이 필요합니다.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        try {
            Long hospitalId = Long.valueOf(request.get("hospitalId").toString());
            Long deptId = Long.valueOf(request.get("deptId").toString());
            String name = request.get("name").toString();
            String availableTime = request.get("availableTime").toString();
            
            Doctor createdDoctor = doctorService.createDoctor(hospitalId, deptId, name, availableTime);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "의사가 등록되었습니다.");
            response.put("doctor", createdDoctor);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "의사 등록에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/doctor/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateDoctor(@PathVariable Long id, @RequestBody Map<String, Object> request, HttpSession session) {
        if (!isAdmin(session)) {
            return unauthorizedResponse();
        }
        
        try {
            Long hospitalId = Long.valueOf(request.get("hospitalId").toString());
            Long deptId = Long.valueOf(request.get("deptId").toString());
            String name = request.get("name").toString();
            String availableTime = request.get("availableTime").toString();
            
            doctorService.updateDoctor(id, hospitalId, deptId, name, availableTime);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "의사 정보가 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "의사 정보 업데이트에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/doctor/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return unauthorizedResponse();
        }
        
        try {
            doctorService.deleteDoctor(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "의사가 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "의사 삭제에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 예약 관리
    @GetMapping("/reservations")
    @ResponseBody
    public ResponseEntity<List<Reservation>> getReservations(
            @RequestParam(required = false) Long hospitalId,
            HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            List<Reservation> reservations;
            if (hospitalId != null) {
                reservations = reservationService.getReservationsByHospital(hospitalId);
            } else {
                reservations = reservationService.getAllReservations();
            }
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            e.printStackTrace(); // 오류 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/reservation/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateReservationStatus(@PathVariable Long id, @RequestParam String status, HttpSession session) {
        if (!isAdmin(session)) {
            return unauthorizedResponse();
        }
        
        try {
            Reservation.ReservationStatus reservationStatus = Reservation.ReservationStatus.valueOf(status);
            reservationService.updateReservationStatus(id, reservationStatus);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "예약 상태가 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "예약 상태 업데이트에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 결제 관리
    @GetMapping("/payments")
    @ResponseBody
    public ResponseEntity<List<Payment>> getPayments(
            @RequestParam(required = false) Long hospitalId,
            HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            List<Payment> payments;
            if (hospitalId != null) {
                payments = paymentService.getPaymentsByHospital(hospitalId);
            } else {
                payments = paymentService.getAllPayments();
            }
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            e.printStackTrace(); // 오류 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/payment/{id}/status")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updatePaymentStatus(@PathVariable Long id, @RequestParam String status, HttpSession session) {
        if (!isAdmin(session)) {
            return unauthorizedResponse();
        }
        
        try {
            Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status);
            paymentService.updatePaymentStatus(id, paymentStatus);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "결제 상태가 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "결제 상태 업데이트에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 알림 관리
    @GetMapping("/notifications")
    @ResponseBody
    public ResponseEntity<List<Notification>> getNotifications() {
        try {
            List<Notification> notifications = notificationService.getAllNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/notification/resend/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> resendNotification(@PathVariable Long id) {
        try {
            notificationService.resendNotification(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "알림이 재발송되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "알림 재발송에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 통계 API
    @GetMapping("/stats/monthly")
    @ResponseBody
    public ResponseEntity<Object> getMonthlyStats(@RequestParam(required = false) Long hospitalId) {
        try {
            // TODO: reservationService.getMonthlyStats(hospitalId) 메서드 구현 필요
            Map<String, Object> mockData = new HashMap<>();
            mockData.put("labels", new String[]{"1월", "2월", "3월", "4월", "5월", "6월"});
            mockData.put("values", new int[]{12, 19, 3, 5, 2, 3});
            return ResponseEntity.ok(mockData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/stats/department")
    @ResponseBody
    public ResponseEntity<Object> getDepartmentStats(@RequestParam(required = false) Long hospitalId) {
        try {
            // TODO: reservationService.getDepartmentStats(hospitalId) 메서드 구현 필요
            Map<String, Object> mockData = new HashMap<>();
            mockData.put("labels", new String[]{"정형외과", "내과", "소아과", "신경외과", "가정의학과"});
            mockData.put("values", new int[]{12, 19, 3, 5, 2});
            return ResponseEntity.ok(mockData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/stats/payment")
    @ResponseBody
    public ResponseEntity<Object> getPaymentStats(@RequestParam(required = false) Long hospitalId) {
        try {
            // TODO: paymentService.getPaymentStats(hospitalId) 메서드 구현 필요
            Map<String, Object> mockData = new HashMap<>();
            mockData.put("labels", new String[]{"완료", "대기", "취소"});
            mockData.put("values", new int[]{300, 50, 100});
            return ResponseEntity.ok(mockData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 스케줄 관리
    @GetMapping("/schedule/doctor/{doctorId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getDoctorSchedule(
            @PathVariable Long doctorId,
            @RequestParam String date,
            HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            // 의사 정보 조회
            var doctor = doctorService.getDoctorById(doctorId);
            if (!doctor.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "의사를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // 해당 날짜의 예약 목록 조회
            List<Reservation> reservations = reservationService.getReservationsByDoctorAndDate(doctorId, date);
            
            // 의사 정보와 예약 목록 반환
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("doctor", doctor.get());
            response.put("reservations", reservations);
            response.put("date", date);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "스케줄 조회 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/schedule/reservation")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createAdminReservation(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            Long doctorId = Long.valueOf(request.get("doctorId").toString());
            Long userId = Long.valueOf(request.get("userId").toString());
            String date = request.get("date").toString();
            String time = request.get("time").toString();
            
            // 의사 정보로부터 병원, 진료과 정보 가져오기
            var doctor = doctorService.getDoctorById(doctorId);
            if (!doctor.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "의사를 찾을 수 없습니다.");
                return ResponseEntity.badRequest().body(response);
            }
            
            Doctor doctorEntity = doctor.get();
            Long hospitalId = doctorEntity.getHospital().getHospitalId();
            Long deptId = doctorEntity.getDepartment().getDeptId();
            
            // 날짜와 시간을 합쳐서 LocalDateTime 생성
            String dateTimeStr = date + " " + time + ":00";
            LocalDateTime resDateTime = LocalDateTime.parse(dateTimeStr, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // 예약 생성
            Reservation reservation = reservationService.createReservation(
                userId, hospitalId, doctorId, deptId, resDateTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "예약이 성공적으로 생성되었습니다.");
            response.put("reservation", reservation);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "예약 생성 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
