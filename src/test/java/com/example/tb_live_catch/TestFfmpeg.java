package com.example.tb_live_catch;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class TestFfmpeg {

    public static void main(String[] args) throws IOException, InterruptedException {
        String mp4File = "C:\\Users\\Zzz\\Desktop\\test2.mp4";
        String command = "D:\\ffmpeg\\bin\\ffmpeg -re -y -i http://liveng.alicdn.com/mediaplatform/b5060d8a-e3ae-4304-a0e2-d0bd36f66489_liveng-270p.flv?auth_key=1580226605-0-0-b5660eae668821be0e792a8d3ea99176" +
                " -vn -ar 8000 -ac 1 -ab 64k -acodec pcm_alaw -f wav C:\\Users\\Zzz\\Desktop\\test2.wav";
//        String command = "D:\\ffmpeg\\bin\\ffmpeg -i http://liveng.alicdn.com/mediaplatform/b5060d8a-e3ae-4304-a0e2-d0bd36f66489_liveng-270p.flv?auth_key=1580226605-0-0-b5660eae668821be0e792a8d3ea99176
//        -c copy C:\\Users\\Zzz\\Desktop\\test123.flv";
        Process process = Runtime.getRuntime().exec(command);
        readVoice("C:\\Users\\Zzz\\Desktop\\test2.wav");
        dealStream(process);
        process.waitFor();
    }

    private static void readVoice(String tmpFilePath) {
        (new Thread() {
            @Override
            public void run() {
                try {
                    File file = null;
                    FileInputStream fis = null;
                    do {
                        try {
                            file = new File(tmpFilePath);
                            fis = new FileInputStream(file);
                        } catch (Exception e) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                // nothing to do..
                            }
                        }
                    }while (file == null || fis == null);

                    byte[] bytes = new byte[1024];

                    int len = 0;
                    boolean flag = true;
                    int noneCnt = 0;
                    while (flag) {
                        len = fis.read(bytes);
                        if (len == -1) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                // nothing to do..
                            }
                            if (++noneCnt > 10) {
                                break;
                            }
                        }
                        log.info("readLen:" + len);
                        noneCnt = 0;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void dealStream(Process process) {
        if (process == null) {
            return;
        }
        // 处理InputStream的线程
        new Thread() {
            @Override
            public void run() {
                InputStream is = null;
                try {
                    is = process.getInputStream();
                    FileOutputStream fos =new FileOutputStream("C:\\Users\\Zzz\\Desktop\\test2.mp4");
                    byte[] bytes = new byte[1024];
                    int len = 0;
                    String line = null;
                    while ((len = is.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);
                        log.info("read: " +len);
                    }
                } catch (IOException e) {
                   log.info(e.getMessage());
                } finally {
                    try {
                        is.close();
                    } catch (IOException e) {
                        log.info(e.getMessage());
                    }
                }
            }
        }.start();
        // 处理ErrorStream的线程
        new Thread() {
            @Override
            public void run() {
                BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line = null;
                try {
                    while ((line = err.readLine()) != null) {
                        log.info("err: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        err.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
