package cc.nekocc.cyanchatroomserver.presentation.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatServerInitializer extends ChannelInitializer<SocketChannel>
{
    private final ExecutorService business_executor_;
    private final int heartbeat_interval_seconds_;
    private final String file_storage_path_;

    public ChatServerInitializer(ExecutorService businessExecutor, int heartbeat_interval_seconds, String file_storage_path)
    {
        business_executor_ = businessExecutor;
        heartbeat_interval_seconds_ = heartbeat_interval_seconds;
        file_storage_path_ = file_storage_path;
    }

    @Override
    protected void initChannel(SocketChannel ch)
    {
        ChannelPipeline p = ch.pipeline();
        // HTTP Codec
        p.addLast(new HttpServerCodec());
        // HTTP Aggregator
        p.addLast(new HttpObjectAggregator(1024 * 1024 * 100));
        // Support for large data streams
        p.addLast(new ChunkedWriteHandler());
        // Heartbeat
        p.addLast(new IdleStateHandler(heartbeat_interval_seconds_, 0, 0, TimeUnit.SECONDS));
        // HTTP Router Handler
        p.addLast(new HttpRouterHandler(business_executor_, file_storage_path_));
        // WebSocket Protocol Handler
        p.addLast(new WebSocketServerProtocolHandler("/ws"));
        // WebSocket Business Handler
        p.addLast(new WebSocketHandler(business_executor_));
    }
}