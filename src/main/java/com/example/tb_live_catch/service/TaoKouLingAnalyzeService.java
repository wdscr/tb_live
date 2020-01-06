package com.example.tb_live_catch.service;

import java.io.IOException;

public interface TaoKouLingAnalyzeService {

    /**
     * 通过淘口令解析直播id
     * @param tkl
     * @return
     */
    String getSourceUrl(String tkl);


}
