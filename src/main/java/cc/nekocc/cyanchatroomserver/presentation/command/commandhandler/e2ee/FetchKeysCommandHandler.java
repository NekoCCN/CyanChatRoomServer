package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.e2ee;

import cc.nekocc.cyanchatroomserver.application.impl.KeyManagementServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.KeyManagementService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.FetchKeysRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.FetchKeysResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.UUID;

public class FetchKeysCommandHandler implements CommandHandler
{
    private final KeyManagementService key_management_service_ = new KeyManagementServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.FETCH_KEYS_REQUEST, (UUID requestor_id) ->
        {
            ProtocolMessage<FetchKeysRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, FetchKeysRequest.class);
            UUID target_user_id = request_msg.getPayload().user_id();

            key_management_service_.fetchKeys(target_user_id).ifPresentOrElse(
                    bundle_json ->
                    {
                        FetchKeysResponse response_payload = new FetchKeysResponse(request_msg.getPayload().client_request_id(), true, target_user_id, bundle_json);
                        ProtocolMessage<FetchKeysResponse> response_msg = new ProtocolMessage<>("FETCH_KEYS_RESPONSE", response_payload);
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
                    },
                    () ->
                    {
                        FetchKeysResponse response_payload = new FetchKeysResponse(request_msg.getPayload().client_request_id(), false, target_user_id, null);
                        ProtocolMessage<FetchKeysResponse> response_msg = new ProtocolMessage<>("FETCH_KEYS_RESPONSE", response_payload);
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
                    }
            );
        });
    }
}