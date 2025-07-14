package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.user;

import cc.nekocc.cyanchatroomserver.application.impl.ProfileApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.ProfileApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.user.UpdateProfileRequest;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import java.util.UUID;

public class UpdateProfileCommandHandler implements CommandHandler
{
    private final ProfileApplicationService profile_app_service_ = new ProfileApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.UPDATE_PROFILE_REQUEST, (UUID user_id) ->
        {
            ProtocolMessage<UpdateProfileRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, UpdateProfileRequest.class);
            UpdateProfileRequest payload = request_msg.getPayload();
            profile_app_service_.updateProfile(user_id, payload.nick_name(), payload.signature(), payload.avatar_file_id())
                    .ifPresentOrElse(
                            updatedUser -> CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), true, "Profile updated successfully.", "UPDATE_PROFILE_SUCCESS"),
                            () -> CommandHelper.sendStatusResponse(ctx, payload.client_request_id(), false, "Failed to update profile, user not found.", "UPDATE_PROFILE_REQUEST")
                    );
        });
    }
}