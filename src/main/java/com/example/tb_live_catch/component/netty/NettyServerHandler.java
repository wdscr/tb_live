//package com.example.tb_live_catch.component.netty;
//
//import com.example.tb_live_catch.service.ASRService;
//import com.example.tb_live_catch.thrift.asr.ASRResult;
//import com.example.tb_live_catch.thrift.asr.ASRServ;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.socket.DatagramPacket;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.io.FileOutputStream;
//import java.nio.ByteBuffer;
//import java.util.Arrays;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//@Slf4j
//public class NettyServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {
//
//    private static Map<String, FileOutputStream> channelMap = new ConcurrentHashMap<>();
//
//    private static Map<String, StringBuilder> channelContent = new ConcurrentHashMap<>();
//    private static Map<String, ASRServ.Client> ASRClientMap = new ConcurrentHashMap<>();
//
//    private static Map<String, Long> channelTimeOutMap = new ConcurrentHashMap<>();
//
//    ASRService asrService;
//
//     public NettyServerHandler(ASRService asrService) {
//         this.asrService = asrService;
//     }
//
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        log.info("channel active");
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        log.info("channel inactive");
//    }
//
//    /**
//     * 过期时间 一小时
//     */
//    private static final int TIMEOUT= 20000;
//
//    @PostConstruct
//    public void cleanExpireChannel() {
//    }
//
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) throws Exception {
//        String shortText = ctx.channel().id().asShortText();
//
////        ByteBuf byteBuf = datagramPacket.content();
////        ByteBuffer byteBuffer = ByteBuffer.allocate(byteBuf.readableBytes());
////        byteBuf.readBytes(byteBuffer);
////        log.info("收到信息..." + byteBuffer.array().length + " " + Arrays.toString(byteBuffer.array()));
////
////        StringBuilder sb = channelContent.get(shortText);
////        if (sb == null) {
////            sb = new StringBuilder();
////            channelContent.put(shortText, sb);
////        }
////
////        ASRServ.Client asrClient = ASRClientMap.get(shortText);
////        if (asrClient == null) {
////            asrClient = asrService.create();
////            asrClient.ASR_audio_write(byteBuffer, 1);
////        } else {
////            asrClient.ASR_audio_write(byteBuffer, 2);
////        }
////        ASRResult rs = asrClient.ASR_get_result();
////        if (rs.isSetText()) {
////            sb.append(rs.getText());
////        }
////
////        log.info(sb.toString());
//
//        FileOutputStream fos = channelMap.get(shortText);
//        if (fos == null) {
//            fos = new FileOutputStream("C:\\Users\\Zzz\\Desktop\\" + shortText + ".wav");
//            channelMap.put(shortText, fos);
//            channelTimeOutMap.put(shortText, System.currentTimeMillis());
//        }
//        ByteBuf byteBuf = datagramPacket.content();
//        ByteBuffer byteBuffer = ByteBuffer.allocate(byteBuf.readableBytes());
//        byteBuf.readBytes(byteBuffer);
//        fos.write(byteBuffer.array());
//        log.info("收到信息..." + byteBuffer.array().length + " " + Arrays.toString(byteBuffer.array()));
//    }
//}
