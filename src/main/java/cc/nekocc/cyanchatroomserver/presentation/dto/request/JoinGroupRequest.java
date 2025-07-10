package cc.nekocc.cyanchatroomserver.presentation.dto.request;

import java.util.UUID;

public record JoinGroupRequest(UUID group_id, String request_message)
{  }