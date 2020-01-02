package com.example.tb_live_catch.component.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private static Map<String, BufferedOutputStream> channelMap = new ConcurrentHashMap<>();

    private static Map<String, StringBuilder> channelTran = new ConcurrentHashMap<>();

    private static Map<String, Long> channelTimeOutMap = new ConcurrentHashMap<>();

    /**
     * 过期时间 一小时
     */
    private static final int TIMEOUT = 3600000;

    @PostConstruct
    public void cleanExpireChannel() {
        Long nowTimestamp = System.currentTimeMillis();
        List<String> removeKey = new ArrayList<>();
        for (Map.Entry<String, Long> item : channelTimeOutMap.entrySet()) {
            if (nowTimestamp - item.getValue() > TIMEOUT) {
                channelMap.remove(item.getKey());
                removeKey.add(item.getKey());
            }
        }
        removeKey.forEach(k -> {
            channelMap.remove(k);
            channelTimeOutMap.remove(k);
        });
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
        String shortText = ctx.channel().id().asShortText();
        BufferedOutputStream bos = channelMap.get(shortText);
        if (bos == null) {
            FileOutputStream fos = new FileOutputStream("C:\\Users\\Zzz\\Desktop\\" + shortText + ".wav");
            bos = new BufferedOutputStream(fos);
            channelMap.put(shortText, bos);
            channelTimeOutMap.put(shortText, System.currentTimeMillis());
        }
        ByteBuf byteBuf = datagramPacket.content();
        ByteBuffer byteBuffer = ByteBuffer.allocate(byteBuf.readableBytes());
        byteBuf.readBytes(byteBuffer);
        bos.write(byteBuffer.array());
        log.info("收到信息..." + byteBuffer.array().length + " " + Arrays.toString(byteBuffer.array()));
    }
}
