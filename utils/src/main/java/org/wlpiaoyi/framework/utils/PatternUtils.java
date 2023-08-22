package org.wlpiaoyi.framework.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * @author wlpiaoyi
 */
public class PatternUtils {

    /** 整数 **/
    private static final String NUMBER_PATTERN = "^\\d+?";
    public static boolean isNumber(String text){
        return Pattern.matches(NUMBER_PATTERN, text);
    }

    /** 小数 **/
    private static final String FLOAT_PATTERN = "^\\d+(\\.\\d+)?";
    public static boolean isFloat(String text){
        return Pattern.matches(FLOAT_PATTERN, text);
    }

     /** 电话号码 **/
    private static final String MOBILE_PHONE_PATTERN = "^(\\+(\\d{1,3})){0,1}((12)|(13)|(14)|(15)|(16)|(17)|(18)|(19))\\d{9}$";
    public static boolean isMobilePhone(String text){
        return Pattern.matches(MOBILE_PHONE_PATTERN, text);
    }

     /** 座机号码 **/
    private static final String HOME_PHONE_PATTERN = "^((\\d{2,4}\\-){0,1}\\d{7,9})$";
    public static boolean isHomePhone(String text){
        return Pattern.matches(HOME_PHONE_PATTERN, text);
    }

    /** 邮箱 **/
    private static final String EMAIL_PATTERN = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
    public static boolean isEmail(String text){
        return Pattern.matches(EMAIL_PATTERN, text);
    }
    /** 统一社会信用代码 **/
    private static final String USCC_18_PATTERN = "^[0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}$";
    private static final String USCC_15_PATTERN = "^[1-9]\\d{15}$";
    private static final String USCC_PATTERN = "^([0-9A-HJ-NPQRTUWXY]{2}\\d{6}[0-9A-HJ-NPQRTUWXY]{10}|[1-9]\\d{14})$";
    public static boolean isUSCC18(String text){
        return Pattern.matches(USCC_18_PATTERN, text);
    }
    public static boolean isUSCC15(String text){
        return Pattern.matches(USCC_15_PATTERN, text);
    }
    public static boolean isUSCC(String text){
        return Pattern.matches(USCC_PATTERN, text);
    }

    /** 身份证 **/
    private static final char[] SZ_VER_CODE = "10X98765432".toCharArray();
    private static final int[] IW={7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    public static boolean isID2V(String text){
        if(ValueUtils.isBlank(text)){ return false; }
        if(text.length() != 18){ return false; }

        char[] pszSrc = text.toUpperCase(Locale.ROOT).toCharArray();
        int iS = 0;
        for(int i = 0; i < 17; i++){
            iS += (pszSrc[i]-'0') * IW[i];
        }
        int iY = iS%11;
        return pszSrc[17] == SZ_VER_CODE[iY];
    }

    /** 中文匹配 **/
    private static final String  CHINESE_PATTERN = "^[\\u4e00-\\u9fa5]+$";
    public static boolean isChinese(String text){
        return Pattern.matches(CHINESE_PATTERN, text);
    }


//    public static void main(String[] args) {
//        String pattern = "^([a-zA-Z0-9]{4,6})$";
//        Pattern.matches(pattern, "abcd");
//
//
////        pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&*_+=!?\\(\\)\\[\\]{};':\"\\\\|,.<>\\/]).*$";
////        pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&*_+=!?\\(\\)\\[\\]{};':\"\\\\|,.<>\\/]).*$";
//        String patterns[] = {
//                "^(?=.*[A-Z]).*$",
//                "^(?=.*[a-z]).*$",
//                "^(?=.*\\d).*$",
//                "^(?=.*[@#$%^&*_+=!?\\(\\)\\[\\]{};':\"\\\\|,.<>\\/]).*$",
//        };
////        int i = 0;
////        for (String pattern : patterns){
////            if(Pattern.matches(pattern, "12{}3AB")){
////                i ++;
////            }
////        }
////        String pattern = "/^(?!.*[A-Z][a-z][@#$%^&*_+=!?\\(\\)\\[\\]{};':\"\\\\|,.<>\\/]){3,100}.*$";
////        System.out.println();
//
////        Pattern p = Pattern.compile(pattern);
////        Matcher m = p.matcher("喔的按你的剑法${aabc}阿萨的凝聚力${123aba}{}");
////        StringBuffer sr = new StringBuffer();
////        while (m.find()) {
////            String group = m.group();
////            m.appendReplacement(sr, " ");
////        }
////        m.appendTail(sr);
//////        "喔的按你的剑法${aabc}阿萨的凝聚力${123aba}{}".replaceAll("\\$\\{([a-zA-Z0-9_\\.\\-])+\\}", "/")
////        PatternUtils.isNumber("222");
////        PatternUtils.isUSCC18("91510100MA6CW8CM15");
////
////
//    }
}
