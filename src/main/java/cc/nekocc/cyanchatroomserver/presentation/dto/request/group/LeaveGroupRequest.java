package cc.nekocc.cyanchatroomserver.presentation.dto.request.group;

import java.util.UUID;

public record LeaveGroupRequest(UUID client_request_id, UUID group_id)
{  }