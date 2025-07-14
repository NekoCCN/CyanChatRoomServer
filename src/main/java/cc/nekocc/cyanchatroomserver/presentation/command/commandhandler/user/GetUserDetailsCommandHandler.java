package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.user;

import cc.nekocc.cyanchatroomserver.application.impl.KeyManagementServiceImpl;
import cc.nekocc.cyanchatroomserver.application.impl.UserApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.KeyManagementService;
import cc.nekocc.cyanchatroomserver.application.service.UserApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.infrastructure.session.SessionManager;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.user.GetUserDetailsRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.GetUserDetailsResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.Optional;
import java.util.UUID;

public class GetUserDetailsCommandHandler implements CommandHandler
{
    private final UserApplicationService user_app_service_ = new UserApplicationServiceImpl();
    private final KeyManagementService key_management_service_ = new KeyManagementServiceImpl();
    private final SessionManager session_manager_ = SessionManager.getInstance();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.GET_USER_DETAILS_REQUEST, (UUID requestor_id) ->
        {
            ProtocolMessage<GetUserDetailsRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, GetUserDetailsRequest.class);
            GetUserDetailsRequest payload = request_msg.getPayload();
            Optional<User> user_optional = user_app_service_.getUserById(payload.user_id());

            if (user_optional.isPresent())
            {
                User user = user_optional.get();
                boolean is_online = session_manager_.getChannel(user.getId()) != null;
                boolean is_key_enabled = key_management_service_.fetchKeys(user.getId()).isPresent();

                GetUserDetailsResponse user_response = new GetUserDetailsResponse(
                        payload.client_request_id(), user.getUsername(), user.getNickname(), user.getAvatarUrl(),
                        user.getSignature(), user.getStatus(), is_online, is_key_enabled);
                ProtocolMessage<GetUserDetailsResponse> response_msg = new ProtocolMessage<>("GET_USER_DETAILS_SUCCESS", user_response);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
            } else
            {
                CommandHelper.sendErrorResponse(ctx, "User not found.", MessageType.GET_USER_DETAILS_REQUEST);
            }
        });
    }
}
