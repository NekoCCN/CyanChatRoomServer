package cc.nekocc.cyanchatroomserver.application.impl;

import cc.nekocc.cyanchatroomserver.application.service.KeyManagementService;
import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.domain.repository.UserRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.UserRepositoryImpl;
import java.util.Optional;
import java.util.UUID;

public class KeyManagementServiceImpl implements KeyManagementService
{
    private final UserRepository user_repository_ = new UserRepositoryImpl();

    @Override
    public void publishKeys(UUID user_id, String public_key_bundle_json)
    {
        user_repository_.findById(user_id).ifPresent(user ->
        {
            user.setPublicKeyBundle(public_key_bundle_json);
            user_repository_.updatePublicKeyBundle(user);
        });
    }

    @Override
    public Optional<String> fetchKeys(UUID user_id)
    {
        return user_repository_.findById(user_id)
                .map(User::getPublicKeyBundle);
    }
}