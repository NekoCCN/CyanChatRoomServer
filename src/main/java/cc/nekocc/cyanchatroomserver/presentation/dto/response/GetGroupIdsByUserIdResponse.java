package cc.nekocc.cyanchatroomserver.presentation.dto.response;

import java.util.UUID;

public record GetGroupIdsByUserIdResponse(
    UUID client_request_id,
    UUID[] group_ids
)
{  }
