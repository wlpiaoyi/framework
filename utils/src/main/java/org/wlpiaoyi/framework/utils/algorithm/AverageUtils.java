package org.wlpiaoyi.framework.utils.algorithm;

import java.math.BigDecimal;
import java.util.List;

/**
 * 求平均数
 * @Author wlpiaoyi
 * @Date 2022/5/30 17:41
 * @Version 1.0
 */
public class AverageUtils {

    public static BigDecimal getBigDecimal(int scale, BigDecimal... vars){
        return AverageBigDecimal.singleInstance().setScale(scale).execute(vars);
    }

    public static BigDecimal getBigDecimal(BigDecimal... vars){
        return AverageBigDecimal.singleInstance().execute(vars);
    }

    public static BigDecimal getBigDecimal(int scale, List<BigDecimal> vars){
        return AverageBigDecimal.singleInstance().setScale(scale).execute(vars);
    }

    public static BigDecimal getBigDecimal(List<BigDecimal> vars){
        return AverageBigDecimal.singleInstance().setScale(100).execute(vars);
    }

    public static double getDouble(Double... vars){
        return AverageDouble.singleInstance().execute(vars);
    }
    public static double getDouble(List<Double>  vars){
        return AverageDouble.singleInstance().execute(vars);
    }

    public static long getLong(Long... vars){
        return AverageLong.singleInstance().execute(vars);
    }
    public static long getLong(List<Long>  vars){
        return AverageLong.singleInstance().execute(vars);
    }

}
