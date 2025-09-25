package com.example.ClinicMate.controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DbHealthController {

	private final JdbcTemplate jdbcTemplate;

	public DbHealthController(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@GetMapping("/db/health")
	public String health() {
		Integer one = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
		return (one != null && one == 1) ? "OK" : "FAIL";
	}
}
