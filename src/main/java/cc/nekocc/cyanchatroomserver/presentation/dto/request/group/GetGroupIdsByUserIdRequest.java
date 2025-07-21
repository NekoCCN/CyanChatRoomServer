package cc.nekocc.cyanchatroomserver.presentation.dto.request.group;

import java.util.UUID;

public record GetGroupIdsByUserIdRequest(
    UUID client_request_id,
    UUID user_id
)
{  }
