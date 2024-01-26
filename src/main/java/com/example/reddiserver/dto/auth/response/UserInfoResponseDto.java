package com.example.reddiserver.dto.auth.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponseDto {
	private Long userId;
	private String name;
	private String email;
	private String profileImageUrl;

	public static UserInfoResponseDto from(Long userId, String name, String email, String profileImageUrl) {
		UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto();
		userInfoResponseDto.setUserId(userId);
		userInfoResponseDto.setName(name);
		userInfoResponseDto.setEmail(email);
		userInfoResponseDto.setProfileImageUrl(profileImageUrl);
		return userInfoResponseDto;
	}
}
