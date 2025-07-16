package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.group;

import cc.nekocc.cyanchatroomserver.application.impl.GroupApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.GroupApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.group.RemoveMemberRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class RemoveMemberCommandHandler implements CommandHandler
{
    private final GroupApplicationService group_app_service_ = new GroupApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.REMOVE_MEMBER_REQUEST, (UUID operator_id) ->
        {
            ProtocolMessage<RemoveMemberRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, RemoveMemberRequest.class);
            RemoveMemberRequest payload = request_msg.getPayload();
            try
            {
                group_app_service_.removeMember(operator_id, payload.group_id(), payload.target_user_id());
                CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), true, "成员已移除", "REMOVE_MEMBER_SUCCESS");
            }
            catch (Exception e)
            {
                CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), false,
                        "无法移除成员: " + e.getMessage(), "REMOVE_MEMBER_ERROR");
            }
        });
    }
}
