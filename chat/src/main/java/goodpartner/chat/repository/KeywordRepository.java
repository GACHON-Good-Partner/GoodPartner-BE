package goodpartner.chat.repository;

import goodpartner.chat.entity.Chat;
import goodpartner.chat.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    List<Keyword> findByChat(Chat chat);
}
