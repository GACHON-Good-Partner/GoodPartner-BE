package goodpartner.chat.repository;


import goodpartner.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> findChatHistoryByUserId(UUID userId);

    Long countByStatus(Chat.Status status);

    List<Chat> findTop3ByStatusOrderByCreatedAtDesc(Chat.Status status);

    List<Chat> findTop3ByStatusAndUserIdOrderByCreatedAtDesc(Chat.Status status, UUID userId);
}
