package goodpartner.chat.service;


import goodpartner.chat.application.OpenAIRecommendationProvider;
import goodpartner.chat.application.dto.response.ChatResponse;
import goodpartner.chat.application.dto.response.KeywordResponse;
import goodpartner.chat.application.dto.response.OpenAIResponse;
import goodpartner.chat.entity.Chat;
import goodpartner.chat.entity.Keyword;
import goodpartner.chat.entity.enums.Type;
import goodpartner.chat.repository.ChatRepository;
import goodpartner.chat.repository.KeywordRepository;
import goodpartner.chat.soap.SoapClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatRepository chatRepository;
    private final KeywordRepository keywordRepository;
    private final OpenAIRecommendationProvider openAIRecommendationProvider;
    private final SoapClient soapClient;

    //1.사용자 채팅 내역 조회
    public List<ChatResponse> getUserChatHistory(String userId) {
        List<Chat> chats = chatRepository.findChatHistoryByUserId(UUID.fromString(userId));

        return chats.stream()
                .map(chat -> {
                    List<Keyword> keywords = keywordRepository.findByChat(chat);

                    List<KeywordResponse> response = keywords.stream()
                            .map(KeywordResponse::of)
                            .toList();
                    return ChatResponse.from(chat, response);
                })
                .toList();
    }

    //2.사용자 챗봇 질문 저장과 chatGPT 응답 생성
    @Transactional
    public ChatResponse saveChatAndGenerateResponse(String userId, String message) throws Exception {
        // 사용자 질문 저장
        Chat userChat = Chat.builder()
                .userId(UUID.fromString(userId))
                .message(message)
                .status(Chat.Status.REQUEST)
                .build();
        chatRepository.save(userChat);

//        Chat response = Chat.builder()
//                .userId(UUID.fromString(userId))
//                .message(message)
//                .status(Chat.Status.RESPONSE)
//                .build();
//        chatRepository.save(response);
//
//        return ChatResponse.from(response);

        /*
        todo API 테스트 후 원복하기
         */
//         OpenAI 호출 및 응답 생성
        OpenAIResponse response = openAIRecommendationProvider.getRecommendationWithFineTunedModel(message, Type.REQUEST);
        String aiResponseMessage = response.choices().get(0).message().getContent();

        OpenAIResponse responseKeyword = openAIRecommendationProvider.getRecommendationWithFineTunedModel(message, Type.KEYWORD);
        String keywordResponseMessage = responseKeyword.choices().get(0).message().getContent();

        String keyword = null;
        if (keywordResponseMessage.contains("Keyword:")) {
            String[] parts = keywordResponseMessage.split("Keyword:");
            if (parts.length > 1) {
                String keywordPart = parts[1].split("Explanation:")[0].trim();
                keyword = keywordPart.split("\\s+")[0];
            }
        }

        log.info("키워드: {}", keyword);

        // AI 응답 저장
        Chat responseChat = Chat.builder()
                .userId(UUID.fromString(userId))
                .message(aiResponseMessage)
                .status(Chat.Status.RESPONSE)
                .build();
        chatRepository.save(responseChat);

        List<Keyword> keywords = searchKeywords(keyword, responseChat);
        keywordRepository.saveAll(keywords);

        List<KeywordResponse> keywordResponses = keywords.stream()
                .map(KeywordResponse::of)
                .toList();

        return ChatResponse.from(responseChat, keywordResponses);
    }

    //3.사용자 누적 질문수 조회
    public Long getTotalQuestions() {
        return chatRepository.countByStatus(Chat.Status.REQUEST);
    }

    //4.최근 질문 3개 조회
    public List<ChatResponse> getLatestChats(String userId) {
        List<Chat> chat = chatRepository.findTop3ByStatusAndUserIdOrderByCreatedAtDesc(Chat.Status.REQUEST, UUID.fromString(userId));
        return chat.stream()
                .map((Chat chat1) -> ChatResponse.from(chat1, null))
                .toList();
    }

    //5. 키워드 조회
    public List<Keyword> searchKeywords(String keyword, Chat chat) throws Exception {
        return soapClient.searchByKeyword(keyword, chat);
    }
}
