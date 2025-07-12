package cc.nekocc.cyanchatroomserver.presentation.dto.request;

import java.util.UUID;

public record FetchKeysRequest(UUID client_request_id, UUID user_id)
{  }
