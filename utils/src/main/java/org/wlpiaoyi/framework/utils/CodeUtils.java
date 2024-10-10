package org.wlpiaoyi.framework.utils;

/**
 * <p><b>{@code @description:}</b>  随机编码生成器</p>
 * <p><b>{@code @date:}</b>         2019/08/22 11:13</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public class CodeUtils {

    private final static char[] ADRS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    /**
     * <p><b>{@code @description:}</b>
     * 获取MAC地址
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/2/22 11:16</p>
     * <p><b>{@code @return:}</b>{@link char[]}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static char[] getRandomMAC(){
        char[] macAddress = new char[12];

        for (int i = 0; i < 12; i++) {
            int index = (int) (Math.random() * 100);
            index = switch (i) {
                case 0 -> index % 15 + 1;
                case 1 -> index % 14 + 2;
                default -> index % 16;
            };
            macAddress[i] = ADRS[index];
        }
        return macAddress;
    }


    /**
     * <p><b>{@code @description:}</b>
     * 国际移动设备识别码
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/2/22 11:17</p>
     * <p><b>{@code @return:}</b>{@link char[]}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static char[] getRandomMEID(){
        char[] MEID = new char[15];
        int index = (int) (Math.random() * 1000);
        index = index % 100;
        if(index < 60){
            MEID[0] = ADRS[3];
            MEID[1] = ADRS[5];
        }else if(index < 98){
            MEID[0] = ADRS[0];
            MEID[1] = ADRS[1];
        }else {
            MEID[0] = ADRS[8];
            MEID[1] = ADRS[6];
        }
        for (int i = 2; i < 14; i++) {
            index = (int) (Math.random() * 100);
            index = index % 10;
            MEID[i] = ADRS[index];
        }
        MEID[14] = CodeUtils.verifyMEIDForEnd(MEID);
        return MEID;
    }

    public static boolean verifyMEID(String MEID){
        MEID = MEID.replace(" ", "");
        char[] chars = MEID.toCharArray();
        return CodeUtils.verifyMEID(chars);
    }

    public static boolean verifyMEID(char[] MEID){
        return MEID[14] == CodeUtils.verifyMEIDForEnd(MEID);
    }

    private static char verifyMEIDForEnd(char[] MEID){
        int oValue = 0;
        int jValue = 0;
        for (int i = 0; i < MEID.length - 1; i++) {
            char adr = MEID[i];
            int index = 0;
            for (char c : ADRS){
                if(c == adr) {
                    break;
                }
                index ++;
            }
            if((i % 2) == 0){
                int jv = index;
                jValue += jv;
            }else{
                int osv = 0,ogv = 0;
                int temp = index << 1;
                if(temp < 10){
                    ogv = temp;
                }else {
                    osv = temp - 10;
                    osv ++;
                }
                oValue += osv + ogv;
            }
        }

        int tvalue = oValue + jValue;
        int value = tvalue % 10;
        return ADRS[(value == 0 ? value : (10 - value))];
    }

}
