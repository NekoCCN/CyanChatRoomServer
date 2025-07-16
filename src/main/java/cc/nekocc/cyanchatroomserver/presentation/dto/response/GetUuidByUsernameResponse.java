package cc.nekocc.cyanchatroomserver.presentation.dto.response;

import java.util.UUID;

public record GetUuidByUsernameResponse(UUID client_request_id, boolean request_status, UUID user_id)
{  }
