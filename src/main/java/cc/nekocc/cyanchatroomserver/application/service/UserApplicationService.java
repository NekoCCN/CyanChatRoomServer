package cc.nekocc.cyanchatroomserver.application.service;

import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import io.netty.channel.Channel;
import java.util.Optional;

public interface UserApplicationService
{
    Optional<User> login(String username, String password);
    void processPostLoginTasks(User user, Channel channel);
}
