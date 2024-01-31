package com.example.reddiserver.controller;

import com.example.reddiserver.auth.filter.CustomAuthenticationDetails;
import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.dto.auth.request.GoogleLoginRequestDto;
import com.example.reddiserver.dto.auth.response.LoginResponseDto;
import com.example.reddiserver.dto.member.MemberInfoResponseDto;
import com.example.reddiserver.auth.oauth.GoogleOAuth;
import com.example.reddiserver.auth.service.OAuthService;
import com.example.reddiserver.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final OAuthService oAuthService;
    private final GoogleOAuth googleOAuth;
    private final MemberService memberService;


    @Operation(summary = "구글 로그인 링크")
    @Parameter(name = "redirectUri", description = "http://localhost:3000/auth/google/callback , https://reddi.kr/auth/google/callback 중 하나", required = true)
    @GetMapping("/google-auth-url")
    public Map<String, String> getGoogleAuthUrl(@RequestParam String redirectUri) throws Exception {

        Map<String, String> urlMap = new HashMap<>();
//        urlMap.put("url", googleOAuth.getGOOGLE_LOGIN_URL() + "?redirect_uri=" + googleOAuth.getREDIRECT_URI() + "&response_type=code&client_id=" + googleOAuth.getCLIENT_ID() + "&scope=profile email");
        urlMap.put("url", googleOAuth.getGOOGLE_LOGIN_URL() + "?redirect_uri=" + redirectUri + "&response_type=code&client_id=" + googleOAuth.getCLIENT_ID() + "&scope=profile email");
        return urlMap;
    }

    // code 를 받아서 Google 로 부터 access token 을 발급 받고, 이를 이용해 회원가입 및 로그인을 진행
    @PostMapping("/google/login")
    public ApiResponse<LoginResponseDto> googleLogin(@RequestBody GoogleLoginRequestDto googleLoginRequestDto) throws Exception {
        String code = googleLoginRequestDto.getCode();
        String redirectUri = googleLoginRequestDto.getRedirectUri();
        LoginResponseDto loginResponse = oAuthService.login(code, redirectUri);

        return ApiResponse.successResponse(loginResponse);
    }




//    @SecurityRequirement(name = "Authorization") // 인증 필요한 엔드포인트에 설정
//    @PostMapping("/reissue")
//    public ApiResponse<TokenDto> reissue(@RequestBody ReissueRequestDto reissueRequestDto) {
//        return ApiResponse.successResponse(authService.reissue(reissueRequestDto), "Access Token 재발급 성공");
//    }

    @SecurityRequirement(name = "Authorization") // 인증 필요한 엔드포인트에 설정
    @GetMapping("/info")
    public ApiResponse<?> getUserInfo(HttpServletRequest request) {
        // Extract Access Token from the request
        Long userId = oAuthService.getUserId();

        MemberInfoResponseDto memberInfo = memberService.getMemberInfo(userId);

        if (memberInfo != null) {
            return ApiResponse.successResponse(memberInfo);
        }
        return ApiResponse.errorResponse("유효하지 않은 Access Token 입니다.");
	}
}

