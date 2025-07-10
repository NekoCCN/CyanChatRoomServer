package cc.nekocc.cyanchatroomserver.presentation.dto.request;

import java.util.List;
import java.util.UUID;

public record CreateGroupRequest(String group_name, List<UUID> member_ids)
{  }
