package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.user;

import cc.nekocc.cyanchatroomserver.application.impl.ProfileApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.ProfileApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.ChangeUsernameRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class ChangeUsernameCommandHandler implements CommandHandler
{
    private final ProfileApplicationService profile_app_service_ = new ProfileApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.CHANGE_USERNAME_REQUEST, (UUID user_id) ->
        {
            ProtocolMessage<ChangeUsernameRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, ChangeUsernameRequest.class);
            ChangeUsernameRequest payload = request_msg.getPayload();
            profile_app_service_.changeUsername(user_id, payload.current_password(), payload.new_user_name());
            CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), true, "Username changed successfully.", "CHANGE_USERNAME_SUCCESS");
        });
    }
}
