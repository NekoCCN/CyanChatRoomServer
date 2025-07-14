package cc.nekocc.cyanchatroomserver.presentation.dto.request.friendship;

import java.util.UUID;

public record GetActiveFriendshipListRequest(UUID client_request_id, UUID user_id)
{  }
