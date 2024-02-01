package com.example.reddiserver.dto.chatgpt.response;

import com.example.reddiserver.entity.Prompt;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatGptPrompt {
    private String elements;
    private String atmospheres;
    private String industries;
    private String targets;
    private String similarServices;

    public static ChatGptPrompt from(Prompt prompt) {
        return ChatGptPrompt.builder()
                .elements(prompt.getElements())
                .atmospheres(prompt.getAtmospheres())
                .industries(prompt.getIndustries())
                .targets(prompt.getTargets())
                .similarServices(prompt.getSimilarServices())
                .build();
    }
}
