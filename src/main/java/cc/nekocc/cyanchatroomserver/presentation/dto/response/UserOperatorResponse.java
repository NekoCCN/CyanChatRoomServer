package cc.nekocc.cyanchatroomserver.presentation.dto.response;

import cc.nekocc.cyanchatroomserver.domain.model.user.UserStatus;

import java.util.UUID;

public record UserOperatorResponse(UUID client_request_id, boolean success, String message, UserDTO user)
{
    public record UserDTO(UUID id, String nickname, String avatar_url, String signature, UserStatus status)
    {  }
}
