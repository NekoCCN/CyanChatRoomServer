package cc.nekocc.cyanchatroomserver.presentation.command;

import cc.nekocc.cyanchatroomserver.constant.MessageType;
import cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.*;
import cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.file.RequestUploadCommandHandler;
import cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.friendship.*;
import cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.group.*;
import cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.user.*;
import cc.nekocc.cyanchatroomserver.presentation.command.commandhandler.e2ee.*;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class CommandHandlerRegistry
{
    private static final CommandHandlerRegistry INSTANCE = new CommandHandlerRegistry();
    private final Map<String, CommandHandler> handler_map_ = new ConcurrentHashMap<>();

    private CommandHandlerRegistry()
    {
        registerAllHandlers();
    }

    public static CommandHandlerRegistry getInstance()
    {
        return INSTANCE;
    }

    private void registerAllHandlers()
    {
        // User
        handler_map_.put(MessageType.REGISTER_REQUEST, new RegisterCommandHandler());
        handler_map_.put(MessageType.LOGIN_REQUEST, new LoginCommandHandler());
        handler_map_.put(MessageType.GET_UUID_BY_USERNAME_REQUEST, new GetUuidByUsernameCommandHandler());

        // Message
        handler_map_.put(MessageType.CHAT_MESSAGE, new ChatMessageCommandHandler());

        // Profile
        handler_map_.put(MessageType.UPDATE_PROFILE_REQUEST, new UpdateProfileCommandHandler());
        handler_map_.put(MessageType.CHANGE_USERNAME_REQUEST, new ChangeUsernameCommandHandler());
        handler_map_.put(MessageType.CHANGE_PASSWORD_REQUEST, new ChangePasswordCommandHandler());
        handler_map_.put(MessageType.GET_USER_DETAILS_REQUEST, new GetUserDetailsCommandHandler());

        // File
        handler_map_.put(MessageType.REQUEST_FILE_UPLOAD, new RequestUploadCommandHandler());

        // E2EE
        handler_map_.put(MessageType.PUBLISH_KEYS_REQUEST, new PublishKeysCommandHandler());
        handler_map_.put(MessageType.FETCH_KEYS_REQUEST, new FetchKeysCommandHandler());

        // Group
        handler_map_.put(MessageType.CREATE_GROUP_REQUEST, new CreateGroupCommandHandler());
        handler_map_.put(MessageType.JOIN_GROUP_REQUEST, new JoinGroupCommandHandler());
        handler_map_.put(MessageType.HANDLE_JOIN_REQUEST, new HandleJoinRequestCommandHandler());
        handler_map_.put(MessageType.LEAVE_GROUP_REQUEST, new LeaveGroupCommandHandler());
        handler_map_.put(MessageType.REMOVE_MEMBER_REQUEST, new RemoveMemberCommandHandler());
        handler_map_.put(MessageType.SET_MEMBER_ROLE_REQUEST, new SetMemberRoleCommandHandler());
        handler_map_.put(MessageType.CHANGE_GROUP_JOIN_MODE_REQUEST, new ChangeGroupJoinModeCommandHandler());

        // Friendship
        handler_map_.put(MessageType.ACCEPT_FRIENDSHIP_REQUEST, new AcceptFriendshipCommandHandler());
        handler_map_.put(MessageType.REJECT_FRIENDSHIP_REQUEST, new RejectFriendshipCommandHandler());
        handler_map_.put(MessageType.SEND_FRIENDSHIP_REQUEST, new SendFriendshipCommandHandler());
        handler_map_.put(MessageType.GET_FRIENDSHIP_LIST_REQUEST, new GetFriendshipListCommandHandler());
        handler_map_.put(MessageType.GET_ACTIVE_FRIENDSHIP_LIST_REQUEST, new GetActiveFriendshipListCommandHandler());
        handler_map_.put(MessageType.CHECK_FRIENDSHIP_EXISTS_REQUEST, new CheckFriendshipExistsCommandHandler());
    }

    public Optional<CommandHandler> getHandler(String type)
    {
        return Optional.ofNullable(handler_map_.get(type));
    }
}