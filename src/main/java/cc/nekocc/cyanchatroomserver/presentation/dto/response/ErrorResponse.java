package cc.nekocc.cyanchatroomserver.presentation.dto.response;

import java.util.UUID;

public record ErrorResponse(String error, String request_type)
{  }