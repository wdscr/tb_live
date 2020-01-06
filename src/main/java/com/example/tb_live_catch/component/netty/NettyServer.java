//package com.example.tb_live_catch.component.netty;
//
//import com.example.tb_live_catch.service.ASRService;
//import io.netty.bootstrap.Bootstrap;
//import io.netty.channel.*;
//import io.netty.channel.nio.NioEventLoopGroup;
//import io.netty.channel.socket.nio.NioDatagramChannel;
//import io.netty.handler.timeout.IdleStateEvent;
//import io.netty.handler.timeout.IdleStateHandler;
//import io.netty.handler.timeout.ReadTimeoutHandler;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.catalina.Pipeline;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
//@Component
//@Slf4j
//public class NettyServer {
//
//    @Autowired
//    ASRService asrService;
//
//    @PostConstruct
//    public void start() {
//        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
//        try {
//            Bootstrap bootstrap = new Bootstrap();
//            bootstrap.group(eventLoopGroup).channel(NioDatagramChannel.class)
//                    .option(ChannelOption.SO_BROADCAST, true)
//                    .option(ChannelOption.SO_REUSEADDR,true)
//                    .handler(new ChannelInitializer<NioDatagramChannel>() {
//                        @Override
//                        protected void initChannel(NioDatagramChannel nioDatagramChannel) throws Exception {
//                            ChannelPipeline p = nioDatagramChannel.pipeline();
//                            p.addLast("timeoutHandler", new IdleStateHandler(0, 0, 30) {
//                                @Override
//                                protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
//                                    super.channelIdle(ctx, evt);
//                                    ctx.channel().close();
//                                    log.info(ctx.channel().id().asShortText() + " close");
//                                }
//                            });
//                            p.addLast(new NettyServerHandler(asrService));
//                        }
//                    });
//
//            log.info("正在监听 9999 端口...");
//            bootstrap.bind(9999).sync().channel().closeFuture().sync();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            eventLoopGroup.shutdownGracefully();
//        }
//    }
//}
