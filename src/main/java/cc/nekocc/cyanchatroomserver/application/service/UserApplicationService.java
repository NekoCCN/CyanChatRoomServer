package cc.nekocc.cyanchatroomserver.application.service;

import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import io.netty.channel.Channel;
import java.util.Optional;
import java.util.UUID;

public interface UserApplicationService
{
    /**
     * 处理用户登录请求
     *
     * @param username 用户名
     * @param password 密码
     * @return 包含User实体的Optional, 如果登录失败则为空
     */
    Optional<User> login(String username, String password);

    /**
     * 处理用户登录成功后的任务, 例如投递离线消息
     *
     * @param user    已登录的用户实体
     * @param channel 用户的网络通道
     */
    void processPostLoginTasks(User user, Channel channel);

    /**
     * 处理用户注册请求
     *
     * @param user_name 用户名
     * @param password  密码
     * @param nick_name 昵称
     * @return 创建成功后的User实体
     * @throws Exception 如果用户名已存在或注册失败
     */
    User register(String user_name, String password, String nick_name) throws Exception;
}