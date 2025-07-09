package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper;

import cc.nekocc.cyanchatroomserver.domain.model.group.GroupJoinRequest;
import org.apache.ibatis.annotations.Param;
import java.util.UUID;

public interface GroupJoinRequestMapper
{
    int insert(GroupJoinRequest request);
    int update(GroupJoinRequest request);
    GroupJoinRequest findPendingRequest(@Param("group_id") UUID group_id, @Param("user_id") UUID user_id);
}
