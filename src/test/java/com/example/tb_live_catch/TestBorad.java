package com.example.tb_live_catch;

import com.example.tb_live_catch.service.TBWordAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class TestBorad {

    @Autowired
    TBWordAnalysisService tbWordAnalysisService;

    @Test
    public void test() {
        try {
            String sign = tbWordAnalysisService.tbSign();
            System.out.println(sign);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
