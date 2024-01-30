package com.example.reddiserver.service;

import com.example.reddiserver.dto.member.MemberInfoResponseDto;
import com.example.reddiserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
	private final MemberRepository memberRepository;

	public MemberInfoResponseDto getMemberInfo(Long id) {
		return MemberInfoResponseDto.from(memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id=" + id)));
	}
}
