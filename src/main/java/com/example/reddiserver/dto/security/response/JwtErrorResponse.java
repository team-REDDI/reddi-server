package com.example.reddiserver.dto.security.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtErrorResponse {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private int status;
    private String message;

    @Builder
    public JwtErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
    public static JwtErrorResponse of(int status, String message) {
        return JwtErrorResponse.builder()
                .status(status)
                .message(message)
                .build();
    }

    public String convertToJson() throws JsonProcessingException {
        return objectMapper.writeValueAsString(this);
    }
}