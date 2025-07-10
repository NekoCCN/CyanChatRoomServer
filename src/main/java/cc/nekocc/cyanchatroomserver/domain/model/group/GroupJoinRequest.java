package cc.nekocc.cyanchatroomserver.domain.model.group;

import java.time.OffsetDateTime;
import java.util.UUID;

public class GroupJoinRequest
{
    private Long id_;
    private UUID group_id_;
    private UUID user_id_;
    private GroupJoinRequestStatus status_;
    private String request_message_;
    private OffsetDateTime requested_at_;
    private OffsetDateTime handled_at_;
    private UUID handler_id_;

    public GroupJoinRequest(UUID group_id, UUID user_id, String request_message)
    {
        group_id_ = group_id;
        user_id_ = user_id;
        request_message_ = request_message;
        status_ = GroupJoinRequestStatus.PENDING;
        requested_at_ = OffsetDateTime.now();
    }

    public GroupJoinRequest()
    {
    }

    public void approve(UUID handler_id)
    {
        if (status_ == GroupJoinRequestStatus.PENDING)
        {
            status_ = GroupJoinRequestStatus.APPROVED;
            handler_id_ = handler_id;
            handled_at_ = OffsetDateTime.now();
        }
    }

    public void reject(UUID handler_id)
    {
        if (status_ == GroupJoinRequestStatus.PENDING)
        {
            status_ = GroupJoinRequestStatus.REJECTED;
            handler_id_ = handler_id;
            handled_at_ = OffsetDateTime.now();
        }
    }
    
    public Long getId()
    {
        return id_;
    }
    public void setId(Long id)
    {
        id_ = id;
    }
    public UUID getGroupId()
    {
        return group_id_;
    }
    public void setGroupId(UUID group_id)
    {
        group_id_ = group_id;
    }
    public UUID getUserId()
    {
        return user_id_;
    }
    public void setUserId(UUID user_id)
    {
        user_id_ = user_id;
    }
    public GroupJoinRequestStatus getStatus()
    {
        return status_;
    }
    public void setStatus(GroupJoinRequestStatus status)
    {
        status_ = status;
    }
    public String getRequestMessage()
    {
        return request_message_;
    }
    public void setRequestMessage(String request_message)
    {
        request_message_ = request_message;
    }
    public OffsetDateTime getRequestedAt()
    {
        return requested_at_;
    }
    public void setRequestedAt(OffsetDateTime requested_at)
    {
        requested_at_ = requested_at;
    }
    public OffsetDateTime getHandledAt()
    {
        return handled_at_;
    }
    public void setHandledAt(OffsetDateTime handled_at)
    {
        handled_at_ = handled_at;
    }
    public UUID getHandlerId()
    {
        return handler_id_;
    }
    public void setHandlerId(UUID handler_id)
    {
        handler_id_ = handler_id;
    }
}