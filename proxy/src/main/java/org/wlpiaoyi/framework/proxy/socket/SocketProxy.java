package org.wlpiaoyi.framework.proxy.socket;


import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.proxy.socket.protocol.SocketCourse;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.IOException;
import java.net.*;
import java.util.*;

//nohup java -jar proxy.socket.jar > proxy.socket.temp.log 2>&1 &
/*
vpnsetup_centos.sh
wget https://git.io/vpnsetup -O vpnsetup_centos.sh && sudo \
VPN_IPSEC_PSK='000000 \
VPN_USER='vpnname' \
VPN_PASSWORD='000000' \
sh vpnsetup_centos.sh;

 */
@Slf4j
public class SocketProxy implements SocketCourse {


    public static boolean hasLog = true;

    private static final Map<Integer, SocketProxy> servers = new HashMap<>();

    @Getter
    private final int listenPort;

    @Getter @Setter
    private Proxy proxy;

    private final Set<SocketThread> clients = new HashSet<>();

    private final ServerSocket serverSocket;

    private byte[][] encryptionDatas;

    public SocketProxy(int listenPort) throws IOException {
        this.listenPort = listenPort;
        this.encryptionDatas = null;
        this.serverSocket = new ServerSocket(this.listenPort);
        this.proxy = null;
    }
    public SocketProxy(int listenPort, byte[][] encryptionDatas) throws IOException {
        this.listenPort = listenPort;
        this.encryptionDatas = encryptionDatas;
        this.serverSocket = new ServerSocket(this.listenPort);
        this.proxy = null;
    }

    public void synStart(){
        try{
            SocketProxy.servers.put(listenPort, this);
            if(hasLog)log.info("server start port:{} encryption:{}", listenPort, this.encryptionDatas != null);
            while (this.serverSocket.isClosed() == false) {
                try {
                    Socket socket = serverSocket.accept();
                    SocketThread socketThread;
                    if(this.proxy == null){
                        socketThread =new SocketThread(socket, this.encryptionDatas);
                    }else{
                        socketThread = new SocketThread(socket, this.proxy, this.encryptionDatas);
                    }
                    socketThread.setSocketOperation(this);
                    socketThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            this.close();
        }
    }


    public void asynStart(){
        new Thread(() -> {
            this.synStart();
        }).start();
    }

    public void close(){
        try {
            synchronized (this.clients){
                for (SocketThread socketThread : this.clients){
                    socketThread.close();
                }
                this.clients.clear();
            }
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean socketOpen(SocketThread socketThread) {
        synchronized (this.clients){
            this.clients.add(socketThread);
        }
        if(hasLog)log.info("socket ip:{} port:{} come in count ======>{}", socketThread.getRequestDomain(), socketThread.getRequestPort(), this.clients.size());
        return true;
    }


    @Override
    public boolean socketConnect(SocketThread socketThread) {
        if(hasLog)log.info("socket ip:{} port:{} connect to domain:{} port:{} ", socketThread.getRequestDomain(), socketThread.getRequestPort(), socketThread.getResponseDomain(), socketThread.getResponsePort());
        return true;
    }

    @Override
    public void socketClose(SocketThread socketThread) {
        synchronized (this.clients){
            this.clients.remove(socketThread);
        }
        if(hasLog)log.info("socket ip:{} port:{} come out count <======{}", socketThread.getRequestDomain(), socketThread.getRequestPort(), this.clients.size());
    }

    @Override
    public void socketException(SocketThread socketThread, Exception e) {
        if(hasLog)log.error ("socket ip:" + socketThread.getRequestDomain() + "port:" + socketThread.getRequestDomain() + " exception domain:" + socketThread.getRequestPort() + " port:" + socketThread.getResponseDomain(), e);
    }

    public void setProxy(String proxyIP,int proxyPort) {
        if(!this.serverSocket.isClosed()) throw new BusinessException("can't set proxy after the socket serve was started!");
        this.proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyIP, proxyPort));
    }

    public static final Set<Map.Entry<Integer, SocketProxy>> getServers() {
        return servers.entrySet();
    }
    public static SocketProxy remove(int listenPort){
        return servers.remove(listenPort);
    }
    public static SocketProxy get(int listenPort){
        return servers.get(listenPort);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        byte[][] encryptionDatas = new byte[2][];
        String PATH = System.getProperty("user.dir") + "/proxy.json";
        String jsonStr = StringUtils.readFile(PATH);
        List<Map> configurations = new Gson().fromJson(jsonStr, List.class);
        for(Map configuration : configurations){
            int port = ((Double)configuration.get("port")).intValue();
            boolean verify = configuration.containsKey("verify") ? (boolean)configuration.get("verify") : false;
            SocketProxy socketProxy;
            if(verify){
                String name = (String) configuration.get("name");
                String password = (String) configuration.get("password");
                encryptionDatas[0] = name.getBytes("UTF-8");
                encryptionDatas[1] = password.getBytes("UTF-8");
                socketProxy = new SocketProxy(port, encryptionDatas);
            }else {
                socketProxy = new SocketProxy(port);
            }
            socketProxy.asynStart();
        }
        while (true) {
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
