package goodpartner.be.domain.chat.application.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Message {
    private final String role; // SYSTEM, USER
    private final String content; // 메세지의 내용

    // 메시지 리스트 생성
    public static List<Message> createMessages(Message userMessage) {
        List<Message> messages = new ArrayList<>();
        messages.add(createSystemMessage());
        messages.add(userMessage); // 전달된 userMessage를 추가
        return messages;
    }

    // 시스템 메시지 생성: 법률 상담 컨텍스트 제공
    public static Message createSystemMessage() {
        return Message.builder()
                .role(Role.SYSTEM.getDescription())
                .content("""
                    당신은 법률 상담 전문가입니다. 사용자가 제공하는 정보를 바탕으로 정확하고 간결한 법률 조언을 제공해야 합니다.
                    추가적인 정보가 필요하면 요청하십시오. 적절한 법률 조항과 예시를 포함하여 답변을 작성하세요.
                    """) // 지시사항
                .build();
    }

    // 사용자 메시지 생성: 사용자의 법률 상담 질문 예시
    public static Message createUserMessage(String content) {
        return Message.builder()
                .role(Role.USER.getDescription())
                .content(content)
                .build();
    }

    @Getter
    @RequiredArgsConstructor
    public enum Role {
        SYSTEM("system"), // 시스템 메시지
        USER("user");     // 사용자 메시지

        private final String description;
    }

}
