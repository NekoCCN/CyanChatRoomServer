package cc.nekocc.cyanchatroomserver.presentation.dto.request.user;

import java.util.UUID;

public record GetUuidByUsernameRequest(UUID client_request, String username)
{  }
