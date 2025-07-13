package cc.nekocc.cyanchatroomserver.application.impl;

import cc.nekocc.cyanchatroomserver.application.service.UserApplicationService;
import cc.nekocc.cyanchatroomserver.domain.model.message.OfflineMessage;
import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.domain.repository.OfflineMessageRepository;
import cc.nekocc.cyanchatroomserver.domain.repository.UserRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.OfflineMessageRepositoryImpl;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.UserRepositoryImpl;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserApplicationServiceImpl implements UserApplicationService
{
    private final UserRepository user_repository_ = new UserRepositoryImpl();
    private final OfflineMessageRepository offline_message_repository_ = new OfflineMessageRepositoryImpl();

    @Override
    public User register(String user_name, String password, String nick_name, String sign) throws Exception
    {
        if (user_repository_.findByUsername(user_name).isPresent())
        {
            throw new Exception("用户名 '" + user_name + "' 已被注册。");
        }

        String password_hash = BCrypt.hashpw(password, BCrypt.gensalt());
        User new_user = new User(user_name, password_hash, nick_name, sign);

        user_repository_.save(new_user);

        return new_user;
    }

    @Override
    public Optional<User> getUserById(UUID userId) throws Exception
    {
        return user_repository_.findById(userId);
    }

    @Override
    public Optional<User> login(String user_name, String password)
    {
        return user_repository_.findByUsername(user_name)
                .filter(user -> user.checkPassword(password));
    }

    @Override
    public void processPostLoginTasks(User user, Channel channel)
    {
        List<OfflineMessage> messages = null;
        try
        {
            messages = offline_message_repository_.findMessagesForUser(user.getId(), null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if (messages.isEmpty())
        {
            return;
        }
        System.out.println("正在为用户 " + user.getId() + " 投递 " + messages.size() + " 条离线消息...");
        for (OfflineMessage msg : messages)
        {
            channel.writeAndFlush(new TextWebSocketFrame(msg.getMessagePayload()));
        }
        List<UUID> messageIds = messages.stream().map(OfflineMessage::getId).collect(Collectors.toList());
        offline_message_repository_.deleteByIds(messageIds);
        System.out.println("用户 " + user.getId() + " 的离线消息已清理完毕");
    }
}
