package cc.nekocc.cyanchatroomserver.presentation.handler;

import cc.nekocc.cyanchatroomserver.infrastructure.session.SessionManager;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandlerRegistry;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.ErrorResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.util.concurrent.ExecutorService;

public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame>
{
    private final ExecutorService business_executor_;
    private final CommandHandlerRegistry command_registry_ = CommandHandlerRegistry.getInstance();
    private final SessionManager session_manager_ = SessionManager.getInstance();

    public WebSocketHandler(ExecutorService executor)
    {
        this.business_executor_ = executor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame)
    {
        if (frame instanceof TextWebSocketFrame)
        {
            frame.retain();
            business_executor_.submit(() ->
            {
                try
                {
                    dispatch(ctx, ((TextWebSocketFrame) frame).text());
                } finally
                {
                    frame.release();
                }
            });
        }
    }

    private void dispatch(ChannelHandlerContext ctx, String json_request)
    {
        String type = "UNKNOWN";
        System.out.println("Received request: " + json_request);
        try
        {
            JsonObject json_object = JsonParser.parseString(json_request).getAsJsonObject();
            type = json_object.get("type").getAsString();

            final String finalType = type;

            command_registry_.getHandler(type)
                    .ifPresentOrElse(
                            handler ->
                            {
                                try
                                {
                                    handler.handle(ctx, json_request);
                                } catch (Exception e)
                                {
                                    e.printStackTrace();
                                    sendErrorResponse(ctx, e.getMessage(), finalType);
                                }
                            },
                            () ->
                            {
                                sendErrorResponse(ctx, "Unknown message type: " + finalType, finalType);
                            }
                    );
        } catch (Exception e)
        {
            e.printStackTrace();
            sendErrorResponse(ctx, "Invalid request format: " + e.getMessage(), type);
        }
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, String error_message, String request_type)
    {
        ProtocolMessage<ErrorResponse> error_msg = new ProtocolMessage<>("ERROR_RESPONSE",
                new ErrorResponse(error_message, request_type));
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(error_msg)));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
    {
        session_manager_.logout(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        cause.printStackTrace();
        session_manager_.logout(ctx.channel());
        ctx.close();
    }
}