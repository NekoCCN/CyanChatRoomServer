package cc.nekocc.cyanchatroomserver.presentation.dto.response;

import cc.nekocc.cyanchatroomserver.domain.model.group.GroupMember;

import java.util.UUID;

public record GetGroupMembersResponse(
    UUID client_request_id,
    GroupMember[] group_members
)
{  }
