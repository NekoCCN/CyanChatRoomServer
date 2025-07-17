package cc.nekocc.cyanchatroomserver.infrastructure.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 负责与本地文件系统进行交互的服务
 */
public class FileSystemStorageService
{
    private final Path root_location;

    public FileSystemStorageService(String storage_path)
    {
        if (storage_path == null || storage_path.isEmpty())
        {
            throw new IllegalArgumentException("存储路径不能为空。");
        }

        this.root_location = Paths.get(storage_path).toAbsolutePath().normalize();

        if (!Files.exists(root_location))
        {
            try
            {
                Files.createDirectories(root_location);
            } catch (IOException e)
            {
                throw new RuntimeException("Could not initialize storage directory: " + root_location, e);
            }
        }
    }

    /**
     * 将输入流保存到指定的存储路径
     *
     * @param input_stream 文件输入流
     * @param storage_path 文件的唯一存储名 (例如 UUID)
     */
    public void save(InputStream input_stream, String storage_path) throws IOException
    {
        if (storage_path == null || storage_path.isEmpty())
        {
            throw new IllegalArgumentException("存储路径不能为空。");
        }
        Path destination_file = this.root_location.resolve(Paths.get(storage_path)).normalize().toAbsolutePath();
        // 安全性检查，防止目录遍历攻击
        if (!destination_file.getParent().equals(this.root_location.toAbsolutePath()))
        {
            throw new SecurityException("不能将文件存储在当前目录之外。");
        }
        // 确保目标目录存在
        if (!Files.exists(destination_file.getParent()))
        {
            throw new IOException("ROOT目录不存在: " + destination_file.getParent());
        }
        try (input_stream)
        {
            Files.copy(input_stream, destination_file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * 根据存储路径加载文件
     *
     * @param storage_path 文件的唯一存储名
     * @return Path对象
     */
    public Path load(String storage_path)
    {
        return root_location.resolve(storage_path);
    }

    /**
     * 根据存储路径删除文件
     *
     * @param storage_path 文件的唯一存储名
     * @return 是否删除成功
     */
    public boolean delete(String storage_path)
    {
        if (storage_path == null || storage_path.isEmpty())
        {
            return false;
        }
        try
        {
            Path file = load(storage_path);
            return Files.deleteIfExists(file);
        } catch (IOException e)
        {
            System.err.println("删除文件失败: " + storage_path);
            e.printStackTrace();
            return false;
        }
    }
}
