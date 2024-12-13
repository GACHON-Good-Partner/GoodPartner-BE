package goodpartner.chat.application.dto.request;

import goodpartner.chat.entity.enums.Type;
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
    public static List<Message> createMessages(Message userMessage, Type type) {
        List<Message> messages = new ArrayList<>();
        if (type == Type.KEYWORD) {
            messages.add(createSystemKeywordMessage());
            messages.add(userMessage); // 전달된 userMessage를 추가
            return messages;
        }
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

    // 시스템 메시지 생성: 사용자 질문에서 주요 키워드 1개 추출
    public static Message createSystemKeywordMessage() {
        return Message.builder()
                .role(Role.SYSTEM.getDescription())
                .content("""
                        당신은 법률 상담 전문가입니다. 사용자가 제공하는 정보에서 가장 중요한 키워드를 1단어로 추출해야 합니다.
                        반드시 아래 형식을 따르세요:
                        Keyword: [Main keyword]
                        Explanation: [Additional explanation]
                        형식을 절대 변경하지 마세요. 다음과 같은 형식을 항상 유지하세요:
                        - Keyword: 뒤에는 반드시 단일 키워드를 작성하세요.
                        - Explanation: 뒤에는 키워드와 관련된 추가 설명을 작성하세요.
                        예를 들어:
                        질문: "이혼 시 양육권은 어떻게 결정되나요?"
                        응답:
                        Keyword: 양육권
                        Explanation: 자녀 양육권은 부모의 품성과 복리를 기준으로 법원이 결정합니다.
                        사용자의 질문에 대해 항상 이 형식을 유지하세요.
                        """)
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
