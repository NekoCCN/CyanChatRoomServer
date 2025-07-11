package cc.nekocc.cyanchatroomserver.presentation.handler;

import cc.nekocc.cyanchatroomserver.infrastructure.config.MyBatisUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cc.nekocc.cyanchatroomserver.application.impl.*;
import cc.nekocc.cyanchatroomserver.application.service.*;
import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.domain.model.file.FileMetadata;
import cc.nekocc.cyanchatroomserver.domain.model.group.Group;
import cc.nekocc.cyanchatroomserver.domain.model.user.User;
import cc.nekocc.cyanchatroomserver.infrastructure.session.SessionManager;
import cc.nekocc.cyanchatroomserver.presentation.assembler.GroupAssembler;
import cc.nekocc.cyanchatroomserver.presentation.assembler.UserAssembler;
import cc.nekocc.cyanchatroomserver.presentation.dto.request.*;
import cc.nekocc.cyanchatroomserver.presentation.dto.response.*;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

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
        try
        {
            JsonObject json_object = JsonParser.parseString(json_request).getAsJsonObject();
            String type = json_object.get("type").getAsString();

            switch (type)
            {
                case MessageType.REGISTER_REQUEST:
                    handleRegister(ctx, json_request);
                    break;
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
                case MessageType.CHANGE_USERNAME_REQUEST:
                    handleChangeUsername(ctx, json_request);
                    break;
                case MessageType.CHANGE_PASSWORD_REQUEST:
                    handleChangePassword(ctx, json_request);
                    break;
                case MessageType.PUBLISH_KEYS_REQUEST:
                    handlePublishKeys(ctx, json_request);
                    break;
                case MessageType.FETCH_KEYS_REQUEST:
                    handleFetchKeys(ctx, json_request);
                    break;
                case MessageType.JOIN_GROUP_REQUEST:
                    handleJoinGroup(ctx, json_request);
                    break;
                case MessageType.HANDLE_JOIN_REQUEST:
                    handleProcessJoinRequest(ctx, json_request);
                    break;
                case MessageType.LEAVE_GROUP_REQUEST:
                    handleLeaveGroup(ctx, json_request);
                    break;
                case MessageType.REMOVE_MEMBER_REQUEST:
                    handleRemoveMember(ctx, json_request);
                    break;
                case MessageType.SET_MEMBER_ROLE_REQUEST:
                    handleSetMemberRole(ctx, json_request);
                    break;
                case MessageType.CHANGE_GROUP_JOIN_MODE_REQUEST:
                    handleChangeGroupJoinMode(ctx, json_request);
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

    private void handleRegister(ChannelHandlerContext ctx, String json_request)
    {
        try
        {
            ProtocolMessage<RegisterRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, RegisterRequest.class);
            RegisterRequest payload = request_msg.getPayload();
            User new_user = user_app_service_.register(payload.user_name(), payload.password(), payload.nick_name());
            UserLoginResponse.UserDTO user_dto = UserAssembler.toDTO(new_user);
            ProtocolMessage<UserLoginResponse.UserDTO> response_msg = new ProtocolMessage<>("REGISTER_SUCCESS", user_dto);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
        } catch (Exception e)
        {
            e.printStackTrace();
            sendErrorResponse(ctx, e.getMessage(), MessageType.REGISTER_REQUEST);
        }
    }

    private void handleLogin(ChannelHandlerContext ctx, String json_request)
    {
        ProtocolMessage<LoginRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, LoginRequest.class);
        Optional<User> user_optional = user_app_service_.login(request_msg.getPayload().username(), request_msg.getPayload().password());
        ProtocolMessage<UserLoginResponse> response_msg = new ProtocolMessage<>();
        user_optional.ifPresentOrElse(user ->
        {
            session_manager_.login(user.getId(), ctx.channel());
            UserLoginResponse.UserDTO user_dto = UserAssembler.toDTO(user);
            response_msg.setType("LOGIN_SUCCESS");
            response_msg.setPayload(new UserLoginResponse(true, "Login successful!", user_dto));
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
            business_executor_.submit(() -> user_app_service_.processPostLoginTasks(user, ctx.channel()));
        }, () ->
        {
            response_msg.setType("LOGIN_FAILED");
            response_msg.setPayload(new UserLoginResponse(false, "Invalid credentials.", null));
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
        });
    }

    private void handleChatMessage(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (user_id) ->
        {
            ProtocolMessage<ChatMessageRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, ChatMessageRequest.class);
            ChatMessageRequest payload = request_msg.getPayload();
            chat_app_service_.sendMessage(user_id, payload.recipient_type(), payload.recipient_id(), payload.content_type(), payload.is_encrypted(), payload.content());
        });
    }

    private void handleCreateGroup(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (creator_id) ->
        {
            ProtocolMessage<CreateGroupRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, CreateGroupRequest.class);
            CreateGroupRequest payload = request_msg.getPayload();
            Group new_group = group_app_service_.createGroup(creator_id, payload.group_name(), payload.member_ids());
            GroupResponse group_dto = GroupAssembler.toDTO(new_group, payload.member_ids());
            ProtocolMessage<GroupResponse> response_msg = new ProtocolMessage<>("CREATE_GROUP_SUCCESS", group_dto);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
            // TODO: 向所有在线的初始成员推送新群通知
        });
    }

    private void handleRequestUpload(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (uploader_id) ->
        {
            ProtocolMessage<FileUploadRequest> msg = JsonUtil.deserializeProtocolMessage(json_request, FileUploadRequest.class);
            FileUploadRequest payload = msg.getPayload();
            FileMetadata metadata = file_app_service_.requestUpload(uploader_id, payload.file_name(), payload.file_size(), payload.expires_in_hours());
            FileUploadResponse response_payload = new FileUploadResponse(metadata.getId(), "/api/files/upload/" + metadata.getId());
            ProtocolMessage<FileUploadResponse> response_msg = new ProtocolMessage<>("RESPONSE_FILE_UPLOAD", response_payload);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
        });
    }

    private void handleUpdateProfile(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (user_id) ->
        {
            ProtocolMessage<UpdateProfileRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, UpdateProfileRequest.class);
            UpdateProfileRequest payload = request_msg.getPayload();
            profile_app_service_.updateProfile(user_id, payload.nick_name(), payload.signature(), payload.avatar_file_id())
                    .ifPresentOrElse(
                            updatedUser -> sendSuccessResponse(ctx, "Profile updated successfully.", "UPDATE_PROFILE_SUCCESS"),
                            () -> sendErrorResponse(ctx, "Failed to update profile, user not found.", "UPDATE_PROFILE_REQUEST")
                    );
        });
    }

    private void handleChangeUsername(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (user_id) ->
        {
            ProtocolMessage<ChangeUsernameRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, ChangeUsernameRequest.class);
            ChangeUsernameRequest payload = request_msg.getPayload();
            profile_app_service_.changeUsername(user_id, payload.current_password(), payload.new_user_name());
            sendSuccessResponse(ctx, "Username changed successfully.", "CHANGE_USERNAME_SUCCESS");
        });
    }

    private void handleChangePassword(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (user_id) ->
        {
            ProtocolMessage<ChangePasswordRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, ChangePasswordRequest.class);
            ChangePasswordRequest payload = request_msg.getPayload();
            profile_app_service_.changePassword(user_id, payload.current_password(), payload.new_password());
            sendSuccessResponse(ctx, "Password changed successfully.", "CHANGE_PASSWORD_SUCCESS");
        });
    }

    private void handlePublishKeys(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (user_id) ->
        {
            JsonObject json_object = JsonParser.parseString(json_request).getAsJsonObject();
            String payload_json = json_object.get("payload").toString();
            key_management_service_.publishKeys(user_id, payload_json);
            sendSuccessResponse(ctx, "Keys published successfully.", "PUBLISH_KEYS_SUCCESS");
        });
    }

    private void handleFetchKeys(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (user_id) ->
        {
            ProtocolMessage<FetchKeysRequest> request_msg = JsonUtil.deserializeProtocolMessage(json_request, FetchKeysRequest.class);
            var key = key_management_service_.fetchKeys(user_id);

            if (key.isPresent())
            {
                FetchKeysResponse response_payload = new FetchKeysResponse(true, request_msg.getPayload().user_id(), key.get());
                ProtocolMessage<FetchKeysResponse> response_msg = new ProtocolMessage<>("FETCH_KEYS_RESPONSE", response_payload);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
            }
            else
            {
                FetchKeysResponse response_payload = new FetchKeysResponse(false, request_msg.getPayload().user_id(), "");
                ProtocolMessage<FetchKeysResponse> response_msg = new ProtocolMessage<>("FETCH_KEYS_RESPONSE", response_payload);
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(response_msg)));
            }
        });
    }

    private void handleJoinGroup(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (user_id) ->
        {
            ProtocolMessage<JoinGroupRequest> msg = JsonUtil.deserializeProtocolMessage(json_request, JoinGroupRequest.class);
            group_app_service_.requestToJoinGroup(user_id, msg.getPayload().group_id(), msg.getPayload().request_message());
            sendSuccessResponse(ctx, "请求已发送", "JOIN_GROUP_SUCCESS");
        });
    }

    private void handleProcessJoinRequest(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (user_id) ->
        {
            ProtocolMessage<HandleJoinRequest> msg = JsonUtil.deserializeProtocolMessage(json_request, HandleJoinRequest.class);
            group_app_service_.handleJoinRequest(msg.getPayload().group_id(), user_id, msg.getPayload().request_id(), msg.getPayload().approved());
            sendSuccessResponse(ctx, "请求已处理", "HANDLE_JOIN_SUCCESS");
        });
    }

    private void handleLeaveGroup(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (user_id) ->
        {
            ProtocolMessage<LeaveGroupRequest> msg = JsonUtil.deserializeProtocolMessage(json_request, LeaveGroupRequest.class);
            group_app_service_.leaveGroup(user_id, msg.getPayload().group_id());
            sendSuccessResponse(ctx, "已退出群组", "LEAVE_GROUP_SUCCESS");
        });
    }

    private void handleRemoveMember(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (user_id) ->
        {
            ProtocolMessage<RemoveMemberRequest> msg = JsonUtil.deserializeProtocolMessage(json_request, RemoveMemberRequest.class);
            group_app_service_.removeMember(user_id, msg.getPayload().group_id(), msg.getPayload().target_user_id());
            sendSuccessResponse(ctx, "成员已移除", "REMOVE_MEMBER_SUCCESS");
        });
    }

    private void handleSetMemberRole(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (user_id) ->
        {
            ProtocolMessage<SetMemberRoleRequest> msg = JsonUtil.deserializeProtocolMessage(json_request, SetMemberRoleRequest.class);
            group_app_service_.setMemberRole(user_id, msg.getPayload().group_id(), msg.getPayload().target_user_id(), msg.getPayload().new_role());
            sendSuccessResponse(ctx, "角色已更新", "SET_MEMBER_ROLE_SUCCESS");
        });
    }

    private void handleChangeGroupJoinMode(ChannelHandlerContext ctx, String json_request)
    {
        withAuthenticatedUser(ctx, json_request, (user_id) ->
        {
            ProtocolMessage<ChangeGroupJoinModeRequest> msg = JsonUtil.deserializeProtocolMessage(json_request, ChangeGroupJoinModeRequest.class);
            group_app_service_.changeGroupJoinMode(user_id, msg.getPayload().group_id(), msg.getPayload().new_mode());
            sendSuccessResponse(ctx, "加群方式已更新", "CHANGE_GROUP_JOIN_MODE_SUCCESS");
        });
    }

    private void withAuthenticatedUser(ChannelHandlerContext ctx, String json_request, ThrowingConsumer<UUID> action)
    {
        UUID user_id = session_manager_.getUserId(ctx.channel());
        if (user_id == null)
        {
            sendErrorResponse(ctx, "Not logged in.", JsonParser.parseString(json_request).getAsJsonObject().get("type").getAsString());
            return;
        }
        try
        {
            action.accept(user_id);
        } catch (Exception e)
        {
            e.printStackTrace();
            sendErrorResponse(ctx, e.getMessage(), JsonParser.parseString(json_request).getAsJsonObject().get("type").getAsString());
        }
    }

    @FunctionalInterface
    private interface ThrowingConsumer<T>
    {
        void accept(T t) throws Exception;
    }

    private void sendErrorResponse(ChannelHandlerContext ctx, String error_message, String request_type)
    {
        ProtocolMessage<ErrorResponse> error_msg = new ProtocolMessage<>("ERROR_RESPONSE", new ErrorResponse(error_message, request_type));
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(error_msg)));
    }

    private void sendSuccessResponse(ChannelHandlerContext ctx, String message, String response_type)
    {
        ProtocolMessage<String> success_msg = new ProtocolMessage<>(response_type, message);
        ctx.channel().writeAndFlush(new TextWebSocketFrame(JsonUtil.serialize(success_msg)));
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

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception
    {
        /*
        if (evt instanceof IdleStateEvent)
        {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE)
            {
                System.out.println("Timeout: " + ctx.channel().id());
                ctx.close();
            }
        } else
        {
            super.userEventTriggered(ctx, evt);
        }
        */
    }
}