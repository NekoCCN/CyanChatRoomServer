package cc.nekocc.cyanchatroomserver.presentation.dto.request.group;

import java.util.UUID;

public record JoinGroupRequest(UUID client_request_id, UUID group_id, String request_message)
{  }