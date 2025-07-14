package cc.nekocc.cyanchatroomserver.application.impl;

import cc.nekocc.cyanchatroomserver.application.service.FriendshipApplicationService;
import cc.nekocc.cyanchatroomserver.domain.model.friendship.Friendship;
import cc.nekocc.cyanchatroomserver.domain.repository.FriendshipRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.FriendshipRepositoryImpl;

import java.util.List;
import java.util.UUID;

public class FriendshipApplicationServiceImpl implements FriendshipApplicationService
{
    private final FriendshipRepository friendship_repository_ = new FriendshipRepositoryImpl();

    @Override
    public void sendFriendshipRequest(UUID sender_id, UUID receiver_id)
    {
        if (friendship_repository_.findByUserIds(sender_id, receiver_id).isPresent())
        {
            throw new IllegalArgumentException("Friendship already exists between the users.");
        }
        if (sender_id.equals(receiver_id))
        {
            throw new IllegalArgumentException("Users cannot be friends with themselves.");
        }
        if (receiver_id == null)
        {
            throw new IllegalArgumentException("Sender and receiver IDs cannot be null.");
        }

        UUID action_id = sender_id;
        // Ensure sender_id is always less than receiver_id for consistency
        if (sender_id.compareTo(receiver_id) > 0)
        {
            UUID temp = sender_id;
            sender_id = receiver_id;
            receiver_id = temp;
        }

        Friendship friendship = new Friendship(sender_id, receiver_id, action_id);
        friendship_repository_.insert(friendship);
    }

    @Override
    public void acceptFriendshipRequest(UUID user_id, UUID request_id)
    {
        var friendship = friendship_repository_.findById(request_id)
                .orElseThrow(() -> new IllegalArgumentException("Friendship request not found."));


        friendship_repository_.update(friendship.acceptRequest(user_id));
    }

    @Override
    public void rejectFriendshipRequest(UUID user_id, UUID request_id)
    {
        var friendship = friendship_repository_.findById(request_id)
                .orElseThrow(() -> new IllegalArgumentException("Friendship request not found."));

        friendship_repository_.update(friendship.rejectRequest(user_id));
    }

    @Override
    public boolean checkFriendshipExists(UUID user_id1, UUID user_id2)
    {
        if (user_id1 == null || user_id2 == null)
        {
            throw new IllegalArgumentException("User IDs cannot be null.");
        }
        return friendship_repository_.findByUserIds(user_id1, user_id2).isPresent();
    }

    @Override
    public List<Friendship> getFriendshipList(UUID user_id)
    {
        if (user_id == null)
        {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        return friendship_repository_.findByUserId(user_id);
    }

    @Override
    public List<Friendship> getActiveFriendshipList(UUID user_id)
    {
        if (user_id == null)
        {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        return friendship_repository_.findActiveByUserId(user_id);
    }
}
