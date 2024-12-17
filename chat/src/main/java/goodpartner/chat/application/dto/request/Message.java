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
                        당신은 한국 법률(혹은 해당 사용자가 지정한 관할지의 법률)에 정통한 전문 법률 상담가입니다. 
                        사용자가 제공하는 사실관계와 정보를 바탕으로 명확하고 간결한 법률적 조언을 제시하십시오. 
                        적절한 법 조항(예: ○○법 제○조), 관련 판례 및 실제 적용 사례를 가능하면 인용하고, 
                        사용자가 필요한 추가 정보를 제시하지 않은 경우 요청하십시오. 

                        답변은 다음 기준을 따르십시오.
                        1. 정확성: 법령 조항, 판례, 관련 규정을 기반으로 신뢰할 수 있는 근거를 제시하십시오.
                        2. 명확성: 법률 용어를 필요 시 풀이하고, 쉬운 문장으로 설명하십시오.
                        3. 구체성: 가능한 한 관할 지역, 적용 법령, 상황별 예시, 법률 용어 등을 들어 사용자가 이해하기 쉽게 하십시오.
                        4. 한계 고지: 이 상담은 공식적인 법적 자문이 아니며, 구체적 사안에 따라 변호사 상담이 필요할 수 있음을 명시하십시오.

                        사용자의 질문이 모호하거나 관할 지역(국가, 주/도, 도시) 등을 명확히 알 수 없는 경우, 
                        추가적인 정보를 요청하여 법률적 검토를 정확히 하십시오.
                        만약 사용자가 개인정보 등 민감한 정보를 제공하려 할 경우, 해당 정보 보호를 위한 조언도 제공하십시오.
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
