package cc.nekocc.cyanchatroomserver.presentation.dto.response;

import java.util.List;

public record GroupResponse(Integer id, String name, Integer creatorI_id, List<Integer> members)
{  }
