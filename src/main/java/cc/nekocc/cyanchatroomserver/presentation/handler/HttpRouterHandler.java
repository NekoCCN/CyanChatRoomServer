package cc.nekocc.cyanchatroomserver.presentation.handler;

import cc.nekocc.cyanchatroomserver.application.impl.FileApplicationServiceImpl;
import cc.nekocc.cyanchatroomserver.infrastructure.storage.FileSystemStorageService;
import cc.nekocc.cyanchatroomserver.application.service.FileApplicationService;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

public class HttpRouterHandler extends SimpleChannelInboundHandler<FullHttpRequest>
{
    private final ExecutorService business_executor_;
    private final FileApplicationService file_app_service_ = new FileApplicationServiceImpl();
    private final FileSystemStorageService storage_service_ = new FileSystemStorageService();
    private static final HttpDataFactory factory_ = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    public HttpRouterHandler(ExecutorService businessExecutor)
    {
        this.business_executor_ = businessExecutor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request)
    {
        String uri = request.uri();
        HttpMethod method = request.method();

        if (method.equals(HttpMethod.POST) && uri.startsWith("/api/files/upload/"))
        {
            request.retain();
            business_executor_.submit(() ->
            {
                try
                {
                    handleUpload(ctx, request);
                } finally
                {
                    request.release();
                }
            });
        }
        else if (method.equals(HttpMethod.GET) && uri.startsWith("/api/files/download/"))
        {
            request.retain();
            business_executor_.submit(() ->
            {
                try
                {
                    handleDownload(ctx, request);
                } finally
                {
                    request.release();
                }
            });
        }
        else if (uri.startsWith("/ws"))
        {
            ctx.fireChannelRead(request.retain());
        }
        else
        {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
        }
    }

    private void handleUpload(ChannelHandlerContext ctx, FullHttpRequest request)
    {
        HttpPostRequestDecoder decoder = null;
        try {
            decoder = new HttpPostRequestDecoder(factory_, request);
            InterfaceHttpData data = decoder.getBodyHttpData("file");

            if (data != null && data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload)
            {
                FileUpload file_upload = (FileUpload) data;
                String file_id = request.uri().substring(request.uri().lastIndexOf('/') + 1);

                try (InputStream in = new ByteBufInputStream(file_upload.getByteBuf()))
                {
                    storage_service_.save(in, file_id);
                }

                file_app_service_.completeUpload(file_id);

                sendOk(ctx, "Upload complete for file_id: " + file_id);
            }
            else
            {
                sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        finally
        {
            if (decoder != null)
            {
                decoder.destroy();
            }
        }
    }

    private void handleDownload(ChannelHandlerContext ctx, FullHttpRequest request)
    {
        String fileId = request.uri().substring(request.uri().lastIndexOf('/') + 1);
        try
        {
            file_app_service_.findFileById(fileId).ifPresentOrElse(metadata ->
            {
                try
                {
                    Path file_path = storage_service_.load(metadata.getStoragePath());
                    File file = file_path.toFile();
                    if (file.exists() && !file.isHidden())
                    {
                        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                        response.headers().set(HttpHeaderNames.CONTENT_TYPE, metadata.getMimeType() != null ? metadata.getMimeType() : "application/octet-stream");
                        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
                        response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalName() + "\"");

                        ctx.write(response);
                        ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(file, 0, file.length()), ctx.newProgressivePromise());
                        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

                        sendFileFuture.addListener(future ->
                        {
                            if (!future.isSuccess())
                            {
                                System.err.println("Send file failed: " + future.cause());
                            }
                        });
                    } else
                    {
                        sendError(ctx, HttpResponseStatus.NOT_FOUND);
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
                }
            }, () -> sendError(ctx, HttpResponseStatus.NOT_FOUND));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status)
    {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status + "\r\n", CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private static void sendOk(ChannelHandlerContext ctx, String message)
    {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}