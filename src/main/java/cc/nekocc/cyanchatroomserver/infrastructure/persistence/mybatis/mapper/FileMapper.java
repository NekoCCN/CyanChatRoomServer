package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper;

import cc.nekocc.cyanchatroomserver.domain.criteria.FileSearchCriteria;
import cc.nekocc.cyanchatroomserver.domain.model.file.FileMetadata;
import org.apache.ibatis.annotations.Param;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface FileMapper
{
    int insert(FileMetadata fileMetadata);
    FileMetadata findById(@Param("id") UUID id);
    int updateStatus(@Param("id") String id, @Param("status") String status);
    List<FileMetadata> findExpiredFiles(@Param("now") OffsetDateTime now);
    int deleteById(@Param("id") String id);

    List<FileMetadata> findByCriteria(@Param("criteria") FileSearchCriteria criteria);
}