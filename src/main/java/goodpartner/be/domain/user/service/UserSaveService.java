package goodpartner.be.domain.user.service;

import goodpartner.be.domain.user.entity.User;
import goodpartner.be.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserSaveService {

    private final UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
