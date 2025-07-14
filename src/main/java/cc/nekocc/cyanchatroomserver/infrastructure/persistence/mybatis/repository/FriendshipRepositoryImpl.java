package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository;

import cc.nekocc.cyanchatroomserver.domain.model.friendship.Friendship;
import cc.nekocc.cyanchatroomserver.domain.repository.FriendshipRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.config.MyBatisUtil;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper.FriendshipMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FriendshipRepositoryImpl implements FriendshipRepository
{
    @Override
    public void insert(Friendship friendship)
    {
        MyBatisUtil.executeUpdate(session -> session.getMapper(FriendshipMapper.class).insert(friendship));
    }

    @Override
    public Optional<Friendship> findById(UUID id)
    {
        return MyBatisUtil.executeQuery(session ->
                Optional.ofNullable(session.getMapper(FriendshipMapper.class).findById(id)));
    }

    @Override
    public Optional<Friendship> findByUserIds(UUID user_one_id, UUID user_two_id)
    {
        return MyBatisUtil.executeQuery(session ->
                Optional.ofNullable(session.getMapper(FriendshipMapper.class)
                        .findByUserIds(user_one_id, user_two_id)));
    }

    @Override
    public List<Friendship> findActiveByUserId(UUID userId)
    {
        return MyBatisUtil.executeQuery(session ->
                session.getMapper(FriendshipMapper.class).findActiveByUserId(userId));
    }

    @Override
    public List<Friendship> findByUserId(UUID user_id)
    {
        return MyBatisUtil.executeQuery(session ->
                session.getMapper(FriendshipMapper.class).findByUserId(user_id));
    }

    @Override
    public void update(Friendship friendship)
    {
        MyBatisUtil.executeUpdate(session ->
                session.getMapper(FriendshipMapper.class).update(friendship));
    }

    @Override
    public void deleteById(UUID id)
    {
        MyBatisUtil.executeUpdate(session ->
                session.getMapper(FriendshipMapper.class).deleteById(id));
    }
}
