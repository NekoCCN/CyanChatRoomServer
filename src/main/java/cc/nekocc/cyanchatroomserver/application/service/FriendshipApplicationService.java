package cc.nekocc.cyanchatroomserver.application.service;

import cc.nekocc.cyanchatroomserver.domain.model.friendship.Friendship;

import java.util.List;
import java.util.UUID;

public interface FriendshipApplicationService
{
    /**
     * 发送好友申请
     * @param sender_id 发送者ID
     * @param receiver_id 接收者ID
     * @param message 附加消息
     */
    void sendFriendshipRequest(UUID sender_id, UUID receiver_id);

    /**
     * 接受好友申请
     * @param user_id 请求的用户ID
     * @param request_id 好友申请ID
     */
    void acceptFriendshipRequest(UUID user_id, UUID request_id);

    /**
     * 拒绝好友申请
     * @param user_id 请求的用户ID
     * @param request_id 好友申请ID
     */
    void rejectFriendshipRequest(UUID user_id, UUID request_id);

    /**
     * 检查两个用户之间的好友关系是否存在
     * @param user_id1 第一个用户ID
     * @param user_id2 第二个用户ID
     * @return 如果存在好友关系则返回true，否则返回false
     */
    boolean checkFriendshipExists(UUID user_id1, UUID user_id2);

    /**
     * 获取用户的好友列表
     * @param user_id 用户ID
     * @return 好友列表
     */
    List<Friendship> getFriendshipList(UUID user_id);

    /**
     * 获取用户的同意的好友列表
     * @param user_id 用户ID
     * @return 活跃好友列表
     */
    List<Friendship> getActiveFriendshipList(UUID user_id);
}
