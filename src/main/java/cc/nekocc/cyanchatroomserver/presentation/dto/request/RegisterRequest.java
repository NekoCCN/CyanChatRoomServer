package cc.nekocc.cyanchatroomserver.presentation.dto.request;

import java.util.UUID;

public record RegisterRequest(UUID client_request_id,
                              String username, String password, String nick_name, String sign)
{  }
