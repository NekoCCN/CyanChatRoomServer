package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler;

import cc.nekocc.cyanchatroomserver.application.impl.ChatApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.ChatApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.ChatMessageRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class ChatMessageCommandHandler implements CommandHandler
{
    private final ChatApplicationService chat_app_service_ = new ChatApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.CHAT_MESSAGE, (UUID user_id) ->
        {
            ProtocolMessage<ChatMessageRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, ChatMessageRequest.class);
            ChatMessageRequest payload = request_msg.getPayload();
            chat_app_service_.sendMessage(user_id, payload.recipient_type(), payload.recipient_id(), payload.content_type(), payload.is_encrypted(), payload.content());
        });
    }
}