package com.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderController {

	

	@GetMapping("/error")
	public String showMessagePage(Model model) {
		return "error"; // Return the error page
	}
}
