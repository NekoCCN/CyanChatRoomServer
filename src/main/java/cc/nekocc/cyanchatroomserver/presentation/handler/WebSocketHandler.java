package cc.nekocc.cyanchatroomserver.presentation.handler;

import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.domain.model.file.FileMetadata;
import cc.nekocc.cyanchatroomserver.domain.model.group.Group;
import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.application.impl.*;
import cc.nekocc.cyanchatroomserver.infrastructure.session.SessionManager;
import cc.nekocc.cyanchatroomserver.presentation.assembler.GroupAssembler;
import cc.nekocc.cyanchatroomserver.presentation.assembler.UserAssembler;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.*;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.*;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.application.service.*;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame>
{
    private final ExecutorService business_executor_;
    private final UserApplicationService user_app_service_ = new UserApplicationServiceImpl();
    private final ChatApplicationService chat_app_service_ = new ChatApplicationServiceImpl();
    private final GroupApplicationService group_app_service_ = new GroupApplicationServiceImpl();
    private final FileApplicationService file_app_service_ = new FileApplicationServiceImpl();
    private final ProfileApplicationService profile_app_service_ = new ProfileApplicationServiceImpl();
    private final KeyManagementService key_management_service_ = new KeyManagementServiceImpl();
    private final SessionManager session_manager_ = SessionManager.getInstance();

    public WebSocketHandler(ExecutorService executor)
    {
        this.business_executor_ = executor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame)
    {
        frame.retain();

        business_executor_.submit(() -> {
            try
            {
                dispatch(ctx, ((TextWebSocketFrame) frame).text());
            }
            finally
            {
                frame.release();
            }
        });
    }

    private void dispatch(ChannelHandlerContext ctx, String json_request)
    {
        try
        {
            JsonObject jsonObject = JsonParser.parseString(json_request).getAsJsonObject();
            String type = jsonObject.get("type").getAsString();

            switch (type)
            {
                case MessageType.LOGIN_REQUEST:
                    handleLogin(ctx, json_request);
                    break;
                case MessageType.CHAT_MESSAGE:
                    handleChatMessage(ctx, json_request);
                    break;
                case MessageType.CREATE_GROUP_REQUEST:
                    handleCreateGroup(ctx, json_request);
                    break;
                case MessageType.REQUEST_FILE_UPLOAD:
                    handleRequestUpload(ctx, json_request);
                    break;
                case MessageType.UPDATE_PROFILE_REQUEST:
                    handleUpdateProfile(ctx, json_request);
                    break;
                case MessageType.PUBLISH_KEYS_REQUEST:
                    handlePublishKeys(ctx, json_request);
                    break;
                case MessageType.FETCH_KEYS_REQUEST:
                    handleFetchKeys(ctx, json_request);
                    break;
                default:
                    sendErrorResponse(ctx, "Unknown message type: " + type, type);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            sendErrorResponse(ctx, "Invalid request format: " + e.getMessage(), "UNKNOWN");
        }
    }

    private void handleLogin(ChannelHandlerContext ctx, String json_request)
    {
        ProtocolMessage<LoginRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, LoginRequest.class);
        Optional<User> user_optional = user_app_service_.login(request_msg.getPayload().username(), request_msg.getPayload().password());

        ProtocolMessage<UserLoginResponse> responseMsg = new ProtocolMessage<>();
        user_optional.ifPresentOrElse(user ->
        {
            session_manager_.login(user.getId(), ctx.channel());
            UserLoginResponse.UserDTO userDTO = UserAssembler.toDTO(user);
            responseMsg.setType("LOGIN_SUCCESS");
            responseMsg.setPayload(new UserLoginResponse(true, "Login successful!", userDTO));
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(responseMsg)));
            business_executor_.submit(() -> user_app_service_.processPostLoginTasks(user, ctx.channel()));
        }, () ->
        {
            responseMsg.setType("LOGIN_FAILED");
            responseMsg.setPayload(new UserLoginResponse(false, "Invalid credentials.", null));
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(responseMsg)));
        });
    }

    private void handleChatMessage(ChannelHandlerContext ctx, String json_request)
    {
        UUID sender_id = session_manager_.getUserId(ctx.channel());
        if (sender_id == null)
        {
            sendErrorResponse(ctx, "Not logged in.", "CHAT_MESSAGE");
            return;
        }

        ProtocolMessage<ChatMessageRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, ChatMessageRequest.class);
        ChatMessageRequest payload = request_msg.getPayload();
        chat_app_service_.sendMessage(sender_id, payload.recipient_type(), payload.recipient_id(), payload.content_type(), payload.is_encrypted(), payload.content());
    }

    private void handleCreateGroup(ChannelHandlerContext ctx, String json_request)
    {
        UUID creator_id = session_manager_.getUserId(ctx.channel());
        if (creator_id == null)
        {
            sendErrorResponse(ctx, "Not logged in.", "CREATE_GROUP_REQUEST");
            return;
        }
        try
        {
            ProtocolMessage<CreateGroupRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, CreateGroupRequest.class);
            CreateGroupRequest payload = request_msg.getPayload();
            Group new_group = group_app_service_.createGroup(creator_id, payload.group_name(), payload.member_ids());

            // 向创建者发送成功响应
            GroupResponse groupDTO = GroupAssembler.toDTO(new_group, payload.member_ids());
            ProtocolMessage<GroupResponse> response_msg = new ProtocolMessage<>("CREATE_GROUP_SUCCESS", groupDTO);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));

            // TODO: 向所有在线的初始成员推送新群通知
        } catch (Exception e)
        {
            e.printStackTrace();
            sendErrorResponse(ctx, "Failed to create group: " + e.getMessage(), "CREATE_GROUP_REQUEST");
        }
    }

    private void handleRequestUpload(ChannelHandlerContext ctx, String json_request)
    {
        UUID uploader_id = session_manager_.getUserId(ctx.channel());
        if (uploader_id == null)
        {
            sendErrorResponse(ctx, "Not logged in.", "REQUEST_FILE_UPLOAD");
            return;
        }

        ProtocolMessage<FileUploadRequest> msg = JsonUtil.deserializeProtocolMessage(json_request, FileUploadRequest.class);
        FileUploadRequest payload = msg.getPayload();

        FileMetadata metadata = file_app_service_.requestUpload(uploader_id, payload.file_name(), payload.file_size(), payload.expires_in_hours());

        FileUploadResponse response_payload = new FileUploadResponse(metadata.getId(), "/api/files/upload/" + metadata.getId());

        ProtocolMessage<FileUploadResponse> response_msg = new ProtocolMessage<>("RESPONSE_FILE_UPLOAD", response_payload);

        ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
    }

    private void handleUpdateProfile(ChannelHandlerContext ctx, String json_request)
    {
        UUID user_id = session_manager_.getUserId(ctx.channel());
        if (user_id == null)
        {
            sendErrorResponse(ctx, "Not logged in.", "UPDATE_PROFILE_REQUEST");
            return;
        }

        ProtocolMessage<UpdateProfileRequest> requestMsg = JsonUtil.deserializeProtocolMessage(json_request, UpdateProfileRequest.class);
        UpdateProfileRequest payload = requestMsg.getPayload();
        profile_app_service_.updateProfile(user_id, payload.nickname(), payload.signature(), payload.avatar_file_id())
                .ifPresentOrElse(updatedUser ->
                        {
                            ProtocolMessage<String> successMsg = new ProtocolMessage<>("UPDATE_PROFILE_SUCCESS", "Profile updated successfully.");
                            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(successMsg)));
                        },
                        () -> sendErrorResponse(ctx, "Failed to update profile.", "UPDATE_PROFILE_REQUEST")
                );
    }

    private void handlePublishKeys(ChannelHandlerContext ctx, String json_request)
    {
        UUID user_id = session_manager_.getUserId(ctx.channel());
        if (user_id == null)
        {
            sendErrorResponse(ctx, "Not logged in.", "PUBLISH_KEYS_REQUEST");
            return;
        }

        JsonObject json_object = JsonParser.parseString(json_request).getAsJsonObject();
        String payload_json = json_object.get("payload").toString();
        key_management_service_.publishKeys(user_id, payload_json);

        ProtocolMessage<String> success_msg = new ProtocolMessage<>("PUBLISH_KEYS_SUCCESS", "Keys published successfully.");
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(success_msg)));
    }

    private void handleFetchKeys(ChannelHandlerContext ctx, String json_request)
    {
        ProtocolMessage<FetchKeysRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, FetchKeysRequest.class);
        UUID target_user_id = request_msg.getPayload().user_id();

        Optional<String> key = key_management_service_.fetchKeys(target_user_id);

        if (key.isPresent())
        {
            FetchKeysResponse response_payload = new FetchKeysResponse(true, target_user_id, key.get());
            ProtocolMessage<FetchKeysResponse> response_msg = new ProtocolMessage<>("FETCH_KEYS_RESPONSE", response_payload);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
        }
        else
        {
            FetchKeysResponse response_payload = new FetchKeysResponse(false, target_user_id, "");
            ProtocolMessage<FetchKeysResponse> response_msg = new ProtocolMessage<>("FETCH_KEYS_RESPONSE", response_payload);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
        }
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

    private void sendErrorResponse(ChannelHandlerContext ctx, String error_message, String request_type)
    {
        ProtocolMessage<ErrorResponse> error_msg = new ProtocolMessage<>("ERROR_RESPONSE", new ErrorResponse(error_message, request_type));
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(error_msg)));
    }
}