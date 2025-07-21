package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.group;

import cc.nekocc.cyanchatroomserver.application.impl.GroupApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.GroupApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.group.GetGroupIdsByUserIdRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.group.HandleJoinRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.GetGroupIdsByUserIdResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.UUID;

public class GetGroupIdsByUserIdCommandHandler implements CommandHandler
{
    private final GroupApplicationService group_app_service_ = new GroupApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.GET_GROUP_IDS_BY_USERID_REQUESTS, (UUID handler_id) ->
        {
            ProtocolMessage<GetGroupIdsByUserIdRequest> request_msg =
                    JsonUtil.deserializeProtocolMessage(json_request, GetGroupIdsByUserIdRequest.class);
            GetGroupIdsByUserIdRequest payload = request_msg.getPayload();
            try
            {
                var group_list = group_app_service_.getGroupIdsByUserId(request_msg.getPayload().user_id());

                GetGroupIdsByUserIdResponse response = new GetGroupIdsByUserIdResponse(
                        request_msg.getPayload().client_request_id(),
                        group_list.toArray(new UUID[0])
                );

                ProtocolMessage<GetGroupIdsByUserIdResponse> response_msg =
                        new ProtocolMessage<>("GET_GROUP_IDS_BY_USERID_RESPONSE", response);

                ctx.channel().writeAndFlush(
                        new TextWebSocketFrame(JsonUtil.serialize(response_msg))
                );
            }
            catch (Exception e)
            {
                GetGroupIdsByUserIdResponse response = new GetGroupIdsByUserIdResponse(
                        request_msg.getPayload().client_request_id(),
                        null
                );

                ProtocolMessage<GetGroupIdsByUserIdResponse> response_msg =
                        new ProtocolMessage<>("GET_GROUP_IDS_BY_USERID_RESPONSE", response);

                ctx.channel().writeAndFlush(
                        new TextWebSocketFrame(JsonUtil.serialize(response_msg))
                );
            }
        });
    }
}
