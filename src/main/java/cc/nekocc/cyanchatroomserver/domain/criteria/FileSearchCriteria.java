package cc.nekocc.cyanchatroomserver.domain.criteria;

import java.time.OffsetDateTime;
import java.util.UUID;

public class FileSearchCriteria
{
    private UUID uploader_id_;
    private String original_name_like_;
    private String mime_type_is_;
    private OffsetDateTime created_after_;
    private OffsetDateTime created_before_;
    
    public FileSearchCriteria byUploader(UUID uploader_id)
    {
        uploader_id_ = uploader_id;
        return this;
    }

    public FileSearchCriteria withOriginalNameLike(String name_fragment)
    {
        original_name_like_ = name_fragment;
        return this;
    }

    public FileSearchCriteria withMimeType(String mime_type)
    {
        mime_type_is_ = mime_type;
        return this;
    }

    public FileSearchCriteria createdAfter(OffsetDateTime date)
    {
        created_after_ = date;
        return this;
    }

    public FileSearchCriteria createdBefore(OffsetDateTime date)
    {
        created_before_ = date;
        return this;
    }

    // Getters for all fields...
    public UUID getUploaderId()
    {
        return uploader_id_;
    }

    public String getOriginalNameLike()
    {
        return original_name_like_;
    }

    public String getMimeTypeIs()
    {
        return mime_type_is_;
    }

    public OffsetDateTime getCreatedAfter()
    {
        return created_after_;
    }

    public OffsetDateTime getCreatedBefore()
    {
        return created_before_;
    }
}
