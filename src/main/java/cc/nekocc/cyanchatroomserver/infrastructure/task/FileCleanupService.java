package cc.nekocc.cyanchatroomserver.infrastructure.task;

import cc.nekocc.cyanchatroomserver.domain.model.file.FileMetadata;
import cc.nekocc.cyanchatroomserver.domain.repository.FileRepository;
import cc.nekocc.cyanchatroomserver.infrastructure.persistence.mybatis.repository.FileRepositoryImpl;
import cc.nekocc.cyanchatroomserver.infrastructure.storage.FileSystemStorageService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 负责周期性清理过期文件的后台服务
 */
public class FileCleanupService
{
    private final long cleanup_interval_minutes_;

    private final ScheduledExecutorService scheduler_ = Executors.newSingleThreadScheduledExecutor();
    private final FileRepository file_repository_ = new FileRepositoryImpl();
    private final FileSystemStorageService storage_service_ = new FileSystemStorageService();

    public FileCleanupService(long cleanup_interval_minutes)
    {
        cleanup_interval_minutes_ = cleanup_interval_minutes;
    }
    public FileCleanupService()
    {
        this(60);
    }

    public void start()
    {
        Runnable cleanup_task = () ->
        {
            System.out.println("TASK: 开始执行文件清理任务...");
            try
            {
                List<FileMetadata> expired_files = file_repository_.findExpiredFiles(OffsetDateTime.now());
                if (expired_files.isEmpty())
                {
                    System.out.println("TASK: 没有需要清理的过期文件。");
                    return;
                }

                System.out.println("TASK: 发现 " + expired_files.size() + " 个过期文件需要删除。");
                for (FileMetadata file_info : expired_files)
                {
                    // 先删除物理文件
                    if (storage_service_.delete(file_info.getStoragePath()))
                    {
                        // 物理文件删除成功后, 才删除数据库记录
                        file_repository_.deleteById(file_info.getId().toString());
                        System.out.println("TASK: 已成功删除过期文件: " + file_info.getStoragePath());
                    } else if (!new java.io.File(storage_service_.load(file_info.getStoragePath()).toUri()).exists())
                    {
                        // 如果物理文件本就不存在, 也直接删除数据库记录
                        file_repository_.deleteById(file_info.getId().toString());
                        System.out.println("TASK: 物理文件不存在, 已清理数据库记录: " + file_info.getStoragePath());
                    } else
                    {
                        System.err.println("TASK: 删除物理文件失败，跳过数据库记录删除: " + file_info.getStoragePath());
                    }
                }
            } catch (Exception e)
            {
                System.err.println("TASK: 文件清理任务执行出错。");
                e.printStackTrace();
            }
        };

        scheduler_.scheduleAtFixedRate(cleanup_task, 0, cleanup_interval_minutes_, TimeUnit.MINUTES);

        System.out.println("File Cleaner is runner, will run every " + cleanup_interval_minutes_ + " minutes.");
    }

    public void stop()
    {
        scheduler_.shutdown();
    }
}
