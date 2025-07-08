package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper;

import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import org.apache.ibatis.annotations.Param;
import java.util.UUID;

public interface UserMapper
{
    int insert(User user);
    User findByUsername(@Param("username") String username);
    User findById(@Param("id") UUID id);
    int updateProfile(User user);
    int updateStatus(User user);
    int updatePublicKeyBundle(User user);
}