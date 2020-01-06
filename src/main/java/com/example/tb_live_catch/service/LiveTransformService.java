package com.example.tb_live_catch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LiveTransformService {

    @Value("${udp.host:127.0.0.1}")
    private String udpHost;

    @Value("${udp.port:8888}")
    private Integer udpPort;

    @Autowired
    FfmpegService ffmpegService;

    @Autowired
    TaoKouLingAnalyzeService taoKouLingAnalyzeService;

    Map<String, String> jobMap = null;

    public void transform(String tkl) {
        String sourceUrl = taoKouLingAnalyzeService.getSourceUrl(tkl);
        String udpAddress = udpHost + udpPort;
        ffmpegService.liveTransform(sourceUrl, tkl);

    }


}
