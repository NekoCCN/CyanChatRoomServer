package cc.nekocc.cyanchatroomserver.presentation.dto.request;

import cc.nekocc.cyanchatroomserver.domain.model.user.UserStatus;
import java.util.UUID;

public record UpdateProfileRequest(UUID client_request_id, String nick_name, String signature,
                                   String avatar_file_id, UserStatus status)
{  }