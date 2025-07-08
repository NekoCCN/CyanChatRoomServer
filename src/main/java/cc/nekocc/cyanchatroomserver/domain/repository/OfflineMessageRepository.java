package cc.nekocc.cyanchatroomserver.domain.repository;

import cc.nekocc.cyanchatroomserver.domain.model.message.OfflineMessage;
import java.util.List;
import java.util.UUID;

public interface OfflineMessageRepository
{
    void save(OfflineMessage offlineMessage);

    /**
     * 查找一个用户的所有离线消息 (包括发给他的私聊, 和发给他所在群的群聊)
     * @param user_id 用户的UUID
     * @param group_ids 用户所在的所有群组的UUID列表
     * @return 离线消息列表
     */
    List<OfflineMessage> findMessagesForUser(UUID user_id, List<UUID> group_ids);

    /**
     * 删除一个用户的所有已投递的离线消息
     * @param message_ids 要删除的消息ID列表
     */
    void deleteByIds(List<Long> message_ids);
}