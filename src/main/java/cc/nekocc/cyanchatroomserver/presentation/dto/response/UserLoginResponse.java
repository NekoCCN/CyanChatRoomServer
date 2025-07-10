package cc.nekocc.cyanchatroomserver.presentation.dto.response;

import java.util.UUID;

public record UserLoginResponse(boolean success, String message, UserDTO user)
{
    public record UserDTO(UUID id, String nickname, String avatar_url, String signature)
    {  }
}
