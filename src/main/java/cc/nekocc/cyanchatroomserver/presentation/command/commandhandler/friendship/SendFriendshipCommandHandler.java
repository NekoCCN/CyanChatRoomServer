package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.friendship;

import cc.nekocc.cyanchatroomserver.application.impl.FriendshipApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.FriendshipApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.friendship.SendFriendshipRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class SendFriendshipCommandHandler implements CommandHandler
{
    private final FriendshipApplicationService friendship_application_service_ = new FriendshipApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.SEND_FRIENDSHIP_REQUEST, (UUID operator_id) ->
        {
            ProtocolMessage<SendFriendshipRequest> request_msg =
                    JsonUtil.deserializeProtocolMessage(json_request, SendFriendshipRequest.class);
            SendFriendshipRequest payload = request_msg.getPayload();
            try
            {
                if (!operator_id.equals(payload.sender_id()))
                {
                    CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), false,
                            "发送请求失败：发送者ID不匹配", "SEND_FRIENDSHIP_FAILURE");
                    return;
                }
                friendship_application_service_.sendFriendshipRequest(payload.sender_id(), payload.receiver_id());
                CommandHelper.sendStatusResponse(ctx, payload.client_request_id(),  true,
                        "已发送请求：%s".formatted(payload.receiver_id()), "SEND_FRIENDSHIP_SUCCESS");
            }
            catch (Exception e)
            {
                CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), false,
                        "发送请求失败：%s".formatted(e.getMessage()), "SEND_FRIENDSHIP_FAILURE");
                e.printStackTrace();
            }
        });
    }
}