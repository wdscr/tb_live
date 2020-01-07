package com.example.tb_live_catch;

import com.example.tb_live_catch.service.LiveTransformService;
import com.example.tb_live_catch.service.LiveUrlCatchService;
import com.example.tb_live_catch.service.TaoKouLingAnalyzeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

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

}
