package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.friendship;

import cc.nekocc.cyanchatroomserver.application.impl.FriendshipApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.FriendshipApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.domain.model.friendship.Friendship;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.friendship.GetFriendshipListRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.friendship.SendFriendshipRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.FriendshipListResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;
import java.util.UUID;

public class GetActiveFriendshipListCommandHandler implements CommandHandler
{
    private final FriendshipApplicationService friendship_application_service_ = new FriendshipApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.GET_ACTIVE_FRIENDSHIP_LIST_REQUEST, (UUID operator_id) ->
        {
            ProtocolMessage<GetFriendshipListRequest> request_msg =
                    JsonUtil.deserializeProtocolMessage(json_request, GetFriendshipListRequest.class);
            GetFriendshipListRequest payload = request_msg.getPayload();
            try
            {
                List<Friendship> friends = friendship_application_service_.getActiveFriendshipList(payload.user_id());
                ProtocolMessage<FriendshipListResponse> response_msg =
                        new ProtocolMessage<>(MessageType.GET_ACTIVE_FRIENDSHIP_LIST_RESPONSE,
                                new FriendshipListResponse(payload.client_request_id(),
                                        true, friends));
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
            }
            catch (Exception e)
            {
                ProtocolMessage<FriendshipListResponse> response_msg =
                        new ProtocolMessage<>(MessageType.GET_ACTIVE_FRIENDSHIP_LIST_RESPONSE,
                                new FriendshipListResponse(payload.client_request_id(),
                                        false, null));
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
                e.printStackTrace();
                return;
            }
        });
    }
}