package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.group;

import cc.nekocc.cyanchatroomserver.application.impl.GroupApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.GroupApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.domain.model.group.Group;
import cc.nekocc.cyanchatroomserver.presentation.assembler.GroupAssembler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.group.CreateGroupRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.GroupResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.UUID;

public class CreateGroupCommandHandler implements CommandHandler
{
    private final GroupApplicationService group_app_service_ = new GroupApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.CREATE_GROUP_REQUEST, (UUID creator_id) ->
        {
            ProtocolMessage<CreateGroupRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, CreateGroupRequest.class);
            CreateGroupRequest payload = request_msg.getPayload();

            Group new_group = group_app_service_.createGroup(creator_id, payload.group_name(), payload.member_ids());

            GroupResponse group_dto = GroupAssembler.toDTO(payload.client_request_id(), true, new_group, payload.member_ids());

            ProtocolMessage<GroupResponse> response_msg = new ProtocolMessage<>(MessageType.CREATE_GROUP_RESPONSE, group_dto);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
        });
    }
}