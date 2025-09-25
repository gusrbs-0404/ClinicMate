package com.example.ClinicMate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "redirect:/preview/signup";
	}

	@GetMapping("/index")
	public String index() {
		return "redirect:/preview/signup";
	}
}
