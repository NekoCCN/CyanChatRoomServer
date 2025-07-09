package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository;

import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.domain.repository.UserRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.config.MyBatisUtil;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper.UserMapper;

import java.util.Optional;
import java.util.UUID;

public class UserRepositoryImpl implements UserRepository
{
    @Override
    public void save(User user)
    {
        MyBatisUtil.executeUpdate(session ->
        {
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.insert(user);
        });
    }

    @Override
    public Optional<User> findByUsername(String username)
    {
        User user = MyBatisUtil.executeQuery(session ->
        {
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.findByUsername(username);
        });
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findById(UUID id)
    {
        User user = MyBatisUtil.executeQuery(session ->
        {
            UserMapper mapper = session.getMapper(UserMapper.class);
            return mapper.findById(id);
        });
        return Optional.ofNullable(user);
    }

    @Override
    public void update(User user)
    {
        MyBatisUtil.executeUpdate(session ->
        {
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.updateProfile(user);
        });
    }

    @Override
    public void updatePublicKeyBundle(User user)
    {
        MyBatisUtil.executeUpdate(session ->
        {
            UserMapper mapper = session.getMapper(UserMapper.class);
            mapper.updatePublicKeyBundle(user);
        });
    }
}