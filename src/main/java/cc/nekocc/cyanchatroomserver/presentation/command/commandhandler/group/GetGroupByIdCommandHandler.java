package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.group;

import cc.nekocc.cyanchatroomserver.application.impl.GroupApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.GroupApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.domain.model.group.Group;
import cc.nekocc.cyanchatroomserver.presentation.assembler.GroupAssembler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.group.GetGroupByIdRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.GroupResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.UUID;

public class GetGroupByIdCommandHandler implements CommandHandler
{
    private final GroupApplicationService group_app_service_ = new GroupApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.GET_GROUP_BY_ID_REQUEST, (UUID creator_id) ->
        {
            ProtocolMessage<GetGroupByIdRequest> request_msg =
                    JsonUtil.deserializeProtocolMessage(json_request, GetGroupByIdRequest.class);
            GetGroupByIdRequest payload = request_msg.getPayload();

            try
            {
                Group group = group_app_service_.getGroupById(payload.group_id());

                GroupResponse group_dto = GroupAssembler.toDTO(payload.client_request_id(),
                        true, group, null);

                ProtocolMessage<GroupResponse> response_msg = new ProtocolMessage<>(MessageType.GET_GROUP_BY_ID_RESPONSE, group_dto);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
            }
            catch (Exception e)
            {
                GroupResponse group_dto = new GroupResponse(payload.client_request_id(), false, null, null, null, null);

                ProtocolMessage<GroupResponse> response_msg = new ProtocolMessage<>(MessageType.GET_GROUP_BY_ID_RESPONSE, group_dto);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
            }
        });
    }
}
