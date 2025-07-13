package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.group;

import cc.nekocc.cyanchatroomserver.application.impl.GroupApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.GroupApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.LeaveGroupRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class LeaveGroupCommandHandler implements CommandHandler
{
    private final GroupApplicationService group_app_service_ = new GroupApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.LEAVE_GROUP_REQUEST, (UUID user_id) ->
        {
            ProtocolMessage<LeaveGroupRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, LeaveGroupRequest.class);
            LeaveGroupRequest payload = request_msg.getPayload();
            group_app_service_.leaveGroup(user_id, payload.group_id());
            CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), true, "已退出群组", "LEAVE_GROUP_SUCCESS");
        });
    }
}
