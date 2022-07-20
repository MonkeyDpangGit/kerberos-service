package com.cgs.kerberos.server;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cgs.kerberos.bean.SecondRequest;
import com.cgs.kerberos.bean.SecondResponse;
import com.cgs.kerberos.exception.KerberosException;
import com.cgs.kerberos.handle.BaseTgsProcessor;
import com.cgs.kerberos.handle.TgsProcessor;

public class TGSHandler extends BaseHandler implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(TGTHandler.class);

    private TgsProcessor tgsProcessor;

    public void setTgsProcessor(TgsProcessor tgsProcessor) {
        this.tgsProcessor = tgsProcessor;
    }

    public TGSHandler(Socket socket) {
        super(socket);
        tgsProcessor = new BaseTgsProcessor();
    }

    @Override
    public void run() {
        byte[] bytes = new byte[1024 * 10];

        try {
            ois.read(bytes);
            SecondRequest obj = (SecondRequest) serializer.byte2Object(bytes);
            String clientIp = socket.getInetAddress().toString();
            obj.getRequestInformation().setIp(clientIp);
            SecondResponse responseBody = tgsProcessor.check(obj);

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
