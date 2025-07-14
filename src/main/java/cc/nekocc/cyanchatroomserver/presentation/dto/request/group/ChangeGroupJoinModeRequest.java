package cc.nekocc.cyanchatroomserver.presentation.dto.request.group;

import cc.nekocc.cyanchatroomserver.domain.model.group.GroupJoinMode;
import java.util.UUID;

public record ChangeGroupJoinModeRequest(UUID client_request_id, UUID group_id, GroupJoinMode new_mode)
{  }