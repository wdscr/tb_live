package com.example.tb_live_catch.component.netty.tcp;

import com.alibaba.fastjson.JSONObject;
import com.example.tb_live_catch.service.ASRService;
import com.example.tb_live_catch.thrift.asr.ASRResult;
import com.example.tb_live_catch.thrift.asr.ASRServ;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketByteHandler extends ChannelInboundHandlerAdapter {

    ASRService asrService;

    Map<String, ASRServ.Client> ASRClinetMap = new ConcurrentHashMap<>();

    Map<String, FileOutputStream> fileMap = new ConcurrentHashMap<>();

    Map<String, Long> streamSizeMap = new ConcurrentHashMap<>();

    public SocketByteHandler(ASRService asrService) {
        this.asrService = asrService;
    }

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
        String shortId = ctx.channel().id().asShortText();
        ASRServ.Client client = ASRClinetMap.get(shortId);
//        client.ASR_audio_write(ByteBuffer.wrap(new byte[]{0}), 4);
        ASRResult rs = null;
                StringBuilder sb = new StringBuilder();
                    do {
                       rs = client.ASR_get_result();
                        if (rs !=null && StringUtils.isNotBlank(rs.getText())) {
                            sb.append(rs.getText());
                            if(rs.getText().matches("。|！|？")) {
                                sb.append("\n");

                            }
                            log.info("=========================================");
                            log.info(sb.toString());
                            log.info("=========================================");
                        }
                    } while (rs == null || rs.getRsltStatus() == 2 || rs.getRsltStatus() == 0);

                    fileMap.get(shortId).close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf result = (ByteBuf) msg;
        byte[] bytes = new byte[result.readableBytes()];
        // msg中存储的是ByteBuf类型的数据，把数据读取到byte[]中
        result.readBytes(bytes);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        String shortId = ctx.channel().id().asShortText();
        ASRServ.Client asrClient = ASRClinetMap.get(shortId);
        FileOutputStream fos = fileMap.get(shortId);
        if (asrClient == null) {

            fos = new FileOutputStream("C:\\Users\\Xxx\\Desktop\\test.wav");
            fileMap.put(shortId, fos);
            asrClient = asrService.create();
            ASRClinetMap.put(shortId, asrClient);
            streamSizeMap.put(shortId, Long.valueOf(bytes.length));
            asrClient.ASR_audio_write(byteBuffer, 1);
            ASRServ.Client finalAsrClient = asrClient;
            (new Thread(() -> {
                log.info("start new thread ...");
//                StringBuilder sb = new StringBuilder();
//                try {
//                    ASRResult rs = null;
//                    Long start = 0L;
//                    do {
//                       rs = finalAsrClient.ASR_get_result();
//                        if (rs !=null && StringUtils.isNotBlank(rs.getText())) {
//                            sb.append(rs.getText());
//                            if(rs.getText().matches("。|！|？")) {
//                                Long end = streamSizeMap.get(shortId);
//                                String timeSection = " (" +makeTimeByStreamSize(start, 256L)
//                                        + "-" + makeTimeByStreamSize(end, 256L) + ")";
//                                sb.append(timeSection);
//                                sb.append("\n");
//
//                            }
//                            log.info("=========================================");
//                            log.info(sb.toString());
//                            log.info("=========================================");
//                        }
//                    } while (rs == null || rs.getRsltStatus() == 2);
//
//                } catch (TException e) {
//                    log.error(e.getMessage());
//                }
            })).start();
        } else {
            streamSizeMap.put(shortId, streamSizeMap.get(shortId) + bytes.length);
            asrClient.ASR_audio_write(byteBuffer, 2);
        }

        fos.write(bytes, 0, bytes.length);
        fos.flush();

        log.info("Client put:" + bytes.length + Arrays.toString(bytes));
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

    private static String makeTimeByStreamSize(Long streamSize, Long bits) {
        long secondSum = (streamSize/(bits/8)/1000);
        int hour = (int) (secondSum/3600);
        secondSum %= 3600;
        int min = (int) (secondSum/60);
        int second = (int) (secondSum%60);

        LocalTime time = LocalTime.of(hour, min, second);
        return time.toString();
    }

    public static void main(String[] args) {
        System.out.println(makeTimeByStreamSize(1851392L, 256L));

    }
}
