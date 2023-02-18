package org.wlpiaoyi.framework.utils;


public class CodeUtils {

    private final static char ADRS[] = new char[]{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    public static char[] getRandomMAC(){
        char MAC[] = new char[12];

        for (int i = 0; i < 12; i++) {
            int index = (int) (Math.random() * 100);
            switch (i){
                case 0:
                    index = index % 15 + 1;
                    break;
                case 1:
                    index = index % 14 + 2;
                    break;
                default:
                    index = index % 16;
                    break;
            }
            MAC[i] = ADRS[index];
        }
        return MAC;
    }


    public static char[] getRandomMEID(){
        char MEID[] = new char[15];
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
