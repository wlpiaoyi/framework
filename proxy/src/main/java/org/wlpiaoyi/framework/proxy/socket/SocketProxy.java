package org.wlpiaoyi.framework.proxy.socket;


import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.proxy.stream.SocketThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
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
public class SocketProxy extends Thread implements SocketThread.SocketThreadInterface {

    private static final Map<Integer, SocketProxy> servers = new HashMap<>();

    private final Set<SocketThread> clients = new HashSet<>();

    private final int listenPort;

    private Proxy proxy;
    private ServerSocket serverSocket;

    public SocketProxy(int listenPort){
        this.listenPort = listenPort;
        this.proxy = null;
    }

    public void run(){
        this.synStart();
    }

    public void synStart(){
        try{
            System.out.println(this.listenPort);
            this.serverSocket = new ServerSocket(this.listenPort);
            while (this.serverSocket.isClosed() == false) {
                try {
                    Socket socket = serverSocket.accept();
                    SocketThread socketThread;
                    if(this.proxy == null){
                        socketThread =new SocketThread(socket);
                    }else{
                        socketThread = new SocketThread(socket, this.proxy);
                    }
                    socketThread.setSocketInterface(this);
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
        SocketProxy.servers.put(listenPort, this);
    }


    public void start(){
        SocketProxy.servers.put(listenPort, this);
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
        log.info("socket ip:{} port:{} come in count ======>{}", socketThread.getRequestDomain(), socketThread.getRequestPort(), this.clients.size());
        return true;
    }


    @Override
    public boolean socketConnect(SocketThread socketThread) {
        log.info("socket ip:{} port:{} connect to domain:{} port:{} ", socketThread.getRequestDomain(), socketThread.getRequestPort(), socketThread.getResponseDomain(), socketThread.getResponsePort());
        return true;
    }

    @Override
    public void socketClose(SocketThread socketThread) {
        synchronized (this.clients){
            this.clients.remove(socketThread);
        }
        log.info("socket ip:{} port:{} come out count <======{}", socketThread.getRequestDomain(), socketThread.getRequestPort(), this.clients.size());
    }

    @Override
    public void socketException(SocketThread socketThread, Exception e) {
        e.printStackTrace();
        log.info("socket ip:{} port:{} exception domain:{} port:{} ", socketThread.getRequestDomain(), socketThread.getRequestPort(), socketThread.getResponseDomain(), socketThread.getResponsePort());
    }


    public int getListenPort() {
        return listenPort;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void clearProxy() {
        proxy = null;
    }

    public void setProxy(String proxyIP,int proxyPort) {
        this.proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyIP, proxyPort));
        System.out.println("set proxyHost:" + ((InetSocketAddress)proxy.address()).getHostName() + "porxyProt:"+ ((InetSocketAddress)proxy.address()).getPort());
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
    public static void main(String[] args) {
        SocketProxy socketProxy = new SocketProxy(8010);
        socketProxy.synStart();
    }
}
