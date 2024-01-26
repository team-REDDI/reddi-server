package com.example.reddiserver.service;

import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.dto.auth.request.ReissueRequestDto;
import com.example.reddiserver.dto.security.response.TokenDto;
import com.example.reddiserver.entity.Member;
import com.example.reddiserver.repository.MemberRepository;
import com.example.reddiserver.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    @Transactional
    public TokenDto reissue(ReissueRequestDto reissueRequestDto) {
        if (!tokenProvider.refreshTokenValidation(reissueRequestDto.getRefreshToken())) {
            throw new RuntimeException("유효하지 않은 Refresh Token 입니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(reissueRequestDto.getAccessToken());

        return tokenProvider.createAccessToken(authentication);
    }

    // user info 조회 서비스
    public Map<String, Object> getUserInfo(String accessToken) {

        // Validate the Access Token
        if (StringUtils.hasText(accessToken) && tokenProvider.validateAccessToken(accessToken)) {
            // Retrieve user information from the token
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String providerId = userDetails.getUsername();

            Member member = memberRepository.findByProviderId(providerId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", member.getId());
            userInfo.put("username", member.getName());
            userInfo.put("email", member.getEmail());
            userInfo.put("profileImageUrl", member.getProfileImageUrl());
            return userInfo;
        }
        return null;
    }

}
