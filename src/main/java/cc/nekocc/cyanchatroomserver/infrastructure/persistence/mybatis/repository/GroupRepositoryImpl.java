package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository;

import cc.nekocc.cyanchatroomserver.domain.model.group.Group;
import cc.nekocc.cyanchatroomserver.domain.repository.GroupRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.config.MyBatisUtil;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper.GroupMapper;
import org.apache.ibatis.session.SqlSession;

import java.util.Optional;
import java.util.UUID;

public class GroupRepositoryImpl implements GroupRepository
{
    private SqlSession session_;

    public GroupRepositoryImpl(SqlSession session)
    {
        session_ = session;
    }

    public GroupRepositoryImpl()
    {
        this(null);
    }

    @Override
    public void save(Group group)
    {
        if (session_ == null)
        {
            MyBatisUtil.executeUpdate(session -> session.getMapper(GroupMapper.class).insert(group));
        }
        session_.getMapper(GroupMapper.class).insert(group);
        session_.commit();
    }

    @Override
    public Optional<Group> findById(UUID id)
    {
        if (session_ == null)
        {
            return MyBatisUtil.executeQuery(session ->
                Optional.ofNullable(session.getMapper(GroupMapper.class).findById(id))
            );
        }

        return Optional.ofNullable(session_.getMapper(GroupMapper.class)
                .findById(id));
    }

    @Override
    public void update(Group group)
    {
        if (session_ == null)
        {
            MyBatisUtil.executeUpdate(session -> session.getMapper(GroupMapper.class).update(group));
            return;
        }

        session_.getMapper(GroupMapper.class).update(group);
        session_.commit();
    }

    @Override
    public void deleteById(UUID id)
    {
        if (session_ == null)
        {
            MyBatisUtil.executeUpdate(session -> session.getMapper(GroupMapper.class).deleteById(id));
            return;
        }

        session_.getMapper(GroupMapper.class).deleteById(id);
        session_.commit();
    }
}
