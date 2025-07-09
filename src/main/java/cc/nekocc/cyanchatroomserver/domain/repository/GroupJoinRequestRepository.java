package cc.nekocc.cyanchatroomserver.domain.repository;

import cc.nekocc.cyanchatroomserver.domain.model.group.GroupJoinRequest;
import java.util.Optional;
import java.util.UUID;
public interface GroupJoinRequestRepository
{
    void save(GroupJoinRequest request);
    void update(GroupJoinRequest request);
    Optional<GroupJoinRequest> findPendingRequest(UUID group_id, UUID user_id);
}