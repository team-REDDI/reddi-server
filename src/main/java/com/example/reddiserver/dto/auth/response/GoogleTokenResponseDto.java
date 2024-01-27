package com.example.reddiserver.dto.auth.response;

public record GoogleTokenResponseDto(String access_token, Integer expires_in, String scope, String token_type, String id_token) {
}
