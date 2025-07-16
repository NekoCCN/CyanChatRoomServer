package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.group;

import cc.nekocc.cyanchatroomserver.application.impl.GroupApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.GroupApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.group.JoinGroupRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class JoinGroupCommandHandler implements CommandHandler
{
    private final GroupApplicationService group_app_service_ = new GroupApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.JOIN_GROUP_REQUEST, (UUID user_id) ->
                {
                    ProtocolMessage<JoinGroupRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, JoinGroupRequest.class);
                    JoinGroupRequest payload = request_msg.getPayload();
                    try
                    {
                        group_app_service_.requestToJoinGroup(user_id, payload.group_id(), payload.request_message());
                        CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), true, "请求已发送", "JOIN_GROUP_SUCCESS");
                    }
                    catch (Exception e)
                    {
                        CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), false, "无法加入群组: " + e.getMessage(), "JOIN_GROUP_ERROR");
                    }
                });
    }
}