package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.friendship;

import cc.nekocc.cyanchatroomserver.application.impl.FriendshipApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.FriendshipApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.domain.model.friendship.Friendship;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.friendship.CheckFriendshipExistsRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.CheckFriendshipExistsResponse;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.FriendshipListResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.UUID;

public class CheckFriendshipExistsCommandHandler implements CommandHandler
{
    private final FriendshipApplicationService friendship_application_service_ = new FriendshipApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.CHECK_FRIENDSHIP_EXISTS_REQUEST, (UUID operator_id) ->
        {
            ProtocolMessage<CheckFriendshipExistsRequest> request_msg =
                    JsonUtil.deserializeProtocolMessage(json_request, CheckFriendshipExistsRequest.class);
            CheckFriendshipExistsRequest payload = request_msg.getPayload();
            try
            {
                boolean exists = friendship_application_service_.checkFriendshipExists(
                        payload.user_id1(), payload.user_id2());
                ProtocolMessage<CheckFriendshipExistsResponse> response_msg =
                        new ProtocolMessage<>(MessageType.CHECK_FRIENDSHIP_EXISTS_RESPONSE,
                                new CheckFriendshipExistsResponse(payload.client_request_id(),
                                        true));
                ctx.channel().writeAndFlush(
                        new ProtocolMessage<>(MessageType.CHECK_FRIENDSHIP_EXISTS_RESPONSE,
                                new CheckFriendshipExistsResponse(payload.client_request_id(), exists)));
            }
            catch (Exception e)
            {
                ProtocolMessage<CheckFriendshipExistsResponse> response_msg =
                        new ProtocolMessage<>(MessageType.CHECK_FRIENDSHIP_EXISTS_RESPONSE,
                                new CheckFriendshipExistsResponse(payload.client_request_id(),
                                        false));
                ctx.channel().writeAndFlush(new ProtocolMessage<>(MessageType.CHECK_FRIENDSHIP_EXISTS_RESPONSE,
                        new CheckFriendshipExistsResponse(payload.client_request_id(), false)));
                e.printStackTrace();
                return;
            }
        });
    }
}