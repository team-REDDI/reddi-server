package com.example.reddiserver.dto.auth.request;

import lombok.Getter;

@Getter
public class ReissueRequestDto {
    private String accessToken;
    private String refreshToken;
}
