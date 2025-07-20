package cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.file;

import cc.nekocc.cyanchatroomserver.application.impl.FileApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.application.service.FileApplicationService;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.domain.model.file.FileMetadata;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHelper;
import cc.nekocc.cyanchatroomserver.presentation.command.CommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.file.FileUploadRequest;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.FileUploadResponse;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.UUID;

public class RequestUploadCommandHandler implements CommandHandler
{
    private final FileApplicationService file_app_service_ = new FileApplicationServiceImpl();

    @Override
    public void handle(ChannelHandlerContext ctx, String json_request)
    {
        CommandHelper.withAuthenticatedUser(ctx, json_request, MessageType.REQUEST_FILE_UPLOAD, (UUID uploader_id) ->
        {
            ProtocolMessage<FileUploadRequest> msg = JsonUtil.deserializeProtocolMessage(json_request, FileUploadRequest.class);
            FileUploadRequest payload = msg.getPayload();
            try
            {
                FileMetadata metadata = file_app_service_.requestUpload(uploader_id, payload.file_name(), payload.file_size(), payload.expires_in_hours());
                FileUploadResponse response_payload = new FileUploadResponse(metadata.getId(),
                        "/api/files/upload/" + metadata.getId(), payload.client_request_id());
                ProtocolMessage<FileUploadResponse> response_msg = new ProtocolMessage<>("RESPONSE_FILE_UPLOAD", response_payload);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
            }
            catch (Exception e)
            {
                FileUploadResponse response_payload = new FileUploadResponse(null,
                        null, payload.client_request_id());
                ProtocolMessage<FileUploadResponse> response_msg = new ProtocolMessage<>("RESPONSE_FILE_UPLOAD", response_payload);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
            }
        });
    }
}