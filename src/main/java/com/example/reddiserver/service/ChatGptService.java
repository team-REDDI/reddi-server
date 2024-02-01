package com.example.reddiserver.service;

import com.example.reddiserver.config.ChatGptConfig;
import com.example.reddiserver.dto.chatgpt.request.ChatGptContent;
import com.example.reddiserver.dto.chatgpt.request.ChatGptMessage;
import com.example.reddiserver.dto.chatgpt.request.ChatGptRequest;
import com.example.reddiserver.dto.chatgpt.response.*;
import com.example.reddiserver.entity.Member;
import com.example.reddiserver.entity.Prompt;
import com.example.reddiserver.repository.MemberRepository;
import com.example.reddiserver.repository.PromptRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ChatGptService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final PromptRepository promptRepository;
    private final MemberRepository memberRepository;

    public ChatGptService(WebClient.Builder webClientBuilder, @Value("${chatgpt.api-key}")String OPENAIAPIKEY,
                          ObjectMapper objectMapper, PromptRepository promptRepository, MemberRepository memberRepository) {
        this.webClient = webClientBuilder.
                baseUrl(ChatGptConfig.CHAT_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + OPENAIAPIKEY)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.objectMapper = objectMapper;
        this.promptRepository = promptRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public ChatGptCreationResultDto postChat(Long memberId, ChatGptRequest chatGptRequest) throws JsonProcessingException {
        String[] elements = chatGptRequest.getElements().split(", ");
        String atmospheres = chatGptRequest.getAtmospheres();
        String industries = chatGptRequest.getIndustries();
        String targets = chatGptRequest.getTargets();
        String similarServices = chatGptRequest.getSimilarServices();

        String data1 = """
                
                # 브랜드 스토리
                  왜 금융은 어려워야만 할까요? 필요하지만 어려운, 장벽이 있는 금융에 도전장을 던진 기업이 있습니다. 바로 토스입니다. 토스 등장 이전까지, 모바일 송금 절차는 어려웠습니다. 공인인증서가 필요했고 OTP나 보안카드 등 절차가 까다로웠죠. 하지만 2015년, 토스는 간편송금 서비스를 제공합니다. 이는 “편하고 쉬운 금융”의 시작이었습니다.      
                  22년 토스는 리브랜딩을 통해 간편 송금을 넘어 투자, 보험 심지어 대출까지 다양한 금융서비스의 제공을 발표합니다. 그리고 “새로운 차원의 금융”을 선보인다고 공표하죠. 이는 간편함과 편리함을 넘어, 금융 생활 전반의 혁신을 일으키기 위한 첫 시작입니다. 어렵고 모르는 금융에서 쉽고 편리한 일상에 녹아드는 금융을 만들기까지, 토스의 여정은 계속됩니다.             
                
                # 슬로건
                  금융을 쉽고 간편하게
                
                # 브랜드 철학
                ## 자유롭게
                  푸른 컬러는 모두의 자유로운 금융생활을 꿈꾸는 토스의 비전을 의미합니다.  
                ## 유연하게
                   로고에서 볼 수 있는 부드럽게 이어지는 곡선은 끊임없이 도전하는 토스의 태도를 보여줍니다.
                ## 대담하게
                   공간감을 가진 토스의 새로운 로고는 새로운 차원의 금융을 약속하고, 이를 위한 의지를 보입니다.
                """;

        String data2 = """
                
                # 브랜드 스토리   
                논픽션(NONFICTION)은 스튜디오 콘크리트를 역임한 차혜영 대표가 창업한 코스메틱 브랜드입니다. 논픽션이 출시한 제품은 향수, 바디용품, 핸드워시, 핸드크림 등의 라인업을 갖고 있습니다. 일반적인 뷰티 브랜드와 달리 논픽션은 한남동, 성수동 등 쇼룸에서 시작해 백화점 및 편집숍에 입점하는 색다른 행보를 보여주었습니다. 이는 논픽션의 프리미엄 컨셉의 브랜딩에 맞는 프리미엄 공간 추구성을 나타내며 그 공간에서의 제품 노출에 따른 브랜드 인식을 소비자에게 세워주기 위함으로 보입니다. 제품, 향, 입점 채널 모두 프리미엄 컨셉을 가진 논픽션은 최근 MZ 사이에서 작은 돈으로 사치를 누리는 '스몰 럭셔리'의 영향으로 높은 판매율을 기록했습니다.              
                
                # 슬로건
                ## Uncover your story
                논픽션은 향을 매개로 내면의 힘을 이야기하는 라이프스타일 뷰티 브랜드입니다.
                
                # Mission
                ## 고객에게 최상의 제품과 아름다운 라이프스타일 경험을 함께 선사하는 것
                
                # 브랜드 네이밍
                픽션(fiction)이 상상의 세계를 여는 문이라면, 논픽션(nonfiction)은 치장을 걷어낸 본질을 비추는 거울입니다. 우리는 자신과 만나는 일상 속 가장 내밀한 순간을 통해 그 순수한 이야기의 힘을 교감하고자 합니다. 모호한 현실과 부산한 잡음 사이에서 나만의 해답을 찾아내는 일. 나를 둘러싼 수많은 삶 속에서 목적과 의미를 되찾는 일. 논픽션은 변하지 않는 나만의 이야기를 찾아가는 순간과 함께합니다.
                """;

        String data3 = """
                
                # 브랜드 스토리           
                누데이크(NUDAKE)는 아이웨어 브랜드 젠틀몬스터에서 만든 디저트 브랜드입니다. 패션과 아트를 합쳐 특별한 디저트를 만든다는 신념 하에 유니크한 디자인과 독특한 맛으로 다른 디저트 브랜드와 차별화를 시도 했습니다. 누데이크의 시그니쳐 디저트인 피크(Peak) 케이크는 한동안 SNS 상에서 큰 인기를 끌기도 했습니다. 누데이크는 주로 젊은 세대를 타켓으로 하며 젠틀몬스터에서 만든 브랜드인만큼 감각적인 공간 구성을 선보이며 젊은 고객의 선택을 많이 받고 있습니다. 누데이트는 패션과 예술에 대한 이해가 높은 기획자와 전문 파티시에가 협업해 특별함을 더하고 디저트 외에도 공간 곳곳에 설치된 오브제와 조형물, 공간의 여백까지 공간 예술적 구현을 통해 누데이트만의 브랜드 매력을 크게 나타내었습니다.
                
                # 슬로건 
                ## Make New Fantasy  
                
                # 브랜드 네이밍  
                New, Different, Cake. 세 가지 단어를 조합해 기존 F&B 업계에서는 볼 수 없었던 새로움을 가지고 있으면서도 맛있는 디저트라는 뜻입니다.
                """;

        String system_content = """
                당신은 AI 브랜드를 만들어주는 서비스입니다
                당신의 역할은 아래와 같습니다.

                1. 사용자로부터 받는 입력 형식

                아래의 key, value 형식으로 사용자 입력이 들어와

                분위기 : ''
                산업군 : ''
                타겟 고객 : ''
                유사 서비스 : ''

                2. 각 Key 의 설명

                * 분위기 : 생성하고 싶은 브랜드의 분위기
                  예시 : 역동적인, 즐거운 등등
                * 산업군 : 생성하고 싶은 브랜드의 산업군
                  예시 : 금융, F&B
                * 타겟 고객 : 생성하고 싶은 브랜드의 타겟 고객
                  예시 : gen Z, 시니어, 20대 등등
                * 유사 서비스 : 생성하고 싶은 브랜드의 유사 서비스
                  예시 : 토스, 카카오뱅크 등등

                위 네 가지 정보에 적합한 새로운 브랜드 정보를 생성해줘

                3. 당신이 출력(생성)해야하는 브랜드 정보

                생성할 때 들어갈 key 값들은 총 7가지로, 아래와 같아
                '브랜드 네이밍(name)', '브랜드 네이밍 이유(reason)', '브랜드 슬로건(slogan)', '비전(vision)'
                '브랜드 에센스(essence)', '브랜드 키워드(keyword)', '매니페스토(manifesto)'

                브랜드 생성 예시를 보고 JSON('key': 'value') 형태의 결과값을 객체로 생성해줘
                그리고, 브랜드 생성 예시에 맞춰서 정확히 7가지의 key, value 값들을 생성해줘

                브랜드 생성 시 최대한 자세하게 내용 작성해줘

                <브랜드 생성 JSON 형식>
                {
                    "name": string,
                    "reason": string,
                    "slogan": string,
                    "vision": string,
                    "essence": string,
                    "keyword": string,
                    "manifesto": string
                }
                <브랜드 생성 JSON 형식 끝>

                참고로 아래는 '브랜드 키워드(keyword)' 로 들어갈 키워드들 예시야
                * 산업군 : F&B, 패션, 금융, 뷰티 등등
                * 브랜드 분위기 : 힙함, 키치함, 영함, 스트리트, 캐주얼, 심플함, 클래식, 아날로그, 귀여움, 세련됨 등등
                * 브랜드 색감 : 흰색, 푸른색, 초록색 등등

                4. 생성할 때 참고할 브랜드 정보 데이터
                아래는 기존에 존재하는 '브랜드 네이밍', '브랜드 스토리', '슬로건' 예시야
                아래 데이터를 참고하되 새롭게 변형해서 새로운 브랜드를 생성해줘야해

                <브랜드 데이터 목록>

                data1 : %s

                data2 : %s

                data3 : %s

                <브랜드 데이터 목록 끝>
                              
                5. 제약 조건

                5-1. 사용자로부터 입력받는 값 중 '유사 서비스' 와 당신이 출력하는 브랜드의 정보가 같거나 비슷하면 안 돼. 독창적인 브랜드를 생성해야해
                
                5-2. 출력 JSON 형식의 key 값들은 반드시 위에서 제시한 7가지 key 값들로 생성해야해
                
                5-3. 해당 key 값들은 반드시 쌍따옴표로 감싸져 있어야해
                """.formatted(data1, data2, data3);

        String user_content = """
                분위기 : %s
                산업군 : %s
                타겟 고객 : %s
                유사 서비스 : %s
                """.formatted(atmospheres, industries, targets, similarServices);

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

        ChatGptResponse chatGptResponse = objectMapper.readValue(webClient.post()
                .uri("")
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block()
                .get("choices")
                .get(0)
                .get("message")
                .get("content")
                .asText(), ChatGptResponse.class);

        Member member = null;

        if (memberId != null) {
            member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id=" + memberId));
        }

        Prompt prompt = promptRepository.save(Prompt.of(member, elements, chatGptRequest, chatGptResponse));

        return ChatGptCreationResultDto.from(prompt);
    }

    public List<ChatGptResultResponseDto> getChats(Long memberId) {
        List<Prompt> prompts = promptRepository.findByMemberId(memberId);

        List<ChatGptResultResponseDto> chatGptResultResponseDtos = prompts.stream()
                .map(prompt -> ChatGptResultResponseDto.from(prompt))
                .collect(Collectors.toList());

        return chatGptResultResponseDtos;
    }

    public ChatGptPromptResponseDto getPrompt(Long id) {
        Optional<Prompt> promptOptional = promptRepository.findById(id);

        if (promptOptional.isPresent()) {
            Prompt prompt = promptOptional.get();

            ChatGptPrompt chatGptPrompt = ChatGptPrompt.from(prompt);

            return ChatGptPromptResponseDto.of(prompt, chatGptPrompt);
        } else {
            throw new NotFoundException("해당 프롬프트를 찾을 수가 없습니다 : id = " + id);
        }
    }
}
