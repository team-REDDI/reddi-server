package com.example.reddiserver.controller;

import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.dto.auth.request.ReissueRequestDto;
import com.example.reddiserver.dto.security.response.TokenDto;
import com.example.reddiserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/reissue")
    public ApiResponse<TokenDto> reissue(@RequestBody ReissueRequestDto reissueRequestDto) {
        return ApiResponse.successResponse(authService.reissue(reissueRequestDto), "Access Token 재발급 성공");
    }
}
