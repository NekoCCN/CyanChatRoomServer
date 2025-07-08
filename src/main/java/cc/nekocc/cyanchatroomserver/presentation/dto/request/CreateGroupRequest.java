package cc.nekocc.cyanchatroomserver.presentation.dto.request;

import java.util.List;

public record CreateGroupRequest(String group_name, List<Integer> member_ids)
{  }
