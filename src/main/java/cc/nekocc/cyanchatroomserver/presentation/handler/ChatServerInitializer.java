package cc.nekocc.cyanchatroomserver.presentation.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import java.util.concurrent.ExecutorService;

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
        // 将多个HTTP消息部分聚合为单个FullHttpRequest或FullHttpResponse，设置最大100MB
        p.addLast(new HttpObjectAggregator(1024 * 1024 * 100));
        // 支持异步发送大的码流（例如大文件），不会导致内存溢出
        p.addLast(new ChunkedWriteHandler());
        // HTTP请求路由器，处理API请求（如文件上传下载），必须在WebSocket处理器之前
        p.addLast(new HttpRouterHandler(business_executor_));
        // WebSocket协议处理器，处理握手、Ping/Pong等
        p.addLast(new WebSocketServerProtocolHandler("/ws"));
        // WebSocket业务逻辑处理器
        p.addLast(new WebSocketHandler(business_executor_));
    }
}