package cc.nekocc.cyanchatroomserver.application.service;

import cc.nekocc.cyanchatroomserver.domain.model.message.OfflineMessage;
import java.util.List;
import java.util.UUID;

public interface OfflineMessageService
{
    List<OfflineMessage> pullMessagesOnce(UUID user_id, List<UUID> group_ids);

    List<OfflineMessage> findMessagesForUser(UUID user_id, List<UUID> group_ids);

    void deleteByIds(List<UUID> message_ids);
}
