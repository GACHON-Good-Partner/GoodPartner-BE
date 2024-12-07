package goodpartner.be.domain.chat.service;

import goodpartner.be.domain.chat.application.OpenAIRecommendationProvider;
import goodpartner.be.domain.chat.application.dto.response.OpenAIResponse;
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
    public List<Chat> getUserChatHistory(UUID userId){
        return chatRepository.findChatHistoryByUserId(userId);
    }

    //2.사용자 챗봇 질문 저장과 chatGPT 응답 생성
    public Chat saveChatAndGenerateResponse(UUID userId, String message) {
        // 사용자 질문 저장
        Chat userChat = Chat.builder()
                .userId(userId)
                .message(message)
                .status(Chat.Status.REQUEST)
                .build();
        chatRepository.save(userChat);

        // OpenAI 호출 및 응답 생성
        OpenAIResponse response = openAIRecommendationProvider.getRecommendationWithPrompt(message);
        String aiResponseMessage = response.choices().get(0).message().getContent();

        // AI 응답 저장
        Chat responseChat = Chat.builder()
                .userId(userId)
                .message(aiResponseMessage)
                .status(Chat.Status.RESPONSE)
                .build();
        chatRepository.save(responseChat);

        return userChat; // 사용자 질문 반환
    }

    //3.사용자 누적 질문수 조회
    public Long getTotalQuestions() {
        return chatRepository.count();
    }

    //4.최근 질문 3개 조회
    public List<Chat> getLatestChats() {
        return chatRepository.findTop3ByStatusOrderByCreatedAtDesc(Chat.Status.REQUEST);
    }
}
