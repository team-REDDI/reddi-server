package com.example.reddiserver.dto.chatgpt.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatGptResponse {
    private String name;
    private String reason;
    private String slogan;
    private String vision;
    private String essence;
    private String keyword;
    private String manifesto;

    @Builder
    public ChatGptResponse(String name, String reason, String slogan, String vision,
                           String essence, String keyword, String manifesto) {
        this.name = name;
        this.reason = reason;
        this.slogan = slogan;
        this.vision = vision;
        this.essence = essence;
        this.keyword = keyword;
        this.manifesto = manifesto;
    }
}
