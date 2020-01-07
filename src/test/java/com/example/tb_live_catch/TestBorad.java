package com.example.tb_live_catch;

import com.alibaba.fastjson.JSONObject;
import com.example.tb_live_catch.service.ASRService;
import com.example.tb_live_catch.service.LiveTransformService;
import com.example.tb_live_catch.service.LiveUrlCatchService;
import com.example.tb_live_catch.service.TaoKouLingAnalyzeService;
import com.example.tb_live_catch.thrift.asr.ASRResult;
import com.example.tb_live_catch.thrift.asr.ASRServ;
import com.example.tb_live_catch.thrift.asr.ASRWrite;
import io.netty.handler.codec.sctp.SctpOutboundByteStreamHandler;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

@SpringBootTest
public class TestBorad {

    @Autowired
    LiveUrlCatchService liveUriCatchService;

    @Autowired
    TaoKouLingAnalyzeService taoKouLingAnalyzeService;

    @Autowired
    LiveTransformService liveTransformService;


    @Test
    public void test() {
        try {
            String sign = liveUriCatchService.getLiveUri("250041692807");
            System.out.println(sign);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2() throws IOException {
        liveUriCatchService.liveCatch("http://liveng.alicdn.com/mediaplatform/412df181-b657-411a-8dc9-bc7f296be4a3_liveng-270p.flv?auth_key=1579967761-0-0-df760a24acd79dd274a13e8efb9aeeed");
    }

    @Test
    public void test3() {
        System.out.println(taoKouLingAnalyzeService.getSourceUrl("￥0payYBN6E71￥"));
    }


    @Autowired
    ASRService asrService;


    @Test
    public void testWhole() throws IOException, TException {
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\Xxx\\Desktop\\test.wav"));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();



        ASRServ.Client client = asrService.create();

        byte[] bytes = new byte[4096];
        int len = 0;
        boolean flag = false;
//        while ((len = fis.read(bytes)) != -1) {
//            if (!flag) {
//                bytes = Arrays.copyOfRange(bytes, 44, len);
//                len -= 44;
//                flag = true;
//            }
//            baos.write(bytes, 0, len);
//        }
//        client.ASR_audio_write(ByteBuffer.wrap(baos.toByteArray()), 4);
        while ((len = fis.read(bytes)) != -1) {
            ASRWrite write = client.ASR_audio_write(ByteBuffer.wrap(bytes), flag?2:1);
//            System.out.println(JSONObject.toJSONString(write));

            if (write.getEpStatus() == 3) {
                ASRResult rs = null;
                do {
                    rs = client.ASR_get_result();
                    if (rs != null && StringUtils.isNotBlank(rs.getText())) {
                        System.out.println(rs.getText());
                    }
                } while (rs == null || rs.getRsltStatus() == 2 || rs.getRsltStatus() == 0 || rs.getErrorCode() != 0);
                client = asrService.create();
                flag = false;
                continue;
            }
            flag = true;
        }


        ASRResult rs = null;
        do {
            rs = client.ASR_get_result();
            if (rs != null && StringUtils.isNotBlank(rs.getText())) {
                System.out.println(rs.getText());
            }
        } while (rs != null && (rs.getRsltStatus() == 2 || rs.getRsltStatus() == 0) && rs.getErrorCode() == 0);



    }

}
