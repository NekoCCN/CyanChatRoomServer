package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.group;

import cc.nekocc.cyanchatroomserver.application.impl.GroupApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.GroupApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.SetMemberRoleRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class SetMemberRoleCommandHandler implements CommandHandler
{
    private final GroupApplicationService group_app_service_ = new GroupApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.SET_MEMBER_ROLE_REQUEST, (UUID operator_id) ->
        {
            ProtocolMessage<SetMemberRoleRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, SetMemberRoleRequest.class);
            SetMemberRoleRequest payload = request_msg.getPayload();
            group_app_service_.setMemberRole(operator_id, payload.group_id(), payload.target_user_id(), payload.new_role());
            CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), true, "角色已更新", "SET_MEMBER_ROLE_SUCCESS");
        });
    }
}