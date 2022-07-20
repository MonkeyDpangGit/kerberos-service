package com.cgs.client;

import com.cgs.kerberos.client.KerberosClient;
import com.cgs.kerberos.client.KerberosClientServerImpl;
import com.cgs.kerberos.client.KerberosSocketClient;
import com.cgs.kerberos.client.bean.FirstResponseWrapper;
import com.cgs.kerberos.client.bean.SecondResponseWrapper;
import com.cgs.kerberos.client.handle.FileClientDatabaseProcessor;
import com.cgs.kerberos.util.KryoSerializer;

public class KerberosSocketClientTest {

    private static String REMOTE_HOST = "127.0.0.1";
    private static Integer TGS_PORT = 8906;
    private static Integer ST_PORT = 8907;

    private static String TARGET_SERVER_NAME = "account-self";
    private static String TARGET_IP = "172.18.110.3";

    public static void main(String[] args) {

        // 第一次请求，请求认证并获取TGT
        KerberosClient k = new KerberosSocketClient(REMOTE_HOST, TGS_PORT, ST_PORT);
        FirstResponseWrapper f = k.getTgt();
        System.out.println(f);
        System.out.println(f.getTgt().length);

        // 第二次请求，请求验证TGT并获取ST
        SecondResponseWrapper s = k.getSt(f, TARGET_SERVER_NAME, TARGET_IP);
        System.out.println(s);
        System.out.println(s.getSt().length);

        // 第三次请求，请求验证ST
        KerberosClientServerImpl kcs = new KerberosClientServerImpl();
        kcs.setCdp(new FileClientDatabaseProcessor());
        kcs.setSerializer(new KryoSerializer());
        byte[] bytes = kcs.getThirdRequestByte(s);

        // 被请求方验证ST
        byte[] responseByte = kcs.checkServiceTicket(bytes);
        boolean result = kcs.checkServiceResponse(responseByte, s);
        System.out.println(result);
    }
}
