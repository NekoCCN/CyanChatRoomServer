package cc.nekocc.cyanchatroomserver.presentation.dto.request;

import cc.nekocc.cyanchatroomserver.domain.model.group.GroupMemberRole;
import java.util.UUID;

public record SetMemberRoleRequest(UUID group_id, UUID target_user_id, GroupMemberRole new_role)
{  }
