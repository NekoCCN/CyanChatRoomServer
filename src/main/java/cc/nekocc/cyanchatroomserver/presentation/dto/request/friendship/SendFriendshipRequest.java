package cc.nekocc.cyanchatroomserver.presentation.dto.request.friendship;

import java.util.UUID;

public record SendFriendshipRequest(UUID client_request_id, UUID sender_id, UUID receiver_id)
{  }
