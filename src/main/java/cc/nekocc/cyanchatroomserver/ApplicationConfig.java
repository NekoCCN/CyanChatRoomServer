package cc.nekocc.cyanchatroomserver;

public record ApplicationConfig(
        int port,
        int business_thread_count,
        String database_url,
        String database_username,
        String database_password,
        int heartbeat_interval_seconds,
        String file_storage_path,
        long file_cleanup_interval_minutes
)
{  }