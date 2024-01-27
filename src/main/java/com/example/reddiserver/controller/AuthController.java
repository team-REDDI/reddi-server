package com.example.reddiserver.controller;

import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.dto.auth.request.ReissueRequestDto;
import com.example.reddiserver.dto.auth.response.UserInfoResponseDto;
import com.example.reddiserver.dto.security.response.TokenDto;
import com.example.reddiserver.security.jwt.TokenProvider;
import com.example.reddiserver.security.oauth.GoogleOAuth;
import com.example.reddiserver.security.oauth.OAuthService;
import com.example.reddiserver.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final OAuthService oAuthService;
    private final TokenProvider tokenProvider;
    private final GoogleOAuth googleOAuth;


    @Operation(summary = "구글 로그인 링크")
    @GetMapping("/google-auth-url")
    public Map<String, String> getGoogleAuthUrl(@RequestParam(defaultValue = "http://localhost:3000") final String redirectUrl) throws Exception {

        Map<String, String> urlMap = new HashMap<>();
        urlMap.put("url", googleOAuth.getGOOGLE_LOGIN_URL() + "?redirect_uri=" + redirectUrl + "/&response_type=code&client_id=" + googleOAuth.getCLIENT_ID() + "&scope=profile email");
        return urlMap;
    }

    // code 와 redirectUrl 을 받아서 Google 로 부터 access token 을 발급 받고, 이를 이용해 회원가입 및 로그인을 진행
    @GetMapping("/google/login")
    public ResponseEntity<?> googleLogin(@RequestParam final String code, @RequestParam(defaultValue = "http://localhost:3000") final String redirectUrl) throws Exception {
        oAuthService.login(code, redirectUrl);

        return null;
    }

    // 임시엔드포인트
    // 쿼리스트링에 있는 code 값을 추출
    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam final String code) throws Exception {

        System.out.println("Received code: " + code);

        return ResponseEntity.ok("Received code: " + code);
    }


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

