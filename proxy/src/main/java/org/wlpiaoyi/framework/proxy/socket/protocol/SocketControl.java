package org.wlpiaoyi.framework.proxy.socket.protocol;

import org.wlpiaoyi.framework.proxy.socket.SocketThread;
import org.wlpiaoyi.framework.proxy.utils.Utils;

import java.io.InputStream;
import java.io.OutputStream;

public interface SocketControl {

    void controlBefore(SocketThread socketThread, byte type, InputStream isIn, OutputStream osIn);

    interface VerifyProxyType extends SocketControl{
        /**
         * judge handle type
         * @param socketThread
         * @param buffer
         * @return
         */
        Utils.SocketProxyType verifyProxyType(SocketThread socketThread, byte[] buffer);
    }

    interface VerifyEncryption extends SocketControl{
        /**
         * verify name and password
         * @param name
         * @param nameL
         * @param password
         * @param passwordL
         * @return
         */
        boolean verifyEncryption(SocketThread socketThread, byte[] name,int nameL, byte[] password, int passwordL);
    }


    interface ResponseAddress extends SocketControl{
        /**
         *
         * @param buffer
         * @param len
         * @return
         */
        String responseDomain(SocketThread socketThread, byte[] buffer, int len, Utils.SocketProxyType proxyType);

        /**
         *
         * @param buffer
         * @param len
         * @return
         */
        int responsePort(SocketThread socketThread, byte[] buffer,int len);
    }


}
