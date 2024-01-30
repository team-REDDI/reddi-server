package com.example.reddiserver.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class HomeController {

	// 임시 리다이렉션 엔드포인트
	@GetMapping("/auth/google/callback")
	public String home(@RequestParam("code") String code) {
		return code;
	}

}
