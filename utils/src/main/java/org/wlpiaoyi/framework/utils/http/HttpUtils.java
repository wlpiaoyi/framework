package org.wlpiaoyi.framework.utils.http;

import com.google.gson.Gson;
import org.apache.hc.core5.http.HttpHeaders;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;
import org.wlpiaoyi.framework.utils.http.factory.HttpFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2024-09-23 09:24:29</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class HttpUtils {

    public static final String HEADER_CHARSET = "utf-8";
    public static final String HEADER_APPLICATION_JSON = "application/json";
    public static final String HEADER_APPLICATION_FORM = "application/x-www-form-urlencoded";
    public static final String HEADER_KEY0 = "content-encoding";
    public static final String HEADER_KEY1 = HttpHeaders.CONTENT_TYPE;
    public static final String HEADER_KEY2 = HttpHeaders.ACCEPT;
    public static final String HEADER_VALUE1_1 = HEADER_APPLICATION_JSON + ";charset=" + HEADER_CHARSET;
    public static final String HEADER_VALUE1_2 = HEADER_APPLICATION_FORM + ";charset=" + HEADER_CHARSET;
    public static final String HEADER_VALUE2 = HEADER_APPLICATION_JSON;


    /**
     * <p><b>{@code @description:}</b>
     * Json默认Header
     * </p>
     *
     * <p><b>@param</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 8:29</p>
     * <p><b>{@code @return:}</b>{@link Map< String, String>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static Map<String, String> HEADER_JSON_DEFAULTS(){
        Map<String, String> resMap = new HashMap<>();
        resMap.put(HEADER_KEY1, HEADER_VALUE1_1);
        resMap.put(HEADER_KEY2, HEADER_VALUE2);
        return resMap;
    }

    /**
     * <p><b>{@code @description:}</b>
     * Form默认Header
     * </p>
     *
     * <p><b>@param</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 8:30</p>
     * <p><b>{@code @return:}</b>{@link Map< String, String>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static Map<String, String> HEADER_FORM_DEFAULTS(){
        Map<String, String> resMap = new HashMap<>();
        resMap.put(HEADER_KEY1, HEADER_VALUE1_2);
        resMap.put(HEADER_KEY2, HEADER_VALUE2);
        return resMap;
    }

    public static final int TIME_OUT_MS = 120000;

    public static final String SCHEME_HTTP = "http";
    public static final String SCHEME_HTTPS = "https";

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>url</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 9:23</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String schemeFromUrl(String url) {
        return url.split("://")[0];
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>url</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 9:23</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String domainFromUrl(String url){
        return url.split("//")[1].split("/")[0].split(":")[0];
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>url</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 9:23</p>
     * <p><b>{@code @return:}</b>{@link int}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static int portFromUrl(String url){
        String[] args =  url.split("//")[1].split("/")[0].split(":");
        if(args.length > 1){
            return Integer.parseInt(args[1]);
        }
        return 80;
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>url</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 9:23</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String pathFromUrl(String url){
        String value = url.split("//")[1];
        int startIndex = value.indexOf("/");
        int endIndex = value.indexOf("?");
        if(startIndex <= 0){
            return "";
        }
        if(endIndex < 0){
            return value.substring(startIndex);
        }
        return value.substring(startIndex, endIndex);
    }

    /**
     * <p><b>{@code @description:}</b>
     * TODO
     * </p>
     *
     * <p><b>@param</b> <b>url</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 9:23</p>
     * <p><b>{@code @return:}</b>{@link Map < String, String>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static Map<String, String> patternsFromUrl(String url){
        int index = url.indexOf("?");
        if(index < 0){
            return null;
        }
        String[] patternsStr = url.substring(index).split("&");
        Map<String, String> patterns = HashMap.newHashMap(patternsStr.length);
        for (String item : patternsStr){
            String[] args = item.split("=");
            String name = args[0];
            String value = args.length > 1 ? args[1] : null;
            patterns.put(name, value);
        }
        return patterns;
    }

    /**
     * <p><b>{@code @description:}</b>
     *
     * </p>
     *
     * <p><b>@param</b> <b>url</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>patterns</b>
     * {@link Map<String, String>}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/9/23 9:22</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String urlMergePatterns(String url, Map<String, String> patterns){
        if(ValueUtils.isBlank(patterns)){
            return url;
        }
        int index = url.indexOf("?");
        if(index > 0){
            Map<String, String> orgPatterns = patternsFromUrl(url);
            url = url.substring(0, index);
            if(ValueUtils.isNotBlank(orgPatterns)){
                for(String key : orgPatterns.keySet()){
                    if(patterns.containsKey(key)){
                        continue;
                    }
                    patterns.put(key, orgPatterns.get(key));
                }
            }
        }
        if(ValueUtils.isBlank(patterns)){
            return url;
        }
        StringBuilder urlBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : patterns.entrySet()){
            urlBuilder.append("&").append(entry.getKey()).append("=").append(patterns.get(entry.getValue()));
        }
        url = url + "?" + urlBuilder.substring(1);
        return url;
    }



//    public static void main(String[] args) {
//        urlMergePatterns("http://www.baidu.com:3039/abc/abc?a=1&b=2", new HashMap(){{
//            put("b", "3");
//            put("c", "4");
//        }});
//    }
}
