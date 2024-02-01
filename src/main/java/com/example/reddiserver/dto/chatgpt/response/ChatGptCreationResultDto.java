package com.example.reddiserver.dto.chatgpt.response;

import com.example.reddiserver.entity.Prompt;
import lombok.Builder;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Builder
@Getter
public class ChatGptCreationResultDto {
    private Map<String, String> result;

    public static ChatGptCreationResultDto from(Prompt prompt) {
        Map<String, String> result = new LinkedHashMap<>();

        if (prompt.getName() != null) {
            result.put("네이밍", prompt.getName());
        }
        if (prompt.getReason() != null) {
            result.put("네이밍 이유", prompt.getReason());
        }
        if (prompt.getSlogan() != null) {
            result.put("슬로건", prompt.getSlogan());
        }
        if (prompt.getVision() != null) {
            result.put("비전 미션", prompt.getVision());
        }
        if (prompt.getEssence() != null) {
            result.put("브랜드 에센스", prompt.getEssence());
        }
        if (prompt.getKeyword() != null) {
            result.put("키워드", prompt.getKeyword());
        }
        if (prompt.getManifesto() != null) {
            result.put("메니페스", prompt.getManifesto());
        }

        return ChatGptCreationResultDto.builder()
                .result(result)
                .build();
    }
}
