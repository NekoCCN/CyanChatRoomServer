package cc.nekocc.cyanchatroomserver.presentation.dto.response;

import java.util.UUID;

public record CheckFriendshipExistsResponse(UUID client_request_id, boolean exists)
{  }
