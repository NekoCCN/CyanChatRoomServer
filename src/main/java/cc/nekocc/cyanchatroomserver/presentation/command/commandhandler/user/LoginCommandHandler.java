package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.user;

import cc.nekocc.cyanchatroomserver.application.impl.UserApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.UserApplicationService;
import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.infrastructure.session.SessionManager;
import cc.nekocc.cyanchatroomserver.presentation.assembler.UserAssembler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.user.LoginRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.UserOperatorResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.Optional;

public class LoginCommandHandler implements CommandHandler
{
    private final UserApplicationService user_app_service_ = new UserApplicationServiceImpl();
    private final SessionManager session_manager_ = SessionManager.getInstance();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        ProtocolMessage<LoginRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request,
                LoginRequest.class);
        Optional<User> user_optional = user_app_service_.login(request_msg.getPayload().username(),
                request_msg.getPayload().password());

        ProtocolMessage<UserOperatorResponse> response_msg = new ProtocolMessage<>();
        user_optional.ifPresentOrElse(user ->
        {
            session_manager_.login(user.getId(), ctx.channel());

            UserOperatorResponse.UserDTO user_dto = UserAssembler.toDTO(user);

            response_msg.setType("LOGIN_SUCCESS");
            response_msg.setPayload(new UserOperatorResponse(request_msg.getPayload().client_request_id(), true, "Login successful!", user_dto));

            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));

            user_app_service_.processPostLoginTasks(user, ctx.channel());
        }, () ->
        {
            response_msg.setType("LOGIN_FAILED");

            response_msg.setPayload(new UserOperatorResponse(request_msg.getPayload().client_request_id(),
                    false, "认证失败。", null));

            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
        });
    }
}
