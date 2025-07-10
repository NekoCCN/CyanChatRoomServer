package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository;

import cc.nekocc.cyanchatroomserver.domain.model.message.OfflineMessage;
import cc.nekocc.cyanchatroomserver.domain.repository.OfflineMessageRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.config.MyBatisUtil;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper.OfflineMessageMapper;
import java.util.List;
import java.util.UUID;

public class OfflineMessageRepositoryImpl implements OfflineMessageRepository
{

    @Override
    public void save(OfflineMessage offlineMessage)
    {
        MyBatisUtil.executeUpdate(session ->
                session.getMapper(OfflineMessageMapper.class).insert(offlineMessage)
        );
    }

    @Override
    public List<OfflineMessage> findMessagesForUser(UUID user_id, List<UUID> group_ids)
    {
        return MyBatisUtil.executeQuery(session ->
                session.getMapper(OfflineMessageMapper.class).findMessagesForUser(user_id, group_ids)
        );
    }

    @Override
    public void deleteByIds(List<Long> message_ids)
    {
        if (message_ids == null || message_ids.isEmpty())
        {
            return;
        }
        MyBatisUtil.executeUpdate(session ->
                session.getMapper(OfflineMessageMapper.class).deleteByIds(message_ids)
        );
    }
}
