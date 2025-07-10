package cc.nekocc.cyanchatroomserver.presentation.dto.request;

public record ChangeUsernameRequest(String new_user_name, String current_password)
{  }
