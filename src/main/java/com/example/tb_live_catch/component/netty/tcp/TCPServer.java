package com.example.tb_live_catch.component.netty.tcp;

import com.example.tb_live_catch.service.ASRService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TCPServer {

    //端口号
    @Value("${tran-server.port:13570}")
    private int port;

    //服务器运行状态
    private volatile boolean isRunning = false;
    //处理Accept连接事件的线程，这里线程数设置为1即可，netty处理链接事件默认为单线程，过度设置反而浪费cpu资源
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    //处理hadnler的工作线程，其实也就是处理IO读写 。线程数据默认为 CPU 核心数乘以2
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(8);

    @Autowired
    ASRService asrService;

    @PostConstruct
    public void start() throws InterruptedException {
        //创建ServerBootstrap实例
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        //初始化ServerBootstrap的线程组
        serverBootstrap.group(bossGroup,workerGroup);//
        //设置将要被实例化的ServerChannel类
        serverBootstrap.channel(NioServerSocketChannel.class);//
        //在ServerChannelInitializer中初始化ChannelPipeline责任链，并添加到serverBootstrap中
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                //IdleStateHandler心跳机制,如果超时触发Handle中userEventTrigger()方法
//                pipeline.addLast("idleStateHandler",
//                        new IdleStateHandler(15, 0, 0, TimeUnit.SECONDS) {
//                            @Override
//                            protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
//                                super.channelIdle(ctx, evt);
//                                ctx.channel().close();
//                                log.info(ctx.channel().id().asLongText() + " close");
//                            }
//                        });
                pipeline.addLast(new SocketByteHandler(asrService));
            }
        });
        //标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        // 是否启用心跳保活机机制
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        //绑定端口后，开启监听
        ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
        if(channelFuture.isSuccess()){
            log.info("TCP服务启动 成功---------------");
        }
    }
}
