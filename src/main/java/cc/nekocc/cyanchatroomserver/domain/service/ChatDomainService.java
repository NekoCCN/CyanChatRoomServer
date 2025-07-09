package cc.nekocc.cyanchatroomserver.domain.service;

import cc.nekocc.cyanchatroomserver.domain.model.message.MessageContentType;
import cc.nekocc.cyanchatroomserver.domain.model.message.OfflineMessage;
import cc.nekocc.cyanchatroomserver.domain.repository.GroupMemberRepository;
import cc.nekocc.cyanchatroomserver.domain.repository.OfflineMessageRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.session.SessionManager;
import cc.nekocc.cyanchatroomserver.protocol.ProtocolMessage;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ChatDomainService
{
    private final OfflineMessageRepository offline_message_repository_;
    private final GroupMemberRepository group_member_repository_;
    private final SessionManager session_manager_;

    public ChatDomainService(OfflineMessageRepository offline_message_repository, GroupMemberRepository group_member_repository, SessionManager session_manager)
    {
        this.offline_message_repository_ = offline_message_repository;
        this.group_member_repository_ = group_member_repository;
        this.session_manager_ = session_manager;
    }

    /**
     * 转发私聊消息
     */
    public void forwardPrivateMessage(UUID sender_id, UUID receiver_id, String content_type, boolean is_encrypted, String content)
    {
        String jsonMessage = createChatMessageJson(sender_id, "USER", receiver_id, content_type, is_encrypted, content);
        forwardMessageToUser(receiver_id, jsonMessage, content_type, is_encrypted);
    }

    /**
     * 转发群聊消息
     */
    public void forwardGroupMessage(UUID sender_id, UUID group_id, String content_type, boolean is_encrypted, String content)
    {
        String jsonMessage = createChatMessageJson(sender_id, "GROUP", group_id, content_type, is_encrypted, content);
        List<UUID> memberIds = group_member_repository_.findUserIdsByGroupId(group_id);

        for (UUID member_id : memberIds)
        {
            // 群消息不应该发给自己
            if (!member_id.equals(sender_id))
            {
                forwardMessageToUser(member_id, jsonMessage, content_type, is_encrypted);
            }
        }
    }

    /**
     * 将消息投递给单个用户 (在线推送或离线存储)
     */
    private void forwardMessageToUser(UUID receiver_id, String json_message, String content_type, boolean is_encrypted)
    {
        Channel receiverChannel = session_manager_.getChannel(receiver_id);
        if (receiverChannel != null && receiverChannel.isActive())
        {
            // 接收者在线, 直接推送
            receiverChannel.writeAndFlush(new TextWebSocketFrame(json_message));
        } else
        {
            // 接收者不在线, 存入数据库离线信箱
            // 将String类型的contentType转换为枚举
            MessageContentType contentTypeEnum = MessageContentType.valueOf(content_type);
            OfflineMessage offlineMessage = new OfflineMessage(
                    "USER", // 离线消息总是针对单个用户
                    receiver_id,
                    contentTypeEnum,
                    is_encrypted,
                    json_message
            );
            offline_message_repository_.save(offlineMessage);
        }
    }

    /**
     * 创建一个标准格式的广播消息JSON字符串
     */
    private String createChatMessageJson(UUID sender_id, String recipient_type, UUID recipient_id, String content_type, boolean is_encrypted, String content)
    {
        Map<String, Object> payload = Map.of(
                "sender_id", sender_id.toString(),
                "recipient_type", recipient_type,
                (recipient_type.equals("USER") ? "receiver_id" : "group_id"), recipient_id.toString(),
                "content_type", content_type,
                "is_encrypted", is_encrypted,
                "content", content,
                "timestamp", System.currentTimeMillis()
        );
        ProtocolMessage<Map<String, Object>> message = new ProtocolMessage<>("BROADCAST_MESSAGE", payload);
        return JsonUtil.serialize(message);
    }
}
