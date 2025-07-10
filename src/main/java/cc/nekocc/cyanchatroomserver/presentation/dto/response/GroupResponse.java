package cc.nekocc.cyanchatroomserver.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public record GroupResponse(UUID id, String name, UUID creator_id, List<UUID> members)
{  }
