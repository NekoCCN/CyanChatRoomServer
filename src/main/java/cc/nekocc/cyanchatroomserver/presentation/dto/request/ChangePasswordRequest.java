package cc.nekocc.cyanchatroomserver.presentation.dto.request;

public record ChangePasswordRequest(String current_password, String new_password)
{  }
