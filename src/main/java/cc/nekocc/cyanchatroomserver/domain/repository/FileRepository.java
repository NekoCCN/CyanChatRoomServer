package cc.nekocc.cyanchatroomserver.domain.repository;

import cc.nekocc.cyanchatroomserver.domain.criteria.FileSearchCriteria;
import cc.nekocc.cyanchatroomserver.domain.model.file.FileMetadata;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FileRepository
{
    void save(FileMetadata fileMetadata);
    Optional<FileMetadata> findById(UUID id);
    void updateStatus(String id, String status);
    List<FileMetadata> findExpiredFiles(OffsetDateTime now);
    void deleteById(String id);

    /**
     * @param criteria 包含了所有查询条件的对象
     * @return 符合条件的文件元数据列表
     */
    List<FileMetadata> findByCriteria(FileSearchCriteria criteria);
}