package com.cgs.kerberos.server;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgs.kerberos.bean.FirstRequest;
import com.cgs.kerberos.bean.FirstResponse;
import com.cgs.kerberos.exception.KerberosException;
import com.cgs.kerberos.handle.TgtProcessor;

/**
 * TGS 请求处理器
 */
public class TGTHandler extends BaseHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(TGTHandler.class);

    private TgtProcessor tgtProcessor;

    public void setTgtProcessor(TgtProcessor tgtProcessor) {
        this.tgtProcessor = tgtProcessor;
    }

    public TGTHandler(Socket socket) {
        super(socket);
//		tgtProcessor=new BaseTgtProcessor();
    }

    @Override
    public void run() {
        byte[] bytes = new byte[1024 * 10];

        try {
            ois.read(bytes);
            FirstRequest obj = (FirstRequest) serializer.byte2Object(bytes);
            String ip = socket.getInetAddress().toString();
            obj.setIp(ip);
            FirstResponse responseBody = tgtProcessor.check(obj);

            writeResponse(responseBody);
        } catch (KerberosException e) {
            logger.debug(e.getMessage(), e);
            writeResponse(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                ois.close();
                socket.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
