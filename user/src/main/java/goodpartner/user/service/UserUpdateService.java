package goodpartner.user.service;


import goodpartner.user.application.dto.request.UserUpdateRequest;
import goodpartner.user.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserUpdateService {

    public void updateUser(User user, UserUpdateRequest dto) {
        user.update(dto);
    }
}
