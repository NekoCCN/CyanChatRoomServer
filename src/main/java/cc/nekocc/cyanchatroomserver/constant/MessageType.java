package cc.nekocc.cyanchatroomserver.constant;

public final class MessageType
{
    private MessageType()
    {  }

    public static final String REGISTER_REQUEST = "REGISTER_REQUEST";
    public static final String LOGIN_REQUEST = "LOGIN_REQUEST";

    public static final String UPDATE_PROFILE_REQUEST = "UPDATE_PROFILE_REQUEST";
    public static final String CHANGE_USERNAME_REQUEST = "CHANGE_USERNAME_REQUEST";
    public static final String CHANGE_PASSWORD_REQUEST = "CHANGE_PASSWORD_REQUEST";

    public static final String CHAT_MESSAGE = "CHAT_MESSAGE";

    public static final String CREATE_GROUP_REQUEST = "CREATE_GROUP_REQUEST";
    public static final String JOIN_GROUP_REQUEST = "JOIN_GROUP_REQUEST";
    public static final String HANDLE_JOIN_REQUEST = "HANDLE_JOIN_REQUEST";
    public static final String LEAVE_GROUP_REQUEST = "LEAVE_GROUP_REQUEST";
    public static final String REMOVE_MEMBER_REQUEST = "REMOVE_MEMBER_REQUEST";
    public static final String SET_MEMBER_ROLE_REQUEST = "SET_MEMBER_ROLE_REQUEST";
    public static final String CHANGE_GROUP_JOIN_MODE_REQUEST = "CHANGE_GROUP_JOIN_MODE_REQUEST";

    public static final String REQUEST_FILE_UPLOAD = "REQUEST_FILE_UPLOAD";

    public static final String PUBLISH_KEYS_REQUEST = "PUBLISH_KEYS_REQUEST";
    public static final String FETCH_KEYS_REQUEST = "FETCH_KEYS_REQUEST";

    public static final String REGISTER_SUCCESS = "REGISTER_SUCCESS";
    public static final String LOGIN_SUCCESS = "LOGIN_SUCCESS";
    public static final String LOGIN_FAILED = "LOGIN_FAILED";
    public static final String BROADCAST_MESSAGE = "BROADCAST_MESSAGE";
    public static final String RESPONSE_FILE_UPLOAD = "RESPONSE_FILE_UPLOAD";
    public static final String ERROR_RESPONSE = "ERROR_RESPONSE";
}