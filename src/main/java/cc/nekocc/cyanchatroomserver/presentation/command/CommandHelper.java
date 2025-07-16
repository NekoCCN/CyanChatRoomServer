package cc.nekocc.cyanchatroomserver.presentation.command;

import cc.nekocc.cyanchatroomserver.infrastructure.session.SessionManager;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.ErrorResponse;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.StatusResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.UUID;

public final class CommandHelper
{
    private static final SessionManager session_manager_ = SessionManager.getInstance();

    private CommandHelper()
    {  }

    public static void withAuthenticatedUser(ChannelHandlerContext ctx, String json_request,
                                             String request_type, ThrowingConsumer<UUID> action)
    {
        UUID user_id = session_manager_.getUserId(ctx.channel());
        if (user_id == null)
        {
            sendErrorResponse(ctx, "Not logged in.", request_type);
            return;
        }
        try
        {
            action.accept(user_id);
        } catch (Exception e)
        {
            e.printStackTrace();
            sendErrorResponse(ctx, e.getMessage(), request_type);
        }
    }

    public static void sendErrorResponse(ChannelHandlerContext ctx, String error_message, String request_type)
    {
        ProtocolMessage<ErrorResponse> error_msg = new ProtocolMessage<>("ERROR_RESPONSE",
                new ErrorResponse(error_message, request_type));

        System.out.println("Sending error response: " + error_message);

        ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(error_msg)));
    }

    public static void sendStatusResponse(ChannelHandlerContext ctx, UUID client_request_id, boolean status,
                                          String message, String response_type)
    {
        ProtocolMessage<StatusResponse> response_msg = new ProtocolMessage<>(response_type,
                new StatusResponse(client_request_id, status, message, response_type));

        System.out.println("Sending status response: " + message);

        ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
    }
}