package com.example.reddiserver.controller;

import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.dto.auth.request.ReissueRequestDto;
import com.example.reddiserver.dto.auth.response.UserInfoResponseDto;
import com.example.reddiserver.dto.security.response.TokenDto;
import com.example.reddiserver.entity.Member;
import com.example.reddiserver.repository.MemberRepository;
import com.example.reddiserver.security.jwt.TokenProvider;
import com.example.reddiserver.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final TokenProvider tokenProvider;

    @SecurityRequirement(name = "Authorization") // 인증 필요한 엔드포인트에 설정
    @PostMapping("/reissue")
    public ApiResponse<TokenDto> reissue(@RequestBody ReissueRequestDto reissueRequestDto) {
        return ApiResponse.successResponse(authService.reissue(reissueRequestDto), "Access Token 재발급 성공");
    }

    @SecurityRequirement(name = "Authorization") // 인증 필요한 엔드포인트에 설정
    @GetMapping("/info")
    public ApiResponse<?> getUserInfo(HttpServletRequest request) {
        // Extract Access Token from the request
        String accessToken = tokenProvider.resolveToken(request);

        Map<String, Object> userInfo = authService.getUserInfo(accessToken);

        if (userInfo != null) {
            UserInfoResponseDto userInfoResponseDto = UserInfoResponseDto.from(Long.valueOf(userInfo.get("userId").toString()),
                    userInfo.get("username").toString(),
                    userInfo.get("email").toString(),
                    userInfo.get("profileImageUrl").toString());

            return ApiResponse.successResponse(userInfoResponseDto);
        }
        return ApiResponse.errorResponse("유효하지 않은 Access Token 입니다.");
	}
}

