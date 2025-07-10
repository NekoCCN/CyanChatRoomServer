package cc.nekocc.cyanchatroomserver.presentation.dto.request;

import java.util.UUID;

public record ChatMessageRequest(String recipient_type, UUID recipient_id,
                                 String content_type, boolean is_encrypted, String content)
{  }
