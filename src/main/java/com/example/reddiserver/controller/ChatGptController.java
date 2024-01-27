package com.example.reddiserver.controller;

import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.dto.chatgpt.request.ChatGptRequest;
import com.example.reddiserver.service.ChatGptService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/chat-gpt")
public class ChatGptController {
    private final ChatGptService chatGptService;

    @Operation(summary = "ChatGPT로 원하는 브랜드 만들기")
    @PostMapping("/question")
    public ApiResponse<String> postChat(@RequestBody ChatGptRequest chatGptRequest) {
        return ApiResponse.successResponse(chatGptService.postChat(chatGptRequest), "AI 브랜딩 성공");
    }
}
