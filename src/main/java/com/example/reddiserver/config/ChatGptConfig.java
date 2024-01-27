package com.example.reddiserver.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatGptConfig {
    public static final String CHAT_MODEL = "gpt-3.5-turbo";
    public static final String SYSTEM_ROLE = "system";
    public static final String USER_ROLE = "user";
    public static final String CHAT_URL = "https://api.openai.com/v1/chat/completions";
}
