package cc.nekocc.cyanchatroomserver.application.service;

import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import java.util.Optional;
import java.util.UUID;

public interface ProfileApplicationService
{
    Optional<User> updateProfile(UUID user_id, String nick_name, String signature, String avatar_file_id);
    void changeUsername(UUID user_id, String current_password, String new_user_name) throws Exception;
    void changePassword(UUID user_id, String current_password, String new_password) throws Exception;
}