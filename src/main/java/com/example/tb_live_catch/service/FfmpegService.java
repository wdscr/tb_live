package com.example.tb_live_catch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
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

    public void liveTransform(String sourceUrl, String udpAddress) {
        StringBuilder command = new StringBuilder();
        command.append(ffmpegPath);
        command.append("ffmpeg -y -i ");
        command.append(sourceUrl);
        command.append(" -vn -ar 16000 -ac 1 -f wav ");
        command.append(udpAddress);
        execCommand(command.toString());
    }

    public void execCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            new Thread(new StreamDump(process.getErrorStream()), "error stream").start();
            new Thread(new StreamDump(process.getInputStream()), "output stream").start();
            process.waitFor();
            log.info("Exit value: " + process.exitValue());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

@Slf4j
class StreamDump implements Runnable {

    private InputStream stream;
    StreamDump(InputStream input) {
        this.stream = input;
    }

    public void run() {
        try {
            int c;
            while ((c = stream.read()) != -1) {
                log.info("读取{}字节...", c);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
