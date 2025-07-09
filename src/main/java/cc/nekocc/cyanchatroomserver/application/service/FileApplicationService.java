package cc.nekocc.cyanchatroomserver.application.service;

import cc.nekocc.cyanchatroomserver.domain.model.file.FileMetadata;
import java.util.Optional;
import java.util.UUID;

public interface FileApplicationService
{
    /**
     * 处理文件上传请求, 创建元数据并返回
     */
    FileMetadata requestUpload(UUID uploader_id, String file_name, Long file_size, Integer expires_in_hours);

    /**
     * 标记文件上传已完成
     */
    void completeUpload(String file_id);

    /**
     * 根据ID查找文件元数据
     */
    Optional<FileMetadata> findFileById(String file_id);
}