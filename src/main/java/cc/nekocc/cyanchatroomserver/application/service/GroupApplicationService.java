package cc.nekocc.cyanchatroomserver.application.service;

import cc.nekocc.cyanchatroomserver.domain.model.group.Group;
import java.util.List;

public interface GroupApplicationService
{
    /**
     * 创建一个新的群组
     * @param creatorId 创建者ID
     * @param groupName 群组名称
     * @param memberIds 初始成员ID列表
     * @return 创建成功的Group实体
     * @throws Exception 如果创建失败
     */
    Group createGroup(Integer creator_id, String group_name, List<Integer> member_ids) throws Exception;
}
