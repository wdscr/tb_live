package com.example.tb_live_catch.service;



import com.example.tb_live_catch.thrift.asr.ASRServ;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class ASRService {

    @Value("${asr.host}")
    private String host;

    @Value("${asr.port}")
    private Integer port;

    @Value("${asr.username}")
    private String username;

    @Value("${asr.password}")
    private String password;

    private static final int SOCKET_TIME_OUT = 10000;

    public ASRServ.Client create() throws TException {
        TTransport transport  = new TSocket(host, port, SOCKET_TIME_OUT);
        TProtocol protocol = new TBinaryProtocol(transport);
        ASRServ.Client client = new ASRServ.Client(protocol);
        transport.open();
        client.asr_init(username, password, "");
        client.ASR_create_session("sub=iat,domain=iat,language=zh_cn," +
                "accent=mandarin,sample_rate=16000," +
                "result_type=plain,result_encoding=utf8,vad_eos=10000", null);
        return client;
    }
}
