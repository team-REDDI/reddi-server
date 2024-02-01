package com.example.reddiserver.controller;

import com.example.reddiserver.auth.service.OAuthService;
import com.example.reddiserver.common.ApiResponse;
import com.example.reddiserver.dto.chatgpt.request.ChatGptRequest;
import com.example.reddiserver.dto.chatgpt.response.ChatGptCreationResultDto;
import com.example.reddiserver.dto.chatgpt.response.ChatGptPromptResponseDto;
import com.example.reddiserver.dto.chatgpt.response.ChatGptResultResponseDto;
import com.example.reddiserver.service.ChatGptService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/chat-gpt")
public class ChatGptController {
    private final ChatGptService chatGptService;
    private final OAuthService oAuthService;

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "ChatGPT로 원하는 브랜드 만들기")
    @PostMapping("/question")
    public ApiResponse<ChatGptCreationResultDto> postChat(@RequestBody ChatGptRequest chatGptRequest) throws JsonProcessingException {
        Long memberId = oAuthService.getUserId();
        System.out.println(memberId);
        return ApiResponse.successResponse(chatGptService.postChat(memberId, chatGptRequest), "AI 브랜딩 성공");
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "REDDIAI로 생성한 브랜드 불러오기")
    @GetMapping("/")
    public ApiResponse<List<ChatGptResultResponseDto>> getChats() {
        Long memberId = oAuthService.getUserId();
        return ApiResponse.successResponse(chatGptService.getChats(memberId), "REDDI로 생성한 브랜드 불러오기");
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "프롬프트 불러오기")
    @GetMapping("/prompt")
    public ApiResponse<ChatGptPromptResponseDto> getPrompt(@RequestParam(required = true) Long id) {
        Long memberId = oAuthService.getUserId();
        return ApiResponse.successResponse(chatGptService.getPrompt(id), "프롬프트 불러오기");
    }
}
