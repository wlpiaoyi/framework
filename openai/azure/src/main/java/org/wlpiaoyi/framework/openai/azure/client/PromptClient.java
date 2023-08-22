package org.wlpiaoyi.framework.openai.azure.client;

import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.http.HttpClient;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/8/18 12:29
 * {@code @version:}:       1.0
 */
public class PromptClient {

    public static String req(String prompt, String tag) {
        Map<String, String> authMap = Utils.AUTH_MAP.get(tag);
        Map body = new HashMap(){{
            put("prompt", prompt);
            put("max_tokens", 2048);
        }};
        Request request = Request.initJson(authMap.get(Utils.AUTH_MAP_OPENAI_ENDPOINT) + "openai/deployments/" + authMap.get(Utils.AUTH_MAP_MODEL_ID) +"/completions?api-version=2023-05-15")
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
        String text = MapUtils.getString((Map) MapUtils.getList(res, "choices").get(0), "text");
        return text;
    }
}
