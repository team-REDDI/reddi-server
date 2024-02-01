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
            result.put("name", prompt.getName());
        }
        if (prompt.getReason() != null) {
            result.put("reason", prompt.getReason());
        }
        if (prompt.getSlogan() != null) {
            result.put("slogan", prompt.getSlogan());
        }
        if (prompt.getVision() != null) {
            result.put("vision", prompt.getVision());
        }
        if (prompt.getEssence() != null) {
            result.put("essence", prompt.getEssence());
        }
        if (prompt.getKeyword() != null) {
            result.put("keyword", prompt.getKeyword());
        }
        if (prompt.getManifesto() != null) {
            result.put("manifesto", prompt.getManifesto());
        }

        return ChatGptCreationResultDto.builder()
                .result(result)
                .build();
    }
}
