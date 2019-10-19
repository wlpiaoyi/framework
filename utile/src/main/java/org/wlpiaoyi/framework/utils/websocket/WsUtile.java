package org.wlpiaoyi.framework.utils.websocket;

import lombok.NonNull;
import org.wlpiaoyi.framework.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class WsUtile {

    public static final String WS_SUFFIX = "::";


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

        String sendArg = uuid + WsUtile.WS_SUFFIX + message;
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
                boolean hasSuffix = false;
                char chars = WsUtile.WS_SUFFIX.charAt(0);
                for(char c : message.toCharArray()){
                    int startIndex = index;
                    int endIndex = index + WsUtile.WS_SUFFIX.length();
                    if(message.length() > endIndex && c == chars && message.substring(startIndex, endIndex).equals(WsUtile.WS_SUFFIX)){
                        hasSuffix = true;
                        break;
                    }
                    uuid.append(c);
                    if(index > StringUtils.UUID64_LENGHT) break;
                    index ++;
                }
                if(hasSuffix == false) index = 0;
            }catch (Exception e){
                e.printStackTrace();
                index = 0;
            }
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
