package cc.nekocc.cyanchatroomserver.presentation.dto.request.group;

import java.util.UUID;

public record HandleJoinRequest(UUID client_request_id, UUID group_id, UUID request_id, boolean approved)
{  }