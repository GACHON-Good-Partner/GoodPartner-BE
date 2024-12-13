package goodpartner.chat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long id;

    private String keyword;

    private String url;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private Chat chat;

    public static Keyword of(String keyword, String url, Chat chat) {
        return Keyword.builder()
                .keyword(keyword)
                .url(url)
                .chat(chat)
                .build();
    }
}
