package goodpartner.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest(
        @NotBlank String authCode

) {
}
