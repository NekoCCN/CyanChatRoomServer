package cc.nekocc.cyanchatroomserver.application.impl;

import cc.nekocc.cyanchatroomserver.application.service.GroupApplicationService;
import cc.nekocc.cyanchatroomserver.domain.model.group.*;
import cc.nekocc.cyanchatroomserver.domain.repository.GroupJoinRequestRepository;
import cc.nekocc.cyanchatroomserver.domain.repository.GroupMemberRepository;
import cc.nekocc.cyanchatroomserver.domain.repository.GroupRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.config.MyBatisUtil;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.GroupJoinRequestRepositoryImpl;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.GroupMemberRepositoryImpl;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.GroupRepositoryImpl;
import org.apache.ibatis.session.SqlSession;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GroupApplicationServiceImpl implements GroupApplicationService
{
    private final GroupRepository group_repository_ = new GroupRepositoryImpl();
    private final GroupMemberRepository group_member_repository_ = new GroupMemberRepositoryImpl();
    private final GroupJoinRequestRepository group_join_request_repository_ = new GroupJoinRequestRepositoryImpl();

    @Override
    public Group createGroup(UUID creator_id, String group_name, List<UUID> initial_member_ids) throws Exception
    {
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession(false))
        {
            try
            {
                GroupRepository group_repo = new GroupRepositoryImpl(session);
                GroupMemberRepository member_repo = new GroupMemberRepositoryImpl(session);

                Group group = new Group(group_name, creator_id);
                group_repo.save(group);

                if (initial_member_ids == null)
                {
                    initial_member_ids = new ArrayList<>();
                }
                if (!initial_member_ids.contains(creator_id))
                {
                    initial_member_ids.add(creator_id);
                }

                List<GroupMember> members = new ArrayList<>();
                for (UUID userId : initial_member_ids)
                {
                    GroupMemberRole role = userId.equals(creator_id) ? GroupMemberRole.CREATOR : GroupMemberRole.MEMBER;
                    members.add(new GroupMember(group.getId(), userId, role));
                }
                member_repo.saveBatch(members);

                session.commit();
                return group;
            } catch (Exception e)
            {
                session.rollback();
                throw new Exception("创建群组失败: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void requestToJoinGroup(UUID user_id, UUID group_id, String request_message) throws Exception
    {
        Group group = group_repository_.findById(group_id)
                .orElseThrow(() -> new Exception("群组不存在。"));

        if (group_member_repository_.findByGroupAndUser(group_id, user_id).isPresent())
        {
            throw new Exception("您已经是该群组成员。");
        }

        if (group.getJoinMode() == GroupJoinMode.DIRECT)
        {
            GroupMember newMember = new GroupMember(group_id, user_id, GroupMemberRole.MEMBER);
            group_member_repository_.save(newMember);
            // TODO: 发送"XXX已加入群聊"的通知
        } else
        {
            if (group_join_request_repository_.findPendingRequest(group_id, user_id).isPresent())
            {
                throw new Exception("您已发送过请求，请等待管理员处理。");
            }
            GroupJoinRequest request = new GroupJoinRequest(group_id, user_id, request_message);
            group_join_request_repository_.save(request);
            // TODO: 向所有管理员和群主推送"XXX请求加入群聊"的通知
        }
    }

    @Override
    public void handleJoinRequest(UUID group_id, UUID handler_id, UUID request_id, boolean is_approved) throws Exception
    {
        GroupMember handler = group_member_repository_.findByGroupAndUser(group_id, handler_id)
                .orElseThrow(() -> new Exception("操作员不是该群组成员。"));

        GroupJoinRequest request = group_join_request_repository_.findPendingRequest(group_id, request_id)
                .orElseThrow(() -> new Exception("加群请求不存在或已被处理。"));

        if (handler.getRole() == GroupMemberRole.MEMBER)
        {
            throw new Exception("权限不足，只有管理员或群主才能处理请求。");
        }

        if (is_approved)
        {
            request.approve(handler_id);
            GroupMember newMember = new GroupMember(request.getGroupId(), request.getUserId(), GroupMemberRole.MEMBER);
            group_member_repository_.save(newMember);
        } else
        {
            request.reject(handler_id);
        }
        group_join_request_repository_.update(request);
        // TODO: 向申请者推送"您的加群请求已被批准/拒绝"的通知
    }

    @Override
    public void removeMember(UUID operator_id, UUID group_id, UUID target_user_id) throws Exception
    {
        GroupMember operator = group_member_repository_.findByGroupAndUser(group_id, operator_id)
                .orElseThrow(() -> new Exception("操作员不是该群组成员。"));
        GroupMember target = group_member_repository_.findByGroupAndUser(group_id, target_user_id)
                .orElseThrow(() -> new Exception("目标用户不是该群组成员。"));

        if (!operator.canManage(target))
        {
            throw new Exception("权限不足，无法移除该成员。");
        }
        group_member_repository_.delete(group_id, target_user_id);
        // TODO: 向被移除者和群内广播"XXX已被移出群聊"的通知
    }

    @Override
    public void leaveGroup(UUID user_id, UUID group_id) throws Exception
    {
        Group group = group_repository_.findById(group_id)
                .orElseThrow(() -> new Exception("群组不存在。"));

        if (group.isCreator(user_id))
        {
            group_repository_.deleteById(group_id);
            // TODO: 向所有群成员广播"群主已解散该群"的通知
        } else
        {
            group_member_repository_.delete(group_id, user_id);
            // TODO: 向群内广播"XXX已退出群聊"的通知
        }
    }

    @Override
    public void setMemberRole(UUID operator_id, UUID group_id, UUID target_user_id, GroupMemberRole new_role) throws Exception
    {
        GroupMember operator = group_member_repository_.findByGroupAndUser(group_id, operator_id)
                .orElseThrow(() -> new Exception("操作员不是该群组成员。"));
        GroupMember target = group_member_repository_.findByGroupAndUser(group_id, target_user_id)
                .orElseThrow(() -> new Exception("目标用户不是该群组成员。"));

        if (operator.getRole() != GroupMemberRole.CREATOR)
        {
            throw new Exception("只有群主才能设置管理员。");
        }
        if (target.getUserId().equals(operator_id))
        {
            throw new Exception("不能更改自己的角色。");
        }

        target.setRole(new_role);
        group_member_repository_.update(target);
        // TODO: 向群内广播权限变更通知
    }

    @Override
    public void changeGroupJoinMode(UUID operator_id, UUID group_id, GroupJoinMode new_mode) throws Exception
    {
        Group group = group_repository_.findById(group_id)
                .orElseThrow(() -> new Exception("群组不存在。"));
        GroupMember operator = group_member_repository_.findByGroupAndUser(group_id, operator_id)
                .orElseThrow(() -> new Exception("操作员不是该群组成员。"));

        if (operator.getRole() == GroupMemberRole.MEMBER)
        {
            throw new Exception("权限不足，只有管理员或群主才能更改加群方式。");
        }

        group.changeJoinMode(new_mode);
        group_repository_.update(group);
        // TODO: 向群内广播设置变更通知
    }
}