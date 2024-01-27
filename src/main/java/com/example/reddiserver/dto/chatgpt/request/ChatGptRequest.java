package com.example.reddiserver.dto.chatgpt.request;

import lombok.Getter;

@Getter
public class ChatGptRequest {
    private String elements; // 원하는 브랜드 요소
    private String atmospheres; // 원하는 브랜드 분위기
    private String industries; // 종사하는 산업군
    private String targets; // 어떤 고객 타켓
    private String similarServices; // 유사 서비스
}
