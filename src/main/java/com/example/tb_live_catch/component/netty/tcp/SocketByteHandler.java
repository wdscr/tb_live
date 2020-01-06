package com.example.tb_live_catch.component.netty.tcp;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class SocketByteHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info(String.valueOf(ctx.channel().remoteAddress()));
        log.info(" " + ctx.channel().attr(AttributeKey.valueOf("tkl")));
        log.info(JSONObject.toJSONString(ctx.channel().metadata()));
        log.info(String.valueOf(ctx.channel().isOpen()));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info(ctx.channel().id().asShortText() + " inActive");
        log.info(String.valueOf(ctx.channel().isOpen()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf result = (ByteBuf) msg;
        byte[] bytes = new byte[result.readableBytes()];
        // msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中 
        result.readBytes(bytes);
        //log.info("Client put:" + bytes.length + Arrays.toString(bytes));
        // 释放资源，这行很关键 
        result.release();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        super.channelReadComplete(ctx);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
