package com.example.reddiserver.dto.chatgpt.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatGptPromptResponseDto {
    private ChatGptPrompt prompt;
    private ChatGptCreationResultDto result;
}
