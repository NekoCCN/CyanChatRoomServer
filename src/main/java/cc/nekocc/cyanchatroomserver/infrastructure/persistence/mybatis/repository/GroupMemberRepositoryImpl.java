package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository;

import cc.nekocc.cyanchatroomserver.domain.model.group.GroupMember;
import cc.nekocc.cyanchatroomserver.domain.repository.GroupMemberRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.config.MyBatisUtil;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper.GroupMemberMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GroupMemberRepositoryImpl implements GroupMemberRepository
{
    private final SqlSession session_;

    public GroupMemberRepositoryImpl(SqlSession session)
    {
        session_ = session;
    }
    public GroupMemberRepositoryImpl()
    {
        this(null);
    }

    @Override
    public void save(GroupMember member)
    {
        if (session_ == null)
        {
            MyBatisUtil.executeUpdate(session -> session.getMapper(GroupMemberMapper.class).insert(member));
            return;
        }

        session_.getMapper(GroupMemberMapper.class).insert(member);
        session_.commit();
    }

    @Override
    public void saveBatch(List<GroupMember> members)
    {
        if (members == null || members.isEmpty())
        {
            return;
        }

        if (session_ == null)
        {
            MyBatisUtil.executeUpdate(session -> session.getMapper(GroupMemberMapper.class).insertBatch(members));
            return;
        }

        session_.getMapper(GroupMemberMapper.class).insertBatch(members);
        session_.commit();
    }

    @Override
    public void update(GroupMember member)
    {
        if (session_ == null)
        {
            MyBatisUtil.executeUpdate(session -> session.getMapper(GroupMemberMapper.class).update(member));
            return;
        }

        session_.getMapper(GroupMemberMapper.class).update(member);
    }

    @Override
    public Optional<GroupMember> findByGroupAndUser(UUID group_id, UUID user_id)
    {
        if (session_ == null)
        {
            return MyBatisUtil.executeQuery(session ->
                Optional.ofNullable(session.getMapper(GroupMemberMapper.class).findByGroupAndUser(group_id, user_id))
            );
        }

        return Optional.ofNullable(session_.getMapper(GroupMemberMapper.class)
                .findByGroupAndUser(group_id, user_id));
    }

    @Override
    public List<UUID> findUserIdsByGroupId(UUID group_id)
    {
        if (session_ == null)
        {
            return MyBatisUtil.executeQuery(session ->
                session.getMapper(GroupMemberMapper.class).findUserIdsByGroupId(group_id)
            );
        }

        return session_.getMapper(GroupMemberMapper.class)
                .findUserIdsByGroupId(group_id);
    }

    @Override
    public List<UUID> findGroupIdsByUserId(UUID user_id)
    {
        if (session_ == null)
        {
            return MyBatisUtil.executeQuery(session ->
                session.getMapper(GroupMemberMapper.class).findGroupIdsByUserId(user_id)
            );
        }

        return session_.getMapper(GroupMemberMapper.class)
                .findGroupIdsByUserId(user_id);
    }

    @Override
    public void delete(UUID group_id, UUID user_id)
    {
        if (session_ == null)
        {
            MyBatisUtil.executeUpdate(session ->
                session.getMapper(GroupMemberMapper.class).delete(group_id, user_id)
            );
            return;
        }

        session_.getMapper(GroupMemberMapper.class).delete(group_id, user_id);
    }
}
