package goodpartner.chat.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChatRequest(
        @NotNull String message
) {
}
