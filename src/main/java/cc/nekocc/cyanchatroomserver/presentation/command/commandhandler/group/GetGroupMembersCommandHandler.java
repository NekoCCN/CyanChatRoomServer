package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.group;

import cc.nekocc.cyanchatroomserver.application.impl.GroupApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.GroupApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.domain.model.group.GroupMember;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.group.GetGroupMembersRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.GetGroupMembersResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.UUID;

public class GetGroupMembersCommandHandler implements CommandHandler
{
    private final GroupApplicationService group_app_service_ = new GroupApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.GET_GROUP_MEMBERS_REQUEST, (UUID handler_id) ->
        {
            ProtocolMessage<GetGroupMembersRequest> request_msg =
                    JsonUtil.deserializeProtocolMessage(json_request, GetGroupMembersRequest.class);
            GetGroupMembersRequest payload = request_msg.getPayload();

            try
            {
                var group_list = group_app_service_.getGroupMembers(request_msg.getPayload().group_id());

                GetGroupMembersResponse response = new GetGroupMembersResponse(
                        request_msg.getPayload().client_request_id(),
                        group_list.toArray(new GroupMember[0])
                );

                ProtocolMessage<GetGroupMembersResponse> response_msg =
                        new ProtocolMessage<>(MessageType.GET_GROUP_MEMBERS_RESPONSE, response);

                ctx.channel().writeAndFlush(
                        new TextWebSocketFrame(JsonUtil.serialize(response_msg))
                );
            }
            catch (Exception e)
            {
                GetGroupMembersResponse response = new GetGroupMembersResponse(
                        request_msg.getPayload().client_request_id(),
                        null
                );

                ProtocolMessage<GetGroupMembersResponse> response_msg =
                        new ProtocolMessage<>(MessageType.GET_GROUP_MEMBERS_RESPONSE, response);

                ctx.channel().writeAndFlush(
                        new TextWebSocketFrame(JsonUtil.serialize(response_msg))
                );
            }
        });
    }
}
