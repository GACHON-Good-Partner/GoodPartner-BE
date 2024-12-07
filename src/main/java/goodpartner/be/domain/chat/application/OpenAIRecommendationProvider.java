package goodpartner.be.domain.chat.application;

import goodpartner.be.domain.chat.application.dto.request.Message;
import goodpartner.be.domain.chat.application.dto.response.OpenAIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class OpenAIRecommendationProvider {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.secret-key}")
    private String secretKey;

    @Value("${openai.chat-completions-url}")
    private String chatCompletionsUrl;

    private final RestClient restClient = RestClient.create();

    // 동적으로 프롬프트를 처리하는 메서드
    public OpenAIResponse getRecommendationWithPrompt(String prompt) {
        Map<String, Object> requestBody = createRecommendationRequestBody(Message.createUserMessage(prompt));
        return sendRequest(requestBody);
    }

    // OpenAI API에 요청을 보내는 메서드
    private OpenAIResponse sendRequest(Map<String, Object> requestBody) {
        try {
            OpenAIResponse response = restClient
                    .post()
                    .uri(chatCompletionsUrl)
                    .headers(headers -> {
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + secretKey);
                    })
                    .body(requestBody)
                    .retrieve()
                    .body(OpenAIResponse.class);

            // 사용된 토큰 수를 로그로 출력
            log.info("사용된 토큰 수 - Prompt: {}, Completion: {}, Total: {}",
                    response.usage().promptTokens(),
                    response.usage().completionTokens(),
                    response.usage().totalTokens());

            return response;

        } catch (RestClientException e) {
            log.error("OpenAI API 요청 중 오류 발생 - 에러 메시지: {}", e.getMessage(), e);
            throw new RuntimeException("OpenAI API 요청 실패", e);
        }
    }

    private Map<String, Object> createRecommendationRequestBody(Message userMessage) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", Message.createMessages(userMessage));
        return requestBody;
    }
}