package goodpartner.user.service;

import goodpartner.user.entity.User;
import goodpartner.user.repository.UserRepository;
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
