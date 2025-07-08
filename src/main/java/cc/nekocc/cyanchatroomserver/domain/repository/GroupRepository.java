package cc.nekocc.cyanchatroomserver.domain.repository;
import cc.nekocc.cyanchatroomserver.domain.model.group.Group;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository
{
    void save(Group group);
    Optional<Group> findById(UUID id);
    void update(Group group);
    void deleteById(UUID id);
}