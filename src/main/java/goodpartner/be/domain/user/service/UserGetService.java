package goodpartner.be.domain.user.service;

import goodpartner.be.domain.user.application.exception.UserNotFoundException;
import goodpartner.be.domain.user.entity.User;
import goodpartner.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserGetService {

    private final UserRepository userRepository;

    public User getUser(Long kakaoId) {
        return userRepository.findByKakaoId(kakaoId)
                .orElseThrow(UserNotFoundException::new);
    }

    public boolean check(Long kakaoId) {
        return userRepository.existsByKakaoId(kakaoId);
    }
}
