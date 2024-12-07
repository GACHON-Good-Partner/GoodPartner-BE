package goodpartner.be.domain.chat.repository;

import goodpartner.be.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> findChatHistoryByUserId(UUID userId);

    List<Chat> findTop3ByStatusOrderByCreatedAtDesc(Chat.Status status);
}
