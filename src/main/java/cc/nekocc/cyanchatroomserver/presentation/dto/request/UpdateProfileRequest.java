package cc.nekocc.cyanchatroomserver.presentation.dto.request;

public record UpdateProfileRequest(String nickname, String signature,
                                   String avatar_file_id)
{  }
