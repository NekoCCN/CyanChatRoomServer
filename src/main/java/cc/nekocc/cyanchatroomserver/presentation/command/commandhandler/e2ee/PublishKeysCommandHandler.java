package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.e2ee;

import cc.nekocc.cyanchatroomserver.application.impl.KeyManagementServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.KeyManagementService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.e2ee.PublishKeysRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class PublishKeysCommandHandler implements CommandHandler
{
    private final KeyManagementService key_management_service_ = new KeyManagementServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.PUBLISH_KEYS_REQUEST, (UUID user_id) ->
        {
            ProtocolMessage<PublishKeysRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, PublishKeysRequest.class);
            String payload_json = request_msg.getPayload().key_bundle().toString();

            key_management_service_.publishKeys(user_id, payload_json);
            CommandHelper.sendStatusResponse(ctx, request_msg.getPayload().client_request_id(), true,
                    "Keys published successfully.", "PUBLISH_KEYS_SUCCESS");
        });
    }
}