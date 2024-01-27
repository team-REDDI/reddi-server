package com.example.reddiserver.service;

import com.example.reddiserver.config.ChatGptConfig;
import com.example.reddiserver.dto.chatgpt.request.ChatGptContent;
import com.example.reddiserver.dto.chatgpt.request.ChatGptMessage;
import com.example.reddiserver.dto.chatgpt.request.ChatGptRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ChatGptService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ChatGptService(WebClient.Builder webClientBuilder, @Value("${chatgpt.api-key}")String OPENAIAPIKEY,
                          ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.
                baseUrl(ChatGptConfig.CHAT_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + OPENAIAPIKEY)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.objectMapper = objectMapper;
    }

    public String postChat(ChatGptRequest chatGptRequest) {
        String elements = chatGptRequest.getElements();
        String atmospheres = chatGptRequest.getAtmospheres();
        String industries = chatGptRequest.getIndustries();
        String targets = chatGptRequest.getTargets();
        String similarServices = chatGptRequest.getSimilarServices();

        String data = """
                # 브랜드 스토리
                  왜 금융은 어려워야만 할까요? 필요하지만 어려운, 장벽이 있는 금융에 도전장을 던진 기업이 있습니다. 바로 토스입니다. 토스 등장 이전까지, 모바일 송금 절차는 어려웠습니다. 공인인증서가 필요했고 OTP나 보안카드 등 절차가 까다로웠죠. 하지만 2015년, 토스는 간편송금 서비스를 제공합니다. 이는 “편하고 쉬운 금융”의 시작이었습니다.      
                  22년 토스는 리브랜딩을 통해 간편 송금을 넘어 투자, 보험 심지어 대출까지 다양한 금융서비스의 제공을 발표합니다. 그리고 “새로운 차원의 금융”을 선보인다고 공표하죠. 이는 간편함과 편리함을 넘어, 금융 생활 전반의 혁신을 일으키기 위한 첫 시작입니다. 어렵고 모르는 금융에서 쉽고 편리한 일상에 녹아드는 금융을 만들기까지, 토스의 여정은 계속됩니다.
                                
                # 슬로건
                  금융을 쉽고 간편하게
                                
                # 브랜드 철학
                  자유롭게
                  푸른 컬러는 모두의 자유로운 금융생활을 꿈꾸는 토스의 비전을 의미합니다.
                                
                ## 유연하게
                   로고에서 볼 수 있는 부드럽게 이어지는 곡선은 끊임없이 도전하는 토스의 태도를 보여줍니다.
                                
                ## 대담하게
                   공간감을 가진 토스의 새로운 로고는 새로운 차원의 금융을 약속하고, 이를 위한 의지를 보입니다.
                """;

        String system_content = """
                당신은 AI 브랜드를 만들어주는 서비스입니다
                당신의 역할은 아래와 같습니다.
                                
                1. 기존에 존재하는 브랜드의 name, 브랜드의 tag, 브랜드의 content 를 제공받습니다
                <브랜드 데이터 목록>
                --------------------------------------------
                * data
                  %s
                --------------------------------------------          
                2. 사용자의 입력은 아래와 같아
                * 생성하고 싶은 브랜드 요소
                  예시 : 네이밍, 슬로건, 로고, 비전 미션, 브랜드 에센스, 키워드, 메니페스토 등등 
                * 생성하고 싶은 브랜드의 분위기
                  예시 : 역동적인, 즐거운 등등
                * 생성하고 싶은 브랜드의 산업군
                  예시 : 금융, F&B
                * 생성하고 싶은 브랜드의 타겟 고객
                  예시 : gen Z, 시니어, 20대 등등
                위 네 가지 카테고리에 대해서 사용자의 입력을 받아 브랜드 정보를 생성해줘
                                
                3. 브랜드 태그 종류와 각 속성들은 아래와 같아
                * 브랜드 분위기 : 힙함, 키치함, 영함, 스트리트, 캐주얼, 심플함, 클래식, 아날로그, 귀여움, 세련됨 등등
                * 브랜드 색감 : 흰색, 푸른색, 초록색 등등
                                
                4. 제약 조건
                브랜드 스토리는 최대한 상세하게 적어줘      
                <브랜드 생성 예시>
                브랜드 이름(네이밍) :
                브랜드 네이밍 이유 :
                브랜드 분위기 :
                브랜드 색감 :
                브랜드 스토리 :
                브랜드 슬로건 :
                """.formatted(data);

        String user_content = """
                생성하고 싶은 브랜드 요소 : %s
                분위기 : %s
                산업군 : %s
                타겟 고객 : %s
                유사 서비스 : %s
                """.formatted(elements, atmospheres, industries, targets, similarServices);

        List<ChatGptMessage> messages = new ArrayList<>();
        messages.add(ChatGptMessage.builder()
                        .role(ChatGptConfig.SYSTEM_ROLE)
                        .content(system_content)
                        .build());
        messages.add(ChatGptMessage.builder()
                        .role(ChatGptConfig.USER_ROLE)
                        .content(user_content)
                        .build());

        ChatGptContent body = ChatGptContent.builder()
                .model(ChatGptConfig.CHAT_MODEL)
                .messages(messages)
                .build();

        String response = webClient.post()
                .uri("")
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block()
                .get("choices")
                .get(0)
                .get("message")
                .get("content")
                .asText();

        return response;
    }
}
