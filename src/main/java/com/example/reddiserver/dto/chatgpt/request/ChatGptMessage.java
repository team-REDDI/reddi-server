package com.example.reddiserver.dto.chatgpt.request;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ChatGptMessage {
    private String role;
    private String content;
}
