package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.group;

import cc.nekocc.cyanchatroomserver.application.impl.GroupApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.GroupApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.HandleJoinRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class HandleJoinRequestCommandHandler implements CommandHandler
{
    private final GroupApplicationService group_app_service_ = new GroupApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.HANDLE_JOIN_REQUEST, (UUID handler_id) ->
        {
            ProtocolMessage<HandleJoinRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, HandleJoinRequest.class);
            HandleJoinRequest payload = request_msg.getPayload();
            group_app_service_.handleJoinRequest(payload.group_id(), handler_id, payload.request_id(), payload.approved());
            CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), true, "请求已处理", "HANDLE_JOIN_SUCCESS");
        });
    }
}