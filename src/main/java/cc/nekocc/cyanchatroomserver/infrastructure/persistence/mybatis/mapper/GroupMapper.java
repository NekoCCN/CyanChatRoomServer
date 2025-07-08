package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper;

import cc.nekocc.cyanchatroomserver.domain.model.group.Group;
import org.apache.ibatis.annotations.Param;
import java.util.UUID;

public interface GroupMapper
{
    int insert(Group group);
    Group findById(@Param("id") UUID id);
    int update(Group group);
    int deleteById(@Param("id") UUID id);
}