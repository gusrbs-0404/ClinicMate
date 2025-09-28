package com.example.ClinicMate.controller;

import com.example.ClinicMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DbHealthController {

	private final JdbcTemplate jdbcTemplate;
	private final UserRepository userRepository;

	@GetMapping("/db/health")
	public Map<String, Object> health() {
		Map<String, Object> result = new HashMap<>();
		
		try {
			// 기본 연결 테스트
			Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
			result.put("connection", (one != null && one == 1) ? "OK" : "FAIL");
			
			// User 테이블 접근 테스트
			long userCount = userRepository.count();
			result.put("userTable", "OK");
			result.put("userCount", userCount);
			
			result.put("status", "SUCCESS");
			result.put("message", "데이터베이스 연결 및 테이블 접근 성공");
			
		} catch (Exception e) {
			result.put("status", "FAIL");
			result.put("message", "데이터베이스 연결 실패: " + e.getMessage());
			result.put("connection", "FAIL");
		}
		
		return result;
	}
}
