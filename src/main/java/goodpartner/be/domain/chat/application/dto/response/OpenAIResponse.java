package goodpartner.be.domain.chat.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import goodpartner.be.domain.chat.application.dto.request.Message;

import java.util.List;

public record OpenAIResponse(
        String id,
        String object,
        int created,
        String model,
        @JsonProperty("system_fingerprint")
        String systemFingerprint,
        List<Choice> choices,
        Usage usage
) {

    public record Choice(
            int index,
            Message message,
            Boolean logprobs,
            @JsonProperty("finish_reason")
            String finishReason
    ) {
    }

    public record Usage(
            @JsonProperty("prompt_tokens")
            int promptTokens,
            @JsonProperty("completion_tokens")
            int completionTokens,
            @JsonProperty("total_tokens")
            int totalTokens,
            @JsonProperty("completion_tokens_details")
            CompletionTokensDetails completionTokensDetails
    ) {
    }

    public record CompletionTokensDetails(
            @JsonProperty("reasoning_tokens")
            int reasoningTokens,
            @JsonProperty("accepted_prediction_tokens")
            int acceptedPredictionTokens,
            @JsonProperty("rejected_prediction_tokens")
            int rejectedPredictionTokens
    ) {
    }
}
