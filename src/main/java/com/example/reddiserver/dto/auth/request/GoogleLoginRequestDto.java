package com.example.reddiserver.dto.auth.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleLoginRequestDto {
	private String code;
	private String redirectUri;
}
