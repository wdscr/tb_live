package com.example.tb_live_catch.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class FfmpegService {

    @Value("${ffmpeg.path}")
    public String ffmpegPath;

    @Value("${ffmpeg.tmp-dir}")
    public String tmpDir;

    private static String HOME_PATH;

    static {
        HOME_PATH = System.getProperty("user.home");
        log.info("USER HOME PATH:" + HOME_PATH);
    }

    public void videoToAudio(String videoUrl) {
        String   aacFile = tmpDir + "/"
                + UUID.randomUUID().toString().replaceAll("-", "") + ".aac";
    }


}
