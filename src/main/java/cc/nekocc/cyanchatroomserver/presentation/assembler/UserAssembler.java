package cc.nekocc.cyanchatroomserver.presentation.assembler;

import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.UserOperatorResponse;

public class UserAssembler
{
    public static UserOperatorResponse.UserDTO toDTO(User user)
    {
        if (user == null)
        {
            return null;
        }
        return new UserOperatorResponse.UserDTO(
                user.getId(),
                user.getNickname(),
                user.getAvatarUrl(),
                user.getSignature(),
                user.getStatus()
        );
    }
}