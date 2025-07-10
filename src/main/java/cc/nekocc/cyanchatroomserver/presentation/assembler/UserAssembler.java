package cc.nekocc.cyanchatroomserver.presentation.assembler;

import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.UserLoginResponse;

public class UserAssembler
{
    public static UserLoginResponse.UserDTO toDTO(User user)
    {
        if (user == null)
        {
            return null;
        }
        return new UserLoginResponse.UserDTO(
                user.getId(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getSignature()
        );
    }
}