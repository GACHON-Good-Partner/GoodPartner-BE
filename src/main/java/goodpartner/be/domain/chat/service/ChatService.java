package goodpartner.be.domain.chat.service;

import goodpartner.be.domain.chat.application.OpenAIRecommendationProvider;
import goodpartner.be.domain.chat.application.dto.response.ChatResponse;
import goodpartner.be.domain.chat.entity.Chat;
import goodpartner.be.domain.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatRepository chatRepository;
    private final OpenAIRecommendationProvider openAIRecommendationProvider;

    //1.사용자 채팅 내역 조회
    public List<ChatResponse> getUserChatHistory(String userId){
        List<Chat> chat = chatRepository.findChatHistoryByUserId(UUID.fromString(userId));

        return chat.stream()
                .map(ChatResponse::from)
                .toList();
    }

    //2.사용자 챗봇 질문 저장과 chatGPT 응답 생성
    @Transactional
    public ChatResponse saveChatAndGenerateResponse(String userId, String message) {
        // 사용자 질문 저장
        Chat userChat = Chat.builder()
                .userId(UUID.fromString(userId))
                .message(message)
                .status(Chat.Status.REQUEST)
                .build();
        chatRepository.save(userChat);

        Chat response = Chat.builder()
                .userId(UUID.fromString(userId))
                .message(message)
                .status(Chat.Status.RESPONSE)
                .build();
        chatRepository.save(response);

        return ChatResponse.from(response);
        /*
        todo API 테스트 후 원복하기
         */
        // OpenAI 호출 및 응답 생성
       OpenAIResponse response = openAIRecommendationProvider.getRecommendationWithPrompt(message);
       String aiResponseMessage = response.choices().get(0).message().getContent();

       // AI 응답 저장
       Chat responseChat = Chat.builder()
               .userId(UUID.fromString(userId))
               .message(aiResponseMessage)
               .status(Chat.Status.RESPONSE)
               .build();
       chatRepository.save(responseChat);
    }

    //3.사용자 누적 질문수 조회
    public Long getTotalQuestions() {
        return chatRepository.countByStatus(Chat.Status.REQUEST);
    }

    //4.최근 질문 3개 조회
    public List<ChatResponse> getLatestChats(String userId) {
        List<Chat> chat = chatRepository.findTop3ByStatusAndUserIdOrderByCreatedAtDesc(Chat.Status.REQUEST, UUID.fromString(userId));
        return chat.stream()
                .map(ChatResponse::from)
                .toList();
    }
}
