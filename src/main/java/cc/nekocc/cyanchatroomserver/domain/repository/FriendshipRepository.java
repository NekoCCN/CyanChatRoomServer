package cc.nekocc.cyanchatroomserver.domain.repository;

import cc.nekocc.cyanchatroomserver.domain.model.friendship.Friendship;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository
{
    void insert(Friendship friendship);
    Optional<Friendship> findById(UUID id);
    Optional<Friendship> findByUserIds(UUID user_one_id, UUID user_two_id);
    List<Friendship> findActiveByUserId(UUID userId);
    List<Friendship> findByUserId(UUID userId);
    void update(Friendship friendship);
    void deleteById(UUID id);
}