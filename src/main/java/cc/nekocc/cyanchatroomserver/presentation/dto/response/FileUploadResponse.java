package cc.nekocc.cyanchatroomserver.presentation.dto.response;

import java.util.UUID;

public record FileUploadResponse(UUID file_id, String upload_url, String client_request_id)
{  }
