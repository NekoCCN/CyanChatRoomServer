package cc.nekocc.cyanchatroomserver.application.impl;

import cc.nekocc.cyanchatroomserver.application.service.FileApplicationService;
import cc.nekocc.cyanchatroomserver.domain.model.file.FileMetadata;
import cc.nekocc.cyanchatroomserver.domain.model.file.FileStatus;
import cc.nekocc.cyanchatroomserver.domain.repository.FileRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.FileRepositoryImpl;
import java.util.Optional;
import java.util.UUID;

public class FileApplicationServiceImpl implements FileApplicationService
{
    private final FileRepository file_repository_ = new FileRepositoryImpl();

    @Override
    public FileMetadata requestUpload(UUID uploader_id, String file_name, Long file_size, Integer expires_in_hours)
    {
        if (expires_in_hours < 1 || expires_in_hours > 48)
        {
            throw new IllegalArgumentException("File expiration time must be between 1 and 48 hours.");
        }
        FileMetadata metadata = new FileMetadata(file_name, file_size, null, uploader_id, expires_in_hours);
        file_repository_.save(metadata);
        return metadata;
    }

    @Override
    public void completeUpload(String file_id)
    {
        file_repository_.updateStatus(file_id, FileStatus.UPLOAD_COMPLETE.name());
    }

    @Override
    public Optional<FileMetadata> findFileById(String file_id)
    {
        return file_repository_.findById(UUID.fromString(file_id));
    }
}