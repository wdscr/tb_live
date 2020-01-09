package com.example.tb_live_catch.component.netty.tcp;

import com.alibaba.fastjson.JSONObject;
import com.example.tb_live_catch.service.ASRService;
import com.example.tb_live_catch.thrift.asr.ASRResult;
import com.example.tb_live_catch.thrift.asr.ASRServ;
import com.example.tb_live_catch.thrift.asr.ASRWrite;
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

    private static String HOME_PATH;

    static {
        HOME_PATH = System.getProperty("user.home");
        log.info("USER HOME PATH:" + HOME_PATH);
    }

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
        String shortId = ctx.channel().id().asShortText();
        ASRServ.Client client = ASRClinetMap.get(shortId);
        client.ASR_audio_write(ByteBuffer.wrap(new byte[]{0}), 4);
        logResult(client);
        fileMap.get(shortId).close();

        log.info(ctx.channel().id().asShortText() + " inActive");
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
            System.out.print("-");
//
//            bytes = Arrays.copyOfRange(bytes, 44, bytes.length);
//            byteBuffer = ByteBuffer.wrap(bytes);
            fos = new FileOutputStream(HOME_PATH + "\\Desktop\\test.wav");
            fileMap.put(shortId, fos);
            asrClient = asrService.create();
            ASRClinetMap.put(shortId, asrClient);
            streamSizeMap.put(shortId, Long.valueOf(bytes.length));
            asrClient.ASR_audio_write(byteBuffer, 1);

            ASRServ.Client finalAsrClient = asrClient;
//            (new Thread(() -> {
//                try {
//                    Thread.sleep(5000);
//                    log.info("开启get线程...");
//
//                    logResult(finalAsrClient);
//                } catch (TException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//            })).start();
        } else {
            streamSizeMap.put(shortId, streamSizeMap.get(shortId) + bytes.length);
            ASRWrite write = asrClient.ASR_audio_write(byteBuffer, 2);
            switch (write.getEpStatus()) {
                case 0 : /*log.info("还没有检测到音频的前端点。");*/
                    break;
                case 1 : /*log.info("已经检测到了音频前端点，正在进行正常的音频处理。");*/
                    break;
                case 3 : log.info("检测到音频的后端点，后继的音频会被忽略。");
                    logResult(asrClient);
                    asrClient = asrService.create();
                    asrClient.ASR_audio_write(byteBuffer, 1);
                    /*log.info("新建client...");*/
                    ASRClinetMap.put(shortId, asrClient);
                    break;
                case 4 : log.info("超时。");
                    break;
                case 5 : log.info("出现错误。");
                    break;
                case 6 : log.info("音频过大。");
                    break;

            }
            for (int i=0; i<5; i++) {
                ASRResult rs = asrClient.ASR_get_result();
                if (rs != null && StringUtils.isNotBlank(rs.getText())) {
                    log.info(rs.getText());
                }
            }
        }

        fos.write(bytes, 0, bytes.length);
        fos.flush();

//        log.info("Client put:" + bytes.length + Arrays.toString(bytes));
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

    private void logResult(ASRServ.Client asrClient) throws TException {
        ASRResult rs = null;
        int getTime = 15;
        while (getTime-- > 0) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rs = asrClient.ASR_get_result();
            if (rs != null && StringUtils.isNoneBlank(rs.getText())) {
                log.info(rs.getText());
            }
        }
//        do {
//            rs = asrClient.ASR_get_result();
//            if (rs != null && StringUtils.isNoneBlank(rs.getText())) {
//
//                log.info(rs.getText());
//
//            }
//        } while (rs == null || rs.getRsltStatus() == 2 || rs.getRsltStatus() == 0 || rs.getErrorCode() != 0);
//        rs = asrClient.ASR_get_result();
//        log.info(rs.getText());
    }
}
