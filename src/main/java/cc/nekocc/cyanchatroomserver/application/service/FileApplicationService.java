package cc.nekocc.cyanchatroomserver.application.service;

import cc.nekocc.cyanchatroomserver.domain.model.file.FileMetadata;
import java.util.Optional;

public interface FileApplicationService
{
    FileMetadata requestUpload(Integer uploader_id, String file_name, Long file_size,
                               Integer expires_in_hours);
    void completeUpload(String file_id);
    Optional<FileMetadata> findFileById(String file_id);
}
