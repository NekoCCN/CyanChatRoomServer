package cc.nekocc.cyanchatroomserver.domain.model.message;

import com.github.f4b6a3.uuid.UuidCreator;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * OfflineMessage 实体, 代表一个待投递的离线消息任务。
 * 使用统一的接收方标识和独立的加密状态。
 */
public class OfflineMessage
{
    private UUID id_;
    private String recipient_type_;
    private UUID recipient_id_;

    private MessageContentType content_type_;

    private boolean is_encrypted_;

    private String message_payload_;
    private OffsetDateTime stored_at_;

    public OfflineMessage(String recipient_type, UUID recipient_id, MessageContentType content_type, boolean is_encrypted, String message_payload)
    {
        id_ = UuidCreator.getTimeOrderedEpoch();
        recipient_type_ = recipient_type;
        recipient_id_ = recipient_id;
        content_type_ = content_type;
        is_encrypted_ = is_encrypted;
        message_payload_ = message_payload;
        stored_at_ = OffsetDateTime.now();
    }

    public OfflineMessage()
    {  }

    public UUID getId()
    {
        return id_;
    }

    public String getRecipientType()
    {
        return recipient_type_;
    }

    public void setRecipientType(String recipient_type)
    {
        recipient_type_ = recipient_type;
    }

    public UUID getRecipientId()
    {
        return recipient_id_;
    }

    public void setRecipientId(UUID recipient_id)
    {
        recipient_id_ = recipient_id;
    }

    public MessageContentType getContentType()
    {
        return content_type_;
    }

    public void setContentType(MessageContentType content_type)
    {
        content_type_ = content_type;
    }

    public boolean isEncrypted()
    {
        return is_encrypted_;
    }

    public void setEncrypted(boolean is_encrypted)
    {
        is_encrypted_ = is_encrypted;
    }

    public String getMessagePayload()
    {
        return message_payload_;
    }

    public void setMessagePayload(String message_payload)
    {
        message_payload_ = message_payload;
    }

    public OffsetDateTime getStoredAt()
    {
        return stored_at_;
    }

    public void setStoredAt(OffsetDateTime stored_at)
    {
        stored_at_ = stored_at;
    }
}