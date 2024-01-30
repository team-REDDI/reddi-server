package com.example.reddiserver.dto.auth.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GoogleUserResponseDto {
		private String sub;
		private String name;
		private String given_name;
		private String family_name;
		private String picture;
		private String email;
		private boolean email_verified;
		private String locale;
}
