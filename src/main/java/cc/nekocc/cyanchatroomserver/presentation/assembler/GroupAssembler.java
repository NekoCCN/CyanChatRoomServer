package cc.nekocc.cyanchatroomserver.presentation.assembler;

import cc.nekocc.cyanchatroomserver.domain.model.group.Group;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.GroupResponse;
import java.util.List;
import java.util.UUID;

public class GroupAssembler
{
    public static GroupResponse toDTO(UUID client_request_id, boolean success, Group group, List<UUID> member_ids)
    {
        if (group == null)
        {
            return null;
        }
        return new GroupResponse(
                client_request_id,
                success,
                group.getId(),
                group.getName(),
                group.getCreatorId(),
                member_ids
        );
    }
}