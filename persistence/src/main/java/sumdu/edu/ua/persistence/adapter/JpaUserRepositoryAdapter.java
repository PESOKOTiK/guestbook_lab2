package sumdu.edu.ua.persistence.adapter;

import org.springframework.stereotype.Component;
import sumdu.edu.ua.core.domain.User;
import sumdu.edu.ua.core.port.UserRepositoryPort;
import sumdu.edu.ua.persistence.entity.UserEntity;
import sumdu.edu.ua.persistence.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaUserRepositoryAdapter implements UserRepositoryPort {
    private final UserRepository userRepository;

    public JpaUserRepositoryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = new UserEntity(user.getId(), user.getUsername(), user.getPassword(), user.getRole());
        return toDomain(userRepository.save(entity));
    }

    private User toDomain(UserEntity e) {
        return new User(e.getId(), e.getUsername(), e.getPassword(), e.getRole());
    }
}
