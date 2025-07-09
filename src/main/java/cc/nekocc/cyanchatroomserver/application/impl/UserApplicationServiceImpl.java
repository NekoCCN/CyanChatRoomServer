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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserApplicationServiceImpl implements UserApplicationService
{
    private final UserRepository user_repository_ = new UserRepositoryImpl();
    private final OfflineMessageRepository offline_message_repository_ = new OfflineMessageRepositoryImpl();

    @Override
    public Optional<User> login(String username, String password)
    {
        return user_repository_.findByUsername(username)
                .filter(user -> user.checkPassword(password));
    }

    @Override
    public void processPostLoginTasks(User user, Channel channel)
    {
        // 查找该用户的所有离线消息
        List<OfflineMessage> messages = offline_message_repository_.findMessagesForUser(user.getId(), null); // 登录时暂不处理群离线消息
        if (messages.isEmpty())
        {
            return;
        }

        System.out.println("正在为用户 " + user.getId() + " 投递 " + messages.size() + " 条离线消息...");

        // 逐条投递离线消息
        for (OfflineMessage msg : messages)
        {
            channel.writeAndFlush(new TextWebSocketFrame(msg.getMessagePayload()));
        }

        // 投递完成后, 从数据库中原子性地删除这些消息
        List<Long> messageIds = messages.stream().map(OfflineMessage::getId).collect(Collectors.toList());
        offline_message_repository_.deleteByIds(messageIds);
        System.out.println("用户 " + user.getId() + " 的离线消息已清理完毕");
    }
}