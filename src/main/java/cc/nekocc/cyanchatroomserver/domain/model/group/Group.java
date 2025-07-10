package cc.nekocc.cyanchatroomserver.domain.model.group;

import com.github.f4b6a3.uuid.UuidCreator;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Group
{
    private UUID id_;
    private String name_;
    private UUID creator_id_;
    private GroupJoinMode join_mode_;
    private OffsetDateTime created_at_;

    public Group(String name, UUID creator_id)
    {
        id_ = UuidCreator.getTimeOrderedEpoch();
        name_ = name;
        creator_id_ = creator_id;
        join_mode_ = GroupJoinMode.VERIFICATION;
        created_at_ = OffsetDateTime.now();
    }

    public Group()
    {
    }
    
    public void changeJoinMode(GroupJoinMode new_mode)
    {
        join_mode_ = new_mode;
    }

    public boolean isCreator(UUID user_id)
    {
        return creator_id_.equals(user_id);
    }
    
    public UUID getId()
    {
        return id_;
    }

    public void setId(UUID id)
    {
        id_ = id;
    }

    public String getName()
    {
        return name_;
    }

    public void setName(String name)
    {
        name_ = name;
    }

    public UUID getCreatorId()
    {
        return creator_id_;
    }

    public void setCreatorId(UUID creator_id)
    {
        creator_id_ = creator_id;
    }

    public GroupJoinMode getJoinMode()
    {
        return join_mode_;
    }

    public void setJoinMode(GroupJoinMode join_mode)
    {
        join_mode_ = join_mode;
    }

    public OffsetDateTime getCreatedAt()
    {
        return created_at_;
    }

    public void setCreatedAt(OffsetDateTime created_at)
    {
        created_at_ = created_at;
    }
}