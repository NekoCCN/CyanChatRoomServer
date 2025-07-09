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

    public ChatServerInitializer(ExecutorService businessExecutor)
    {
        business_executor_ = businessExecutor;
    }

    @Override
    protected void initChannel(SocketChannel ch)
    {
        ChannelPipeline p = ch.pipeline();
        // HTTP编解码器
        p.addLast(new HttpServerCodec());
        // 聚合HTTP消息为单一的FullHttpRequest或FullHttpResponse
        p.addLast(new HttpObjectAggregator(1024 * 1024 * 100));
        // 支持大文件传输
        p.addLast(new ChunkedWriteHandler());
        // HTTP请求路由器
        p.addLast(new HttpRouterHandler(business_executor_));
        // WebSocket协议处理器
        p.addLast(new WebSocketServerProtocolHandler("/ws"));
        // WebSocket业务逻辑处理器
        p.addLast(new WebSocketHandler(business_executor_));
    }
}