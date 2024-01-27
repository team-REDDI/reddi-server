package com.example.reddiserver.dto.chatgpt.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ChatGptContent {
    private String model;
    private List<ChatGptMessage> messages;
}
