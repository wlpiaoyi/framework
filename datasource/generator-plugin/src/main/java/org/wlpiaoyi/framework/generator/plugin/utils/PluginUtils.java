package org.wlpiaoyi.framework.generator.plugin.utils;

import org.wlpiaoyi.framework.utils.ValueUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2024-10-11 12:02:00</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class PluginUtils {

    // 字段类型
    public static Map<String, String> columnTypeDict = new HashMap();
    // 引入类型
    public static Map<String, String> implTypeDict = new HashMap();


    public static Map<String, String> implDecorateDict = new HashMap();
    public static Map<String, String> fieldDecorateDict = new HashMap();

    public static Map<String, String> implValidDict = new HashMap();
    public static Map<String, String> msgValidDict = new HashMap();


    public static Map patternComment(String comment){
        Map<String, Object> res = new HashMap<>();
        String name;
        int i1 = comment.indexOf(",");
        int i2 = comment.indexOf("=(");
        int i = -1;
        if(i1 > 0){
            i = i1;
        }
        if(i2 > 0 && i2 < i1){
            i = i2;
        }
        if(i != -1){
            name = comment.substring(0, i);
            comment = comment.substring(i);
        }else{
            name = comment;
            comment = null;
        }
        res.put("name", name);
        if(ValueUtils.isBlank(comment)){
            return res;
        }

        String desc = null;
        i1 = comment.indexOf(",");
        i2 = comment.indexOf("=(");
        if(i2 != -1 && i2 < i1){
            i1 = -1;
        }
        if(i1 != -1){
            comment = comment.substring(i1 + 1);
            i2 --;
            if(ValueUtils.isBlank(comment)){
                return res;
            }
            if(i2 > 0){
                desc = comment.substring(0,i2);
                comment = comment.substring(i2);
                if(ValueUtils.isBlank(comment)){
                    res.put("desc", desc);
                    return res;
                }
            }else{
                desc = comment;
                comment = null;
            }
            if(ValueUtils.isBlank(comment)){
                res.put("desc", desc);
                return res;
            }
        }else {
            if(i2 == -1){
                desc = comment;
                if(ValueUtils.isBlank(comment)){
                    res.put("desc", desc);
                    return res;
                }
            }
        }
        if(ValueUtils.isNotBlank(desc)){
            res.put("desc", desc);
        }
        if(ValueUtils.isBlank(comment)){
            return res;
        }

        String options;
        i = comment.indexOf("=(");
        if(i != -1){
            comment = comment.substring(i + 2);
        }
        if(ValueUtils.isBlank(comment)){
            return res;
        }
        i = comment.indexOf(")");
        if(i > 0){
            comment = comment.substring(0, i);
        }
        if(ValueUtils.isBlank(comment)){
            return res;
        }
        options = comment;
        if(ValueUtils.isNotBlank(options)){
            res.put("options", options.split(","));
        }
        return res;
    }

//    public static void main(String[] args) {
//        Map res0 = patternComment("是否公开,如果是公开则所有人都能看到,否则只有指定角色的人才能看到=(0:非公开,1:公开)");
//        Map res1 = patternComment("角色编码,abcdaweo=(1:管理员,2:你猜)");
//        Map res3 = patternComment("角色编码=(1:管理员,2:你猜)");
//        Map res2 = patternComment("角色编码,abcdaweo");
//        Map res4 = patternComment("角色编码");
//        System.out.println();
//    }
}
