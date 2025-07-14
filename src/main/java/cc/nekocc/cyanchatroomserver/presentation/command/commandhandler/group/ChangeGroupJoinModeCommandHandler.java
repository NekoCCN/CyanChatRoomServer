package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.group;

import cc.nekocc.cyanchatroomserver.application.impl.GroupApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.GroupApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.group.ChangeGroupJoinModeRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class ChangeGroupJoinModeCommandHandler implements CommandHandler
{
    private final GroupApplicationService group_app_service_ = new GroupApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.CHANGE_GROUP_JOIN_MODE_REQUEST, (UUID operator_id) ->
        {
            ProtocolMessage<ChangeGroupJoinModeRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, ChangeGroupJoinModeRequest.class);
            ChangeGroupJoinModeRequest payload = request_msg.getPayload();
            try
            {
                group_app_service_.changeGroupJoinMode(operator_id, payload.group_id(), payload.new_mode());
                CommandHelper.sendStatusResponse(ctx, payload.client_request_id(),  true,
                        "加群方式已更新", "");
            }
            catch (Exception e)
            {
                CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), false,
                        "无法更改加群方式: " + e.getMessage(), "CHANGE_GROUP_JOIN_MODE_ERROR");
            }
        });
    }
}