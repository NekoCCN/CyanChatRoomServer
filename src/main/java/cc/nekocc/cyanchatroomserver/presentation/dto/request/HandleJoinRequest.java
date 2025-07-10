package cc.nekocc.cyanchatroomserver.presentation.dto.request;

import java.util.UUID;

public record HandleJoinRequest(UUID group_id, UUID request_id, boolean approved)
{  }