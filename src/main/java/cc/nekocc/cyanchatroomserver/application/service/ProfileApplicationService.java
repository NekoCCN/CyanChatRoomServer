package cc.nekocc.cyanchatroomserver.application.service;

import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import java.util.Optional;

public interface ProfileApplicationService
{
    Optional<User> updateProfile(Integer user_id, String nickname, String signature, String avatar_file_id);
}
