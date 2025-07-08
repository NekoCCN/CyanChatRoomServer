package cc.nekocc.cyanchatroomserver.presentation.dto.response;

public record UserLoginResponse(boolean success, String message, UserDTO user)
{
    public record UserDTO(Integer id, String nickname, String avatar_url, String signature)
    {  }
}
