package com.example.tb_live_catch;

import com.example.tb_live_catch.service.LiveCatchService;
import com.example.tb_live_catch.service.TaoKouLingAnalyzeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class TestBorad {

    @Autowired
    LiveCatchService liveUriCatchService;

    @Autowired
    TaoKouLingAnalyzeService taoKouLingAnalyzeService;

    @Test
    public void test() {
        try {
            String sign = liveUriCatchService.getLiveUri("248941792164");
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
    public void test3() throws IOException {
        System.out.println(taoKouLingAnalyzeService.analyzeToLiveId("￥1bbP10tNu5g￥"));
    }

}
