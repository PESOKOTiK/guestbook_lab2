package sumdu.edu.ua.core.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.core.exception.NotFoundException;
import sumdu.edu.ua.core.exception.ValidationException;
import sumdu.edu.ua.core.port.UserRepositoryPort;

import java.util.List;

@Service
public class UserService {
    private final UserRepositoryPort userRepository;

    public UserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("ID користувача має бути додатним");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Користувача з ID " + id + " не знайдено"));
    }

    @Transactional
    public User save(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isBlank()) {
            throw new ValidationException("Ім'я користувача не може бути порожнім");
        }
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ROLE_USER");
        }
        return userRepository.save(user);
    }
}
