package org.wlpiaoyi.framework.mybatis.utils;

import java.util.Locale;
import java.util.regex.Pattern;

public class PatternUtils {

    //整数
    private static final String NUMBER_PATTERN = "^\\d+?";
    public static boolean isNumber(String text){
        return Pattern.matches(NUMBER_PATTERN, text);
    }

    //小数
    private static final String FLOAT_PATTERN = "^\\d+(\\.\\d+)?";
    public static boolean isFloat(String text){
        return Pattern.matches(FLOAT_PATTERN, text);
    }

    //电话号码
    private static final String MOBILE_PHONE_PATTERN = "^(\\+(\\d{1,3})){0,1}((12)|(13)|(14)|(15)|(16)|(17)|(18)|(19))\\d{9}$";
    public static boolean isMobilePhone(String text){
        return Pattern.matches(MOBILE_PHONE_PATTERN, text);
    }

    //座机号码
    private static final String HOME_PHONE_PATTERN = "^((\\d{2,4}\\-){0,1}\\d{7,9})$";
    public static boolean isHomePhone(String text){
        return Pattern.matches(HOME_PHONE_PATTERN, text);
    }

    //邮箱
    private static final String EMAIL_PATTERN = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
    public static boolean isEmail(String text){
        return Pattern.matches(EMAIL_PATTERN, text);
    }

    //身份证
    private static final char SZ_VER_CODE[] = "10X98765432".toCharArray();
    public static boolean isID2V(String text){
        if(ValueUtils.isBlank(text)) return false;
        if(text.length() != 18) return false;

        char pszSrc[] = text.toUpperCase(Locale.ROOT).toCharArray();
        int iS = 0;
        int iW[]={7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        int i;
        for(i=0;i<17;i++)
        {
            iS += (pszSrc[i]-'0') * iW[i];
        }
        int iY = iS%11;
        return pszSrc[17] == SZ_VER_CODE[iY];
    }

//
//    public static void main(String[] args) {
//        PatternUtils.isNumber("222");
//        PatternUtils.isID("510622198906184510");
//
//    }
}
