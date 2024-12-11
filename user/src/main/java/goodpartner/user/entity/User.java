package goodpartner.user.entity;


import goodpartner.global.common.entity.BaseTimeEntity;
import goodpartner.user.application.dto.request.UserUpdateRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    private Long kakaoId;

    private String name;

    private String email;

    private String tel;

    public static User of(Long kakaoId, String email, String name) {
        return User.builder()
                .kakaoId(kakaoId)
                .email(email)
                .name(name)
                .build();
    }

    public void update(UserUpdateRequest dto) {
        this.email = dto.email();
        this.name = dto.name();
        this.tel = dto.tel();
    }
}
