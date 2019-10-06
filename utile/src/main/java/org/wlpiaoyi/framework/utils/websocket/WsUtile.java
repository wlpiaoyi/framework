package org.wlpiaoyi.framework.utils.websocket;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WsUtile {

    public static final int UUID32_LENGHT = 32;
    public static final int UUID64_LENGHT = 64;

    /**
     * 获得指定数目的UUID
     * @param number int 需要获得的UUID数量
     * @return String[] UUID数组
     */
    public static String[] getUUID32S(int number){
        if(number < 1){
            return null;
        }
        StringBuilder uuid = new StringBuilder();
        String[] retArray = new String[number];
        for(int i=0;i<number;i++){
            retArray[i] = WsUtile.getUUID32();
        }
        return retArray;
    }

    /**
     * 获得指定数目的UUID
     * @param number int 需要获得的UUID数量
     * @return String[] UUID数组
     */
    public static String[] getUUID64S(int number){
        if(number < 1){
            return null;
        }
        String[] retArray = new String[number];
        for(int i=0;i<number;i++){
            retArray[i] = WsUtile.getUUID64();
        }
        return retArray;
    }


    /**
     * 获得一个UUID
     * @return String UUID
     */
    public static String getUUID64(){
        String uuid32_1 = WsUtile.getUUID32();
        String uuid32_2 = WsUtile.getUUID32();
        return uuid32_1 + uuid32_2;
    }

    /**
     * 获得一个UUID
     * @return String UUID
     */
    public static String getUUID32(){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid;
    }

    public static void setDownLatchForMapData(CountDownLatch downLatch, Map<String, Object> data){
        data.put("downLatch", downLatch);
    }
    public static CountDownLatch getDownLatchForMapData(Map<String, Object> data){
        Object downLatch = data.get("downLatch");
        if(downLatch == null) return null;
        return (CountDownLatch) downLatch;
    }


    public static void setResultForMapData(String result, Map<String, Object> data){
        data.put("result", result);
    }
    public static String getRresultForMapData(Map<String, Object> data){
        Object result = data.get("result");
        if(result == null) return null;
        return (String) result;
    }


    public interface SendASyncMessageLitsener{
        void ssmlSendASyncMessage(@NonNull String message);
        Map<String, Map<String, Object>> getSsmlResultMap();
        WebSocketListener getSsmlwsAbstract();
        Object getSsmlLcok();
        int getSsmlTimeoutSeconds();
    }

    /**
     * 实现同步线程安全主动推送
     * @param uuid
     * @param message
     * @param sendASyncMessageLitsener
     * @return
     */
    public static String ssmlSendSyncMessage(@NonNull String uuid, @NonNull String message, @NonNull SendASyncMessageLitsener sendASyncMessageLitsener){

        String sendArg = uuid + ":" + message;
        CountDownLatch downLatch = new CountDownLatch(1);
        Map<String, Object> data = new HashMap<>();
        Map<String, Map<String, Object>> resultMap = sendASyncMessageLitsener.getSsmlResultMap();
        int timeOutSeconds = sendASyncMessageLitsener.getSsmlTimeoutSeconds();
        Object lock = sendASyncMessageLitsener.getSsmlLcok();
        try {
            WsUtile.setDownLatchForMapData(downLatch, data);
            sendASyncMessageLitsener.ssmlSendASyncMessage(sendArg);
            resultMap.put(uuid, data);
            if(!downLatch.await(timeOutSeconds, TimeUnit.MILLISECONDS)){
                data = null;
            }
        } catch (Exception e) {
            data = null;
            e.printStackTrace();
        } finally {
            synchronized (lock){
                resultMap.remove(uuid);
            }
        }

        if(data == null) return null;
        String result = (String)data.get("result");
        return result;
    }

    /**
     * 同步和异步数据返回的处理
     * @param message
     * @param target
     * @param sendASyncMessageLitsener
     */
    public static void onMessage(@NonNull String message, @NonNull Object target,
                                 @NonNull SendASyncMessageLitsener sendASyncMessageLitsener) {
        Map<String, Map<String, Object>> resultMap = sendASyncMessageLitsener.getSsmlResultMap();
        WebSocketListener wsAbstract = sendASyncMessageLitsener.getSsmlwsAbstract();
        Object lock = sendASyncMessageLitsener.getSsmlLcok();
        StringBuilder uuid = new StringBuilder();
        int index = 0;
        try{
            try{
                for(char c : message.toCharArray()){
                    if(index > WsUtile.UUID64_LENGHT + 1){
                        index = 0;
                    }
                    if(c == ':') break;
                    uuid.append(c);
                    index ++;
                }
            }catch (Exception e){e.printStackTrace();}
            if(index > 0){
                String result = message.substring(index + 1);
                message = result;
                CountDownLatch downLatch = null;
                synchronized (lock){
                    Map<String, Object> data = resultMap.get(uuid.toString());
                    if(data != null){
                        WsUtile.setResultForMapData(result, data);
                        downLatch = WsUtile.getDownLatchForMapData(data);
                    }
                }
                if(downLatch != null){downLatch.countDown();}
            }
        }catch (Exception e){e.printStackTrace();}
        if (wsAbstract != null){
            try{
                if(index == 0){
                    wsAbstract.onMessage(target, message);
                }else{
                    wsAbstract.onMessage(target, message, uuid.toString());
                }
            }catch (Exception e){e.printStackTrace();}
        }
    }
}
