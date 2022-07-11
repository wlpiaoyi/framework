package org.wlpiaoyi.framework.utils.algorithm;

import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author wlpiaoyi
 * @Date 2022/2/17 10:32 AM
 * @Version 1.0
 */
class AverageDouble implements AlgorithmInterface<Double> {

    private static AverageDouble xAverage;

    static final AverageDouble singleInstance(){
        if(xAverage == null){
            synchronized (AverageDouble.class){
                xAverage = new AverageDouble();
            }
        }
        return xAverage;
    }

    private AverageDouble(){
    }
    private List<Double> doneValues(List<Double> values) {
        List<Double> rs = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            Double v1 = values.get(i);
            Double v2 = values.get(++i);
            double r = Function.getDouble(v1, v2);
            rs.add(r);
        }
        return rs;
    }

    private double doneExecute(List<Double> values) {
        int size = values.size();
        Double last = Function.doneOne(values);
        if(last != null){
            values.remove(values.get(values.size() - 1));
        }
        double r1 = 0;
        if(values.size() > 2){
            List<Double> temps = this.doneValues(values);
            if(temps.size() > 2){
                r1 = this.doneExecute(temps);
            }else{
                r1 = Function.getDouble(temps.get(0), temps.get(1));
            }
        }else if(values.size()  == 2){
            r1 = Function.getDouble(values.get(0), values.get(1));
        }else if(values.size() == 1){
            throw new BusinessException("error average for size 1");
        }else{
            return last.doubleValue();
        }
        if(last != null){
            r1 = r1 + (last.doubleValue() - r1) / (double) size;
        }
        return r1;
    }

    @Override
    public Double execute(Double... values) {
        return this.execute(Arrays.asList(values));
    }

    @Override
    public Double execute(List<Double> vars) {
        List<Double> values = new ArrayList(vars);
        return this.doneExecute(values);
    }

}
