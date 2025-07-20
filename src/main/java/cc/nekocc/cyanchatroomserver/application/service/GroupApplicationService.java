package cc.nekocc.cyanchatroomserver.application.service;

import cc.nekocc.cyanchatroomserver.domain.model.group.Group;
import cc.nekocc.cyanchatroomserver.domain.model.group.GroupJoinMode;
import cc.nekocc.cyanchatroomserver.domain.model.group.GroupMemberRole;

import java.util.List;
import java.util.UUID;

/**
 * 群组应用服务 - 负责所有与群组相关的复杂业务流程
 */
public interface GroupApplicationService
{

    /**
     * 创建一个新群组
     *
     * @param creator_id 创建者的用户ID
     * @param group_name 群组名称
     */
    Group createGroup(UUID creator_id, String group_name, List<UUID> initial_member_ids) throws Exception;

    /**
     * 用户申请加入群组
     *
     * @param user_id 申请加入的用户ID
     * @param group_id 目标群组ID
     */
    void requestToJoinGroup(UUID user_id, UUID group_id, String request_message) throws Exception;

    /**
     * 管理员或创建者处理一个加群请求
     * @param handler_id 处理请求的用户ID (管理员或创建者)
     * @param request_id 加群请求的ID
     */
    public void handleJoinRequest(UUID group_id, UUID handler_id, UUID request_id, boolean is_approved) throws Exception;

    /**
     * 将一个成员移出群组
     * @param operator_id 操作的用户ID (管理员或创建者)
     * @param group_id 目标群组ID
     * @param target_user_id 被移除的用户ID
     */
    void removeMember(UUID operator_id, UUID group_id, UUID target_user_id) throws Exception;

    /**
     * 用户主动退出群组 (如果是创建者, 则解散群组)
     * @param user_id 退出的用户ID
     * @param group_id 目标群组ID
     */
    void leaveGroup(UUID user_id, UUID group_id) throws Exception;

    /**
     * 设置群组成员的角色
     * @param operator_id 操作的用户ID (管理员或创建者)
     * @param group_id 目标群组ID
     */
    void setMemberRole(UUID operator_id, UUID group_id, UUID target_user_id, GroupMemberRole new_role) throws Exception;

    /**
     * 更改群组的加入方式
     * @param operator_id 操作的用户ID (管理员或创建者)
     * @param group_id 目标群组ID
     * @param new_mode 新的加入方式
     */
    void changeGroupJoinMode(UUID operator_id, UUID group_id, GroupJoinMode new_mode) throws Exception;

    /**
     * 获取用户所在的所有群组ID
     * @param user_id 用户ID
     * @return 群组ID列表
     */
    List<UUID> getGroupIdsByUserId(UUID user_id) throws Exception;
}
