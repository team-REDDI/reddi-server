package com.example.reddiserver.dto.member;

import com.example.reddiserver.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInfoResponseDto {
	private Long userId;
	private String name;
	private String email;
	private String profileImageUrl;

	public static MemberInfoResponseDto from(Member member) {
		MemberInfoResponseDto userInfoResponseDto = new MemberInfoResponseDto();
		userInfoResponseDto.setUserId(member.getId());
		userInfoResponseDto.setName(member.getName());
		userInfoResponseDto.setEmail(member.getEmail());
		userInfoResponseDto.setProfileImageUrl(member.getProfileImageUrl());
		return userInfoResponseDto;
	}
}
