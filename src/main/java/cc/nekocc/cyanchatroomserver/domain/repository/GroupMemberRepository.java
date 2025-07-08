package cc.nekocc.cyanchatroomserver.domain.repository;

import cc.nekocc.cyanchatroomserver.domain.model.group.GroupMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupMemberRepository
{
    void save(GroupMember member);
    void saveBatch(List<GroupMember> members);
    Optional<GroupMember> findByGroupAndUser(UUID group_id, UUID user_id);
    List<UUID> findUserIdsByGroupId(UUID group_id);
    List<UUID> findGroupIdsByUserId(UUID user_id);
    void delete(UUID group_id, UUID user_id);
}