package cc.nekocc.cyanchatroomserver.presentation.dto.request;

public record FileUploadRequest(String file_name, Long file_size,
                                Integer expires_in_hours)
{  }
