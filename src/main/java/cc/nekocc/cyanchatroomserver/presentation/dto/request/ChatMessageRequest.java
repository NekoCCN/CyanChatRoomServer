package cc.nekocc.cyanchatroomserver.presentation.dto.request;

public record ChatMessageRequest(String recipient_type, Integer recipient_id,
                                 String content_type, String content)
{  }
