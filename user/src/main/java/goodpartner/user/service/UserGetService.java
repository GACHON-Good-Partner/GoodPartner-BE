package goodpartner.user.service;


import goodpartner.user.application.exception.UserNotFoundException;
import goodpartner.user.entity.User;
import goodpartner.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserGetService {

    private final UserRepository userRepository;

    public User getUser(Long kakaoId) {
        return userRepository.findByKakaoId(kakaoId)
                .orElseThrow(UserNotFoundException::new);
    }

    public User getUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    public boolean check(Long kakaoId) {
        return userRepository.existsByKakaoId(kakaoId);
    }
}
