package cc.nekocc.cyanchatroomserver.infrastructure.session;

import io.netty.channel.Channel;
import java.util.Map;
import java.util.UUID; // ✅ 引入UUID
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager
{
    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<UUID, Channel> user_channel_map_ = new ConcurrentHashMap<>();
    private final Map<Channel, UUID> channel_user_map_ = new ConcurrentHashMap<>();

    private SessionManager()
    {  }

    public static SessionManager getInstance()
    {
        return INSTANCE;
    }

    /**
     * 用户登录, 建立会话。
     *
     * @param user_id 用户ID (UUID)
     * @param channel 用户的网络连接通道
     */
    public void login(UUID user_id, Channel channel)
    {
        user_channel_map_.put(user_id, channel);
        channel_user_map_.put(channel, user_id);
        System.out.println("SESSION: 用户 " + user_id + " 已登录, Channel ID: " + channel.id());
    }

    /**
     * 用户登出或连接断开, 清理会话。
     *
     * @param channel 网络连接通道
     */
    public void logout(Channel channel)
    {
        UUID userId = channel_user_map_.remove(channel);
        if (userId != null)
        {
            user_channel_map_.remove(userId);
            System.out.println("SESSION: 用户 " + userId + " 已登出, Channel ID: " + channel.id());
        }
    }

    /**
     * 根据用户ID获取其网络连接通道。
     *
     * @param user_id 用户ID (UUID)
     * @return Channel, 如果用户不在线则返回 null
     */
    public Channel getChannel(UUID user_id)
    {
        return user_channel_map_.get(user_id);
    }

    /**
     * 根据 Channel 获取用户ID
     *
     * @param channel 网络连接通道
     * @return 用户ID (UUID), 如果未登录则返回 null
     */
    public UUID getUserId(Channel channel)
    {
        return channel_user_map_.get(channel);
    }
}