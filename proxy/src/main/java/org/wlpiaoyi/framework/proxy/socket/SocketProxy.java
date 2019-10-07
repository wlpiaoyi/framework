package org.wlpiaoyi.framework.proxy.socket;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class SocketProxy extends Thread implements SocketThread.SocketThreadInterface {

    private static final Map<Integer, SocketProxy> servers = new HashMap<>();

    private final Set<SocketThread> clients = new HashSet<>();

    private final int listenPort;
    private final boolean isEncryption;

    private Proxy proxy;
    private ServerSocket serverSocket;

    public SocketProxy(boolean isEncryption, int listenPort){
        this.isEncryption = isEncryption;
        this.listenPort = listenPort;
        this.proxy = null;
    }

    public void run(){
        try{
            System.out.println(this.listenPort);
            this.serverSocket = new ServerSocket(this.listenPort);
            while (this.serverSocket.isClosed() == false) {
                try {
                    Socket socket = serverSocket.accept();
                    SocketThread socketThread;
                    if(this.proxy == null){
                        socketThread =new SocketThread(socket, this.isEncryption);
                    }else{
                        socketThread = new SocketThread(socket, this.proxy, this.isEncryption);
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
    public void socketStart(SocketThread socketThread) {
        synchronized (this.clients){
            this.clients.add(socketThread);
        }
        System.out.println("=======>"+this.clients.size());
    }

    @Override
    public void socketHandle(SocketThread socketThread, byte[] buffer, int len) {

    }

    @Override
    public void socketData(SocketThread socketThread, byte[] buffer, int len) {

    }

    @Override
    public void socketConnected(SocketThread socketThread, String requestHost, int requestPort) {

    }

    @Override
    public void socketEnd(SocketThread socketThread) {
        synchronized (this.clients){
            this.clients.remove(socketThread);
        }
        System.out.println("<======="+this.clients.size());
    }

    @Override
    public void socketErro(SocketThread socketThread, Exception e) {
        e.printStackTrace();
    }


    public int getListenPort() {
        return listenPort;
    }

    public boolean isEncryption() {
        return isEncryption;
    }


    public Proxy getProxy() {
        return proxy;
    }

    public void clearProxy() {
        proxy = null;System.out.println("clear proxy");
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
        Scanner scanner = new Scanner(System.in);
        while (true){
            String line = scanner.nextLine();
            try{
                String[] option = line.split(" ");
                if(option[0].equals("start")){
                    boolean isEncryption = new Boolean(option[1]).booleanValue();
                    int port = new Integer(option[2]).intValue();
                    SocketProxy socketProxy = new SocketProxy(isEncryption, port);
                    if(option.length == 5){
                        String proxyIP = option[3];
                        int proxyPort = new  Integer(option[4]).intValue();
                        socketProxy.setProxy(proxyIP, proxyPort);
                    }
                    socketProxy.start();
                    System.out.println("serer hash:"+socketProxy.hashCode());
                    new Thread(socketProxy).start();
                }else if(option[0].equals("close")){
                    int hash = new Integer(option[1]);
                    SocketProxy.remove(hash);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
