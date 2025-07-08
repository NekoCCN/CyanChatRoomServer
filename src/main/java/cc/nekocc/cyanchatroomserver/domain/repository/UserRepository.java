package cc.nekocc.cyanchatroomserver.domain.repository;

import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository
{
    void save(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID id);
    void update(User user);
    void updatePublicKeyBundle(User user);
}