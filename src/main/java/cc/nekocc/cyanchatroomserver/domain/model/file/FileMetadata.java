package cc.nekocc.cyanchatroomserver.domain.model.file;

import com.github.f4b6a3.uuid.UuidCreator;
import java.time.OffsetDateTime;
import java.util.UUID;

public class FileMetadata
{
    private UUID id_;
    private String original_name_;
    private String storage_path_;
    private Long file_size_;
    private String mime_type_;
    private UUID uploader_id_;
    private FileStatus status_;
    private OffsetDateTime created_at_;
    private OffsetDateTime expires_at_;

    public FileMetadata(String original_name, Long file_size, String mime_type, UUID uploader_id, Integer expires_in_hours)
    {
        this.id_ = UuidCreator.getTimeOrderedEpoch();
        this.original_name_ = original_name;
        this.storage_path_ = this.id_.toString();
        this.file_size_ = file_size;
        this.mime_type_ = mime_type;
        this.uploader_id_ = uploader_id;
        this.status_ = FileStatus.PENDING_UPLOAD;
        this.created_at_ = OffsetDateTime.now();
        this.expires_at_ = this.created_at_.plusHours(expires_in_hours);
    }

    public FileMetadata()
    {  }

    public UUID getId()
    {
        return id_;
    }

    public void setId(UUID id)
    {
        this.id_ = id;
    }

    public String getOriginalName()
    {
        return original_name_;
    }

    public void setOriginalName(String original_name)
    {
        this.original_name_ = original_name;
    }

    public String getStoragePath()
    {
        return storage_path_;
    }

    public void setStoragePath(String storage_path)
    {
        this.storage_path_ = storage_path;
    }

    public Long getFileSize()
    {
        return file_size_;
    }

    public void setFileSize(Long file_size)
    {
        this.file_size_ = file_size;
    }

    public String getMimeType()
    {
        return mime_type_;
    }

    public void setMimeType(String mime_type)
    {
        this.mime_type_ = mime_type;
    }

    public UUID getUploaderId()
    {
        return uploader_id_;
    }

    public void setUploaderId(UUID uploader_id)
    {
        this.uploader_id_ = uploader_id;
    }

    public FileStatus getStatus()
    {
        return status_;
    }

    public void setStatus(FileStatus status)
    {
        this.status_ = status;
    }

    public OffsetDateTime getCreatedAt()
    {
        return created_at_;
    }

    public void setCreatedAt(OffsetDateTime created_at)
    {
        this.created_at_ = created_at;
    }

    public OffsetDateTime getExpiresAt()
    {
        return expires_at_;
    }

    public void setExpiresAt(OffsetDateTime expires_at)
    {
        this.expires_at_ = expires_at;
    }
}