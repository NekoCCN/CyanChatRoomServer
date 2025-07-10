package cc.nekocc.cyanchatroomserver;

import cc.nekocc.cyanchatroomserver.infrastructure.config.HikariDataSourceFactory;
import cc.nekocc.cyanchatroomserver.infrastructure.config.MyBatisUtil;
import cc.nekocc.cyanchatroomserver.infrastructure.task.FileCleanupService;
import cc.nekocc.cyanchatroomserver.presentation.handler.ChatServerInitializer;
import cc.nekocc.cyanchatroomserver.util.JsonUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CyanChatServer
{
    public static void main(String... args) throws Exception
    {
        ApplicationConfig config;

        if (args.length > 0 && args[0].equals("--help"))
        {
            System.out.println("Usage: java -jar cyan-chat-server.jar <Config File Path>");
            System.out.println("Start the Cyan Chat Room Server.");
            return;
        }
        else if (args.length == 0)
        {
            System.out.println("No config file path provided. Using default config");
            System.out.println("Usage: java -jar cyan-chat-server.jar <Config File Path>");
            System.out.println("Start the Cyan Chat Room Server.");
            return;
        }
        else
        {
            Path config_file_path = Path.of(args[0]);

            if (!config_file_path.toFile().exists())
            {
                System.err.println("Config file not found: " + config_file_path);
                return;
            }
            try
            {
                System.out.println("Loading config from: " + config_file_path);
                String config_json = java.nio.file.Files.readString(config_file_path);
                config = JsonUtil.deserialize(config_json, ApplicationConfig.class);
            }
            catch (Exception e)
            {
                System.err.println("Failed to load config file: " + e.getMessage());
                return;
            }
        }

        HikariDataSourceFactory.initialize(
                config.database_url(),
                config.database_username(),
                config.database_password()
        );
        MyBatisUtil.init();

        FileCleanupService cleanupService = new FileCleanupService(config.file_cleanup_interval_minutes());
        cleanupService.start();

        @SuppressWarnings("deprecation")
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        @SuppressWarnings("deprecation")
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ExecutorService business_executor = Executors.newFixedThreadPool(config.business_thread_count());

        try
        {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerInitializer(business_executor,
                            config.heartbeat_interval_seconds(), config.file_storage_path()));

            ChannelFuture f;

            try
            {
                f = b.bind(config.port()).sync();
                System.out.println("Cyan chat server is start on port: " + config.port());

                f.channel().closeFuture().sync();
            }
            catch (Exception e)
            {
                System.err.println("Failed to bind server: " + e.getMessage());
                throw e;
            }
        }
        finally
        {
            System.out.println("Cyan chat server is shutting down...");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            business_executor.shutdown();
            cleanupService.stop();
        }
    }
}