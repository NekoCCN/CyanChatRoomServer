package cc.nekocc.cyanchatroomserver.presentation.dto.response;

public record GetGroupIdsByUserIdResponse(
    String client_request_id,
    String[] group_ids
)
{  }
