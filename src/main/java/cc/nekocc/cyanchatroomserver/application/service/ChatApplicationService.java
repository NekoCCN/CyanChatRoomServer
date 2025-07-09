package cc.nekocc.cyanchatroomserver.application.service;

import java.util.UUID;

public interface ChatApplicationService
{
    /**
     * 发送消息 (单聊或群聊)
     * @param sender_id 发送者ID
     * @param recipient_type 接收方类型 ('USER' 或 'GROUP')
     * @param recipient_id 接收方ID (用户ID 或 群组ID)
     * @param content_type 内容类型 ('TEXT', 'IMAGE', 'FILE')
     * @param is_encrypted 内容是否加密
     * @param content 消息内容
     */
    void sendMessage(UUID sender_id, String recipient_type, UUID recipient_id, String content_type, boolean is_encrypted, String content);
}
