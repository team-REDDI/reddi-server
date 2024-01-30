package com.example.reddiserver.auth.filter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

@Getter
public class CustomAuthenticationDetails extends WebAuthenticationDetails {

	private final Long userId;

	public CustomAuthenticationDetails(Long userId, HttpServletRequest request) {
		super(request);
		this.userId = userId;
	}



}