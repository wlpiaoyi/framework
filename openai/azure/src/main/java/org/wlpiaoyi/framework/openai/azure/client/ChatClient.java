package org.wlpiaoyi.framework.openai.azure.client;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.http.HttpClient;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.util.List;
import java.util.Map;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/8/17 15:21
 * {@code @version:}:       1.0
 */
public class ChatClient {

    @Data
    public static class ChatBody{
        private List<ChatMessage> messages;

        private String user;
    }
    @Data
    public static class ChatMessage implements Utils.WaitTimer {

        public ChatMessage(){
            this.waitTimer = 1;
            this.waitFlagEnd = false;
        }

        private String role;
        private String content;
        @Expose
        private int waitTimer;
        @Expose
        private boolean waitFlagEnd;

        public boolean isWaitFlagEnd() {
            return waitFlagEnd;
        }
    }

    public static ChatMessage req(ChatBody body, String tag) {
        Map<String, String> authMap = Utils.AUTH_MAP.get(tag);
        Request request = Request.initJson(authMap.get(Utils.AUTH_MAP_OPENAI_ENDPOINT) + "openai/deployments/"
                        + authMap.get(Utils.AUTH_MAP_MODEL_ID) +"/chat/completions?api-version=2023-05-15")
                .setHeader("Content-Type", "application/json")
                .setHeader("api-key", authMap.get(Utils.AUTH_MAP_OPENAI_KEY))
                .setBody(body)
                .setMethod(Request.Method.Post);
        Response<String> respChat =  HttpClient.instance(request)
                .setRpClazz(String.class)
                .response();
        if(respChat.getStatusCode() != 200){
            throw new BusinessException(respChat.getStatusCode(), respChat.getBody());
        }
        Map res = Utils.GSON.fromJson(respChat.getBody(), Map.class);
        Map msg = MapUtils.getMap((Map) MapUtils.getList(res, "choices").get(0), "message");
        ChatMessage resp = Utils.GSON.fromJson(Utils.GSON.toJson(msg), ChatMessage.class);
        return resp;
    }


}
