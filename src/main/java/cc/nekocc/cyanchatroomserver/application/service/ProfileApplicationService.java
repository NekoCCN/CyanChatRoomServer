package cc.nekocc.cyanchatroomserver.application.service;

import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import java.util.Optional;
import java.util.UUID;

public interface ProfileApplicationService
{
    /**
     * 更新用户个人资料
     */
    Optional<User> updateProfile(UUID user_id, String nickname, String signature, String avatar_file_id);
}