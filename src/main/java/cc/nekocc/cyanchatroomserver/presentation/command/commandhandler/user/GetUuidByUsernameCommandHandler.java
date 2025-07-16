package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.user;

import cc.nekocc.cyanchatroomserver.application.impl.UserApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.UserApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.user.GetUserDetailsRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.user.GetUuidByUsernameRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.GetUserDetailsResponse;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.GetUuidByUsernameResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.awt.desktop.UserSessionListener;
import java.util.Optional;
import java.util.UUID;

public class GetUuidByUsernameCommandHandler
        implements cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler
{
    private final UserApplicationService user_app_service_ = new UserApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request) throws Exception
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.GET_USER_DETAILS_REQUEST, (UUID requestor_id) ->
        {
            ProtocolMessage<GetUserDetailsRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, GetUserDetailsRequest.class);
            GetUserDetailsRequest payload = request_msg.getPayload();
            Optional<User> user_optional = user_app_service_.getUserById(payload.user_id());

            if (user_optional.isPresent())
            {
                GetUuidByUsernameResponse user_response = new GetUuidByUsernameResponse(
                        payload.client_request_id(), true, user_optional.get().getId());
                ProtocolMessage<GetUuidByUsernameResponse> response_msg = new ProtocolMessage<>("GET_UUID_BY_USERNAME_RESPONSE", user_response);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
            } else
            {
                GetUuidByUsernameResponse user_response = new GetUuidByUsernameResponse(
                        payload.client_request_id(), false, null);
                ProtocolMessage<GetUuidByUsernameResponse> response_msg = new ProtocolMessage<>("GET_UUID_BY_USERNAME_RESPONSE", user_response);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
            }
        });
    }
}
