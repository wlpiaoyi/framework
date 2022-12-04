package org.wlpiaoyi.framework.utils.algorithm;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author wlpiaoyi
 * @Date 2022/2/17 10:49 AM
 * @Version 1.0
 */
class Function {


    private static final long AFTER_POINT_FLAG = 100000000000000000L;
    private static final int AFTER_SCALE_FLAG = 18;

    public static <T> T doneOne(List<T> values) {
        if(values.size() % 2 != 0){
            return values.get(values.size() - 1);
        }
        return null;
    }


    static BigDecimal getBigDecimal(BigDecimal value1, BigDecimal value2, int scale){
        return value1.add(value2).divide(new BigDecimal(2), scale, BigDecimal.ROUND_HALF_UP);
    }

    static BigDecimal getBigDecimal(double value1, double value2, int scale){
        long v1_1 = (long) value1;
        long v1_2 = 0;
        double temp = value1 - v1_1;
        if(temp != 0){
            v1_2 = (long) (temp * AFTER_POINT_FLAG);
        }
        long v2_1 = (long) value2;
        long v2_2 = 0;
        temp = value2 - v2_1;
        if(temp != 0){
            v2_2 = (long) (temp * AFTER_POINT_FLAG);
        }
        BigDecimal r1 = getBigDecimal(v1_1, v2_1, scale);
        if(v1_2 > 0 || v2_2 > 0){
            BigDecimal r2 = getBigDecimal(v1_2, v2_2, scale);
            r2 = r2.divide(new BigDecimal(AFTER_POINT_FLAG), AFTER_SCALE_FLAG, BigDecimal.ROUND_HALF_UP);
            r1 = r1.add(r2);
//            r2 = r2 / (double) AFTER_POINT_FLAG;
//            r1 += r2;
        }
        return r1;
    }

    static double getDouble(double value1, double value2){
        long v1_1 = (long) value1;
        long v1_2 = 0;
        double temp = value1 - v1_1;
        if(temp != 0){
            v1_2 = (long) (temp * AFTER_POINT_FLAG);
        }
        long v2_1 = (long) value2;
        long v2_2 = 0;
        temp = value2 - v2_1;
        if(temp != 0){
            v2_2 = (long) (temp * AFTER_POINT_FLAG);
        }
        double r1 = getDouble(v1_1, v2_1);
        if(v1_2 > 0 || v2_2 > 0){
            double r2 = getDouble(v1_2, v2_2);
            r2 = r2 / (double) AFTER_POINT_FLAG;
            r1 += r2;
        }
        return r1;
    }

    static double getDouble(long value1, long value2){
        return (value1 & value2) + (value1 ^ value2) / 2.0;
    }

    static long getLong(long value1, long value2){
        return (value1 & value2) + (value1 ^ value2) / 2;
    }

    public static void main(String[] args) {
        getDouble(1.001, 3.002);
    }
}
