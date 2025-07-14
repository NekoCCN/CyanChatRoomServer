package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.friendship;

import cc.nekocc.cyanchatroomserver.application.impl.FriendshipApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.FriendshipApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.friendship.AcceptFriendshipRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class AcceptFriendshipCommandHandler implements CommandHandler
{
    private final FriendshipApplicationService friendship_application_service_ = new FriendshipApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.ACCEPT_FRIENDSHIP_REQUEST, (UUID operator_id) ->
        {
            ProtocolMessage<AcceptFriendshipRequest> request_msg =
                    JsonUtil.deserializeProtocolMessage(json_request, AcceptFriendshipRequest.class);
            AcceptFriendshipRequest payload = request_msg.getPayload();
            try
            {
                friendship_application_service_.acceptFriendshipRequest(operator_id, request_msg.getPayload().request_id());
                CommandHelper.sendStatusResponse(ctx, payload.client_request_id(),  true,
                        "请求状态变更：同意 - %s".formatted(payload.request_id()), "ACCEPT_FRIENDSHIP_SUCCESS");
            }
            catch (Exception e)
            {
                CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), false,
                        "请求状态变更：同意失败 - %s".formatted(e.getMessage()), "ACCEPT_FRIENDSHIP_FAILED");
                e.printStackTrace();
            }
        });
    }
}