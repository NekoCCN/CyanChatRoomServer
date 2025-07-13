package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.user;

import cc.nekocc.cyanchatroomserver.application.impl.ProfileApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.ProfileApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.ChangePasswordRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class ChangePasswordCommandHandler implements CommandHandler
{
    private final ProfileApplicationService profile_app_service_ = new ProfileApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.CHANGE_PASSWORD_REQUEST, (UUID user_id) ->
        {
            ProtocolMessage<ChangePasswordRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, ChangePasswordRequest.class);
            ChangePasswordRequest payload = request_msg.getPayload();
            profile_app_service_.changePassword(user_id, payload.current_password(), payload.new_password());
            CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), true, "Password changed successfully.", "CHANGE_PASSWORD_SUCCESS");
        });
    }
}