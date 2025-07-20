package cc.nekocc.cyanchatroomserver.presentation.dto.request.group;

public record GetGroupIdsByUserIdRequest(
    String client_request_id,
    String user_id
)
{  }
