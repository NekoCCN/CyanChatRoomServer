package cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository;

import cc.nekocc.cyanchatroomserver.domain.criteria.FileSearchCriteria;
import cc.nekocc.cyanchatroomserver.domain.model.file.FileMetadata;
import cc.nekocc.cyanchatroomserver.domain.repository.FileRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.config.MyBatisUtil;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.mapper.FileMapper;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileRepositoryImpl implements FileRepository
{
    @Override
    public void save(FileMetadata fileMetadata)
    {
        MyBatisUtil.executeUpdate(session -> session.getMapper(FileMapper.class).insert(fileMetadata));
    }

    @Override
    public Optional<FileMetadata> findById(UUID id)
    {
        return Optional.ofNullable(MyBatisUtil.executeQuery(session -> session.getMapper(FileMapper.class).findById(id)));
    }

    @Override
    public void updateStatus(String id, String status)
    {
        MyBatisUtil.executeUpdate(session -> session.getMapper(FileMapper.class).updateStatus(id, status));
    }

    @Override
    public List<FileMetadata> findExpiredFiles(OffsetDateTime now)
    {
        return MyBatisUtil.executeQuery(session -> session.getMapper(FileMapper.class).findExpiredFiles(now));
    }

    @Override
    public void deleteById(String id)
    {
        MyBatisUtil.executeUpdate(session -> session.getMapper(FileMapper.class).deleteById(id));
    }

    @Override
    public List<FileMetadata> findByCriteria(FileSearchCriteria criteria)
    {
        return MyBatisUtil.executeQuery(session ->
                session.getMapper(FileMapper.class).findByCriteria(criteria)
        );
    }
}