package org.wlpiaoyi.framework.utils.http;

import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2024-09-23 09:24:29</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class HttpUtils {

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
        for (String key : patterns.keySet()){
            urlBuilder.append("&").append(key).append("=").append(patterns.get(key));
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
