package cc.nekocc.cyanchatroomserver.application.impl;

import cc.nekocc.cyanchatroomserver.application.service.OfflineMessageService;
import cc.nekocc.cyanchatroomserver.domain.model.message.OfflineMessage;

import java.util.List;
import java.util.UUID;

public class OfflineMessageServiceImpl implements OfflineMessageService
{

    @Override
    public List<OfflineMessage> pullMessagesOnce(UUID user_id, List<UUID> group_ids)
    {
        return List.of();
    }

    @Override
    public List<OfflineMessage> findMessagesForUser(UUID user_id, List<UUID> group_ids)
    {
        return List.of();
    }

    @Override
    public void deleteByIds(List<UUID> message_ids)
    {

    }
}
