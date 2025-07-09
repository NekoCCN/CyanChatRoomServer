package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper;

import cc.nekocc.cyanchatroomserver.domain.model.group.GroupMember;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.UUID;
public interface GroupMemberMapper
{
    int insert(GroupMember member);
    void insertBatch(@Param("list") List<GroupMember> members);
    int update(GroupMember member);
    GroupMember findByGroupAndUser(@Param("group_id") UUID group_id, @Param("user_id") UUID user_id);
    List<UUID> findUserIdsByGroupId(@Param("group_id") UUID group_id);
    List<UUID> findGroupIdsByUserId(@Param("user_id") UUID user_id);
    int delete(@Param("group_id") UUID group_id, @Param("user_id") UUID user_id);
}