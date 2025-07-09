package cc.nekocc.cyanchatroomserver.application.impl;

import cc.nekocc.cyanchatroomserver.application.service.ProfileApplicationService;
import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.domain.repository.UserRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.UserRepositoryImpl;
import java.util.Optional;
import java.util.UUID;

public class ProfileApplicationServiceImpl implements ProfileApplicationService
{
    private final UserRepository user_repository_ = new UserRepositoryImpl();

    @Override
    public Optional<User> updateProfile(UUID user_id, String nickname, String signature, String avatar_file_id)
    {
        return user_repository_.findById(user_id).map(user ->
        {
            user.updateProfile(nickname, signature, avatar_file_id);
            user_repository_.update(user);
            return user;
        });
    }
}