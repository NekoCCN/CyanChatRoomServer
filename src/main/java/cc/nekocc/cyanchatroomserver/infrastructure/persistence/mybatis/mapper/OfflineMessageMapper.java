package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper;

import cc.nekocc.cyanchatroomserver.domain.model.message.OfflineMessage;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.UUID;

public interface OfflineMessageMapper
{
    int insert(OfflineMessage offline_message);
    List<OfflineMessage> findMessagesForUser(
            @Param("user_id") UUID user_id,
            @Param("group_ids") List<UUID> group_ids
    );
    int deleteByIds(@Param("ids") List<Long> ids);
}