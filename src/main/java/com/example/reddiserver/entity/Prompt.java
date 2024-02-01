package com.example.reddiserver.entity;

import com.example.reddiserver.dto.chatgpt.request.ChatGptRequest;
import com.example.reddiserver.dto.chatgpt.response.ChatGptResponse;
import com.example.reddiserver.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "prompts")
public class Prompt extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column
    private String elements;

    @Column
    private String atmospheres;

    @Column
    private String industries;

    @Column
    private String targets;

    @Column
    private String similarServices;

    @Column
    private String name;

    @Column
    private String reason;

    @Column
    private String slogan;

    @Column
    private String vision;

    @Column
    private String essence;

    @Column
    private String keyword;

    @Column
    private String manifesto;

    @Builder
    public Prompt(String elements, String atmospheres, String industries, String targets, String similarServices, Member member, String name, String reason, String slogan, String vision, String essence, String keyword, String manifesto) {
        this.elements = elements;
        this.atmospheres = atmospheres;
        this.industries = industries;
        this.targets = targets;
        this.similarServices = similarServices;
        this.member = member;
        this.name = name;
        this.reason = reason;
        this.slogan = slogan;
        this.vision = vision;
        this.essence = essence;
        this.keyword = keyword;
        this.manifesto = manifesto;
    }

    public static Prompt of(Member member, String[] elements, ChatGptRequest chatGptRequest, ChatGptResponse chatGptResponse) {
        Prompt.PromptBuilder promptBuilder = Prompt.builder()
                .member(member)
                .elements(chatGptRequest.getElements())
                .atmospheres(chatGptRequest.getAtmospheres())
                .industries(chatGptRequest.getIndustries())
                .targets(chatGptRequest.getTargets())
                .similarServices(chatGptRequest.getSimilarServices());

        for (String element : elements) {
            switch (element) {
                case "네이밍":
                    promptBuilder.name(chatGptResponse.getName());
                    promptBuilder.reason(chatGptResponse.getReason());
                    break;
                case "슬로건":
                    promptBuilder.slogan(chatGptResponse.getSlogan());
                    break;
                case "비전 미션":
                    promptBuilder.vision(chatGptResponse.getVision());
                    break;
                case "브랜드 에센스":
                    promptBuilder.essence(chatGptResponse.getEssence());
                    break;
                case "키워드":
                    promptBuilder.keyword(chatGptResponse.getKeyword());
                    break;
                case "메니페스토":
                    promptBuilder.manifesto(chatGptResponse.getManifesto());
                    break;
                default:
                    log.info("정해진 브랜드 요소 이외에 다른 것이 들어왔음");
            }
        }

        return promptBuilder.build();
    }
}
