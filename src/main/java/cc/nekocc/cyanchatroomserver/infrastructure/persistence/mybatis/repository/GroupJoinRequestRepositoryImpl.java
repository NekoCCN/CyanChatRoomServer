package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository;

import cc.nekocc.cyanchatroomserver.domain.model.group.GroupJoinRequest;
import cc.nekocc.cyanchatroomserver.domain.repository.GroupJoinRequestRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.config.MyBatisUtil;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper.GroupJoinRequestMapper;

import java.util.Optional;
import java.util.UUID;

public class GroupJoinRequestRepositoryImpl implements GroupJoinRequestRepository
{
    @Override
    public void save(GroupJoinRequest request)
    {
        MyBatisUtil.executeUpdate(session -> session.getMapper(GroupJoinRequestMapper.class)
               .insert(request));
    }

    @Override
    public void update(GroupJoinRequest request)
    {
        MyBatisUtil.executeUpdate(session -> session.getMapper(GroupJoinRequestMapper.class)
               .update(request));
    }

    @Override
    public Optional<GroupJoinRequest> findPendingRequest(UUID group_id, UUID user_id)
    {
        return MyBatisUtil.executeQuery(session ->
                Optional.ofNullable(session.getMapper(GroupJoinRequestMapper.class).
                        findPendingRequest(group_id, user_id))
        );
    }
}
