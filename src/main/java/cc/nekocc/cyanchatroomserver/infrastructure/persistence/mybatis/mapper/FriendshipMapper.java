package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper;

import cc.nekocc.cyanchatroomserver.domain.model.friendship.Friendship;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.UUID;

public interface FriendshipMapper
{
    void insert(@Param("friendship") Friendship friendship);

    Friendship findById(UUID id);

    Friendship findByUserIds(@Param("user_one_id") UUID user_one_id, @Param("user_two_id") UUID user_two_id);

    List<Friendship> findActiveByUserId(UUID user_id);

    List<Friendship> findByUserId(UUID user_id);

    void update(Friendship friendship);

    void deleteById(UUID id);
}
