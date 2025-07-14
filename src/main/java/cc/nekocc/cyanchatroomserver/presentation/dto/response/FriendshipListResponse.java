package cc.nekocc.cyanchatroomserver.presentation.dto.response;

import cc.nekocc.cyanchatroomserver.domain.model.friendship.Friendship;
import java.util.List;
import java.util.UUID;

public record FriendshipListResponse(UUID client_request_id, boolean status, List<Friendship> friendships)
{  }
