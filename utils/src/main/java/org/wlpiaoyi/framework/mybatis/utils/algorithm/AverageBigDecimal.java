package org.wlpiaoyi.framework.mybatis.utils.algorithm;

import org.wlpiaoyi.framework.mybatis.utils.exception.BusinessException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author wlpiaoyi
 * @Date 2022/5/30 16:43
 * @Version 1.0
 */
class AverageBigDecimal implements AlgorithmInterface<BigDecimal> {

    private int scale = 100;

    public AverageBigDecimal setScale(int scale) {
        this.scale = scale;
        return this;
    }

    private static AverageBigDecimal xAverage;

    static final AverageBigDecimal singleInstance(){
        if(xAverage == null){
            synchronized (AverageBigDecimal.class){
                xAverage = new AverageBigDecimal ();
            }
        }
        return xAverage;
    }

    private AverageBigDecimal (){
    }
    private List<BigDecimal > doneValues(List<BigDecimal > values) {
        List<BigDecimal > rs = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            BigDecimal  v1 = values.get(i);
            BigDecimal  v2 = values.get(++i);
            BigDecimal  r = Function.getBigDecimal(v1, v2, scale);
            rs.add(r);
        }
        return rs;
    }


    private BigDecimal doneExecute(List<BigDecimal> values) {

        int size = values.size();
        BigDecimal last = Function.doneOne(values);
        if (last != null) {
            values.remove(values.get(values.size() - 1));
        }
        BigDecimal r1;
        if (values.size() > 2) {
            List<BigDecimal> temps = this.doneValues(values);
            if (temps.size() > 2) {
                r1 = this.doneExecute(temps);
            } else {
                r1 = Function.getBigDecimal(temps.get(0), temps.get(1), scale);
            }
        } else if (values.size() == 2) {
            r1 = Function.getBigDecimal(values.get(0), values.get(1), scale);
        } else if (values.size() == 1) {
            throw new BusinessException("error average for size 1");
        } else {
            return last;
        }
        if (last != null) {
            r1 = r1.add(last.subtract(r1).divide(new BigDecimal(size), this.scale, BigDecimal.ROUND_HALF_UP));
//            r1 = r1.add(last.subtract(r1).divide(new BigDecimal(size)));
        }
        return r1;
    }

    @Override
    public BigDecimal execute(BigDecimal... values) {
        return this.execute(Arrays.asList(values));
    }

    @Override
    public BigDecimal execute(List<BigDecimal> vars) {
        List<BigDecimal> values = new ArrayList(vars);
        return this.doneExecute(values);
    }


    public static void main(String[] args) {
        int size = 201;
        ArrayList<BigDecimal> values = new ArrayList();
        double suffix = 1.1;
        double value = 0;
        for (int i = 0; i < size; i++){
            value += suffix;
            System.out.println(value);
            values.add(new BigDecimal(value));
        }
        BigDecimal r1 = AverageBigDecimal .singleInstance().execute(values);
        double r2 = 0.0;
        for (BigDecimal v : values){
            r2 = r2 + v.doubleValue();
        }
        r2 = r2/values.size();
        System.out.println(r1 + "---" + r2);
    }
}
