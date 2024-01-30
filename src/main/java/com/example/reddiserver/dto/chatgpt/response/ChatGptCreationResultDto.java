package com.example.reddiserver.dto.chatgpt.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class ChatGptCreationResultDto {
    private Map<String, String> result;
}
