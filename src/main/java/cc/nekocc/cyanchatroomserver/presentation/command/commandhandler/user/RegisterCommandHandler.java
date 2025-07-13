package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.user;

import cc.nekocc.cyanchatroomserver.application.impl.UserApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.UserApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.presentation.assembler.UserAssembler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.RegisterRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.UserOperatorResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class RegisterCommandHandler implements CommandHandler
{
    private final UserApplicationService user_app_service_ = new UserApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request) throws Exception
    {
        ProtocolMessage<RegisterRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, RegisterRequest.class);
        RegisterRequest payload = request_msg.getPayload();

        try
        {
            User new_user = user_app_service_.register(payload.username(), payload.password(), payload.nick_name(), payload.sign());

            UserOperatorResponse response_payload = new UserOperatorResponse(
                    payload.client_request_id(), true, "Registration successful.",
                    UserAssembler.toDTO(new_user));

            ProtocolMessage<UserOperatorResponse> response_msg =
                    new ProtocolMessage<>(MessageType.REGISTER_RESPONSE, response_payload);

            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
        }
        catch (Exception e)
        {
            UserOperatorResponse response_payload = new UserOperatorResponse(
                    payload.client_request_id(), false, e.getMessage(), null);

            ProtocolMessage<UserOperatorResponse> response_msg =
                    new ProtocolMessage<>(MessageType.REGISTER_RESPONSE, response_payload);

            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
        }
    }
}