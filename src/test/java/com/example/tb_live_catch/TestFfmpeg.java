package com.example.tb_live_catch;

import com.example.tb_live_catch.service.ASRService;
import com.example.tb_live_catch.thrift.asr.ASRResult;
import com.example.tb_live_catch.thrift.asr.ASRServ;
import com.example.tb_live_catch.thrift.asr.ASRWrite;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
public class TestFfmpeg {

    public static void main(String[] args) throws IOException, InterruptedException {
        String mp4File = "C:\\Users\\Zzz\\Desktop\\test2.mp4";
//        String command = "D:\\ffmpeg\\bin\\ffmpeg -y -i https://cloud.video.taobao.com/play/u/1759494485/p/1/e/6/t/1/d/ld/242334423281.mp4" +
//                " -vn -ar 16000 -ac 1 -ab 128k -acodec pcm_alaw -f wav udp://127.0.0.1:9999?tag=hello";
        String command = "D:\\ffmpeg\\bin\\ffmpeg -y -i https://cloud.video.taobao.com/play/u/1759494485/p/1/e/6/t/1/d/ld/242334423281.mp4" +
                " -vn -ar 16000 -ac 1 -f wav udp://127.0.0.1:9999?tag=hello";
//        String command = "D:\\ffmpeg\\bin\\ffmpeg -i http://liveng.alicdn.com/mediaplatform/b5060d8a-e3ae-4304-a0e2-d0bd36f66489_liveng-270p.flv?auth_key=1580226605-0-0-b5660eae668821be0e792a8d3ea99176
//        -c copy C:\\Users\\Zzz\\Desktop\\test123.flv";
        Process process = Runtime.getRuntime().exec(command);
        new Thread(new StreamDump(process.getErrorStream()), "error stream").start();
        new Thread(new StreamDump(process.getInputStream()), "output stream").start();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Exit value: " + process.exitValue());
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



    @Test
    public void initTest() throws IOException {

        try {
            ASRServ.Client client;

            TTransport transport = null;
            TProtocol protocol = null;
            transport = new TSocket("120.77.147.194", 9091, 3000);
            protocol = new TBinaryProtocol(transport);
            client = new ASRServ.Client(protocol);
            transport.open();
            client.asr_init("aiyin_zhijian", "aiyin_zhijian123456#", "");
            String sessionId = UUID.randomUUID().toString();
            client.ASR_create_session("sub=iat,domain=iat,language=zh_cn," +
                    "accent=mandarin,sample_rate=16000," +
                    "result_type=plain,result_encoding=utf8,vad_eos=10000", sessionId);
            File file = new File("C:\\Users\\Zzz\\Desktop\\3b3f2f83.wav");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            while ((len = fis.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            ByteBuffer bb = ByteBuffer.wrap(baos.toByteArray());
            ASRWrite wrs = client.ASR_audio_write(bb, 4);
            StringBuilder sb = new StringBuilder();
            ASRResult rs = client.ASR_get_result();
            if (rs.isSetText()) sb.append(rs.getText());
            while (rs.getRsltStatus() == 0 || rs.getRsltStatus() == 2) {
                Thread.sleep(1000);
                rs = client.ASR_get_result();
            }
            if (rs.isSetText()) sb.append(rs.getText());
            System.out.println("text:" + sb.toString());
        } catch (TException | FileNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class StreamDump implements Runnable {

    private InputStream stream;

    StreamDump(InputStream input) {
        this.stream = input;
    }

    public void run() {
        try {
            int c;
            while ((c = stream.read()) != -1) {
                System.out.write(c);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
