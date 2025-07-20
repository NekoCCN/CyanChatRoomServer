package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.friendship;

import cc.nekocc.cyanchatroomserver.application.impl.FriendshipApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.FriendshipApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.friendship.DeleteFriendshipRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class DeleteFriendshipCommandHandler implements CommandHandler
{
    private final FriendshipApplicationService friendship_application_service_
            = new FriendshipApplicationServiceImpl();


    @Override
    public void handle(ChannelHandlerContext ctx, String json_request) throws Exception
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.CHECK_FRIENDSHIP_EXISTS_REQUEST, (UUID operator_id) ->
        {
            ProtocolMessage<DeleteFriendshipRequest> request_msg =
                    JsonUtil.deserializeProtocolMessage(json_request, DeleteFriendshipRequest.class);
            DeleteFriendshipRequest payload = request_msg.getPayload();

            try
            {
                friendship_application_service_.deleteFriendship(payload.friendship_id(), operator_id);
                CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), true,
                        "请求状态变更：删除好友关系 - %s".formatted(payload.friendship_id()), "DELETE_FRIENDSHIP_SUCCESS");
            }
            catch (Exception e)
            {
                CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), false,
                        "请求状态变更：删除好友关系失败 - %s".formatted(e.getMessage()), "DELETE_FRIENDSHIP_FAILED");
                e.printStackTrace();
            }
        });
    }
}
