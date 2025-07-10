package cc.nekocc.cyanchatroomserver.presentation.dto.request;

import cc.nekocc.cyanchatroomserver.domain.model.group.GroupJoinMode;
import java.util.UUID;

public record ChangeGroupJoinModeRequest(UUID group_id, GroupJoinMode new_mode)
{  }