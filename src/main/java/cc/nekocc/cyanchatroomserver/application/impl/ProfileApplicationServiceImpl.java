package cc.nekocc.cyanchatroomserver.application.impl;

import cc.nekocc.cyanchatroomserver.application.service.ProfileApplicationService;
import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.domain.repository.UserRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.UserRepositoryImpl;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

public class ProfileApplicationServiceImpl implements ProfileApplicationService
{
    private final UserRepository user_repository_ = new UserRepositoryImpl();

    @Override
    public Optional<User> updateProfile(UUID user_id, String nick_name, String signature, String avatar_file_id)
    {
        return user_repository_.findById(user_id).map(user ->
        {
            user.updateProfile(nick_name, signature, avatar_file_id);
            user_repository_.update(user);
            return user;
        });
    }

    @Override
    public void changeUsername(UUID user_id, String current_password, String new_user_name) throws Exception
    {
        User user = user_repository_.findById(user_id)
                .orElseThrow(() -> new Exception("用户不存在。"));

        if (!user.checkPassword(current_password))
        {
            throw new Exception("当前密码不正确。");
        }

        if (user_repository_.findByUsername(new_user_name).isPresent())
        {
            throw new Exception("新的用户名已被占用。");
        }

        user.changeUsername(new_user_name);
        user_repository_.update(user);
    }

    @Override
    public void changePassword(UUID user_id, String current_password, String new_password) throws Exception
    {
        User user = user_repository_.findById(user_id)
                .orElseThrow(() -> new Exception("用户不存在。"));

        if (!user.checkPassword(current_password))
        {
            throw new Exception("当前密码不正确。");
        }

        String new_password_hash = BCrypt.hashpw(new_password, BCrypt.gensalt());
        user.setPasswordHash(new_password_hash);
        user_repository_.update(user);
    }
}