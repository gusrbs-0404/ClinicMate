package com.example.ClinicMate.controller;

import com.example.ClinicMate.entity.*;
import com.example.ClinicMate.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "사용자가 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "사용자 삭제에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/user/{id}/role")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        try {
            // TODO: userService.updateUserRole(id, role) 메서드 구현 필요
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "사용자 권한이 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "사용자 권한 업데이트에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 병원 관리
    @GetMapping("/hospitals")
    @ResponseBody
    public ResponseEntity<List<Hospital>> getHospitals(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            List<Hospital> hospitals = hospitalService.getAllHospitals();
            return ResponseEntity.ok(hospitals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/hospital")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createHospital(@RequestBody Hospital hospital) {
        try {
            // TODO: HospitalService에 createHospital 메서드 추가 필요
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "병원이 등록되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "병원 등록에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/hospital/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateHospital(@PathVariable Long id, @RequestBody Hospital hospital) {
        try {
            // TODO: HospitalService에 updateHospital 메서드 추가 필요
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "병원 정보가 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "병원 정보 업데이트에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/hospital/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteHospital(@PathVariable Long id) {
        try {
            // TODO: HospitalService에 deleteHospital 메서드 추가 필요
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "병원이 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "병원 삭제에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/department")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createDepartment(@RequestBody Department department) {
        try {
            // TODO: DepartmentService에 createDepartment 메서드 추가 필요
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "진료과가 등록되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "진료과 등록에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/department/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        try {
            // TODO: DepartmentService에 updateDepartment 메서드 추가 필요
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "진료과 정보가 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "진료과 정보 업데이트에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/department/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteDepartment(@PathVariable Long id) {
        try {
            // TODO: DepartmentService에 deleteDepartment 메서드 추가 필요
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "진료과가 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "진료과 삭제에 실패했습니다.");
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

    @PostMapping("/doctor")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createDoctor(@RequestBody Doctor doctor) {
        try {
            // TODO: DoctorService에 createDoctor 메서드 추가 필요
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "의사가 등록되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "의사 등록에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/doctor/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        try {
            // TODO: DoctorService에 updateDoctor 메서드 추가 필요
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "의사 정보가 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "의사 정보 업데이트에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/doctor/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable Long id) {
        try {
            // TODO: DoctorService에 deleteDoctor 메서드 추가 필요
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "의사가 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "의사 삭제에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 예약 관리
    @GetMapping("/reservations")
    @ResponseBody
    public ResponseEntity<List<Reservation>> getReservations(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
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
    public ResponseEntity<List<Payment>> getPayments(HttpSession session) {
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        
        try {
            List<Payment> payments = paymentService.getAllPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
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
}
