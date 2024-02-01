package com.example.reddiserver.dto.chatgpt.response;

import com.example.reddiserver.entity.Prompt;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class ChatGptResultResponseDto {
    private Long id;
    private String name;
    private List<String> elements;

    public static ChatGptResultResponseDto from(Prompt prompt) {
        List<String> elements = new ArrayList<>();

        if (prompt.getName() != null) {
            elements.add("네이밍");
        }
        if (prompt.getSlogan() != null) {
            elements.add("슬로건");
        }
        if (prompt.getVision() != null) {
            elements.add("비전 미션");
        }
        if (prompt.getEssence() != null) {
            elements.add("브랜드 에센스");
        }
        if (prompt.getKeyword() != null) {
            elements.add("키워드");
        }
        if (prompt.getManifesto() != null) {
            elements.add("메니페스토");
        }

        return ChatGptResultResponseDto.builder()
                .id(prompt.getId())
                .name(prompt.getName())
                .elements(elements)
                .build();
    }
}
