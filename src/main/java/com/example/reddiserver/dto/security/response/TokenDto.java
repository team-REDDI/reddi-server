package com.example.reddiserver.dto.security.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {
    private String accessToken;
    private String refreshToken;
}
