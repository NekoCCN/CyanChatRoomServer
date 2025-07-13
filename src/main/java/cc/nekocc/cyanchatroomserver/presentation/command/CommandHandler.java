package cc.nekocc.cyanchatroomserver.presentation.command;

import io.netty.channel.ChannelHandlerContext;

@FunctionalInterface
public interface CommandHandler
{
    /**
     * 处理一个WebSocket请求
     *
     * @param ctx          Channel上下文
     * @param json_request 原始的JSON请求字符串
     * @throws Exception 如果处理过程中发生错误
     */
    void handle(ChannelHandlerContext ctx, String json_request) throws Exception;
}