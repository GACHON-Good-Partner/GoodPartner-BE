package goodpartner.chat.application.dto.response;


import goodpartner.chat.entity.Chat;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatResponse(
        long chatId,
        UUID userId,
        Chat.Status status,
        String message,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ChatResponse from(Chat chat) {
        return new ChatResponse(
                chat.getId(),
                chat.getUserId(),
                chat.getStatus(),
                chat.getMessage(),
                chat.getCreatedAt(),
                chat.getUpdatedAt()
        );
    }
}
