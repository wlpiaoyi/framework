package org.wlpiaoyi.framework.mybatis.utils.algorithm;

import org.wlpiaoyi.framework.mybatis.utils.exception.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author wlpiaoyi
 * @Date 2022/2/17 5:29 PM
 * @Version 1.0
 */
class AverageLong implements AlgorithmInterface<Long> {

    private static AverageLong xAverage;

    static final AverageLong singleInstance() {
        if (xAverage == null) {
            synchronized (AverageLong.class) {
                xAverage = new AverageLong();
            }
        }
        return xAverage;
    }

    private AverageLong() {
    }

    private List<Long> doneValues(List<Long> values) {
        List<Long> rs = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            Long v1 = values.get(i);
            Long v2 = values.get(++i);
            long r = Function.getLong(v1, v2);
            rs.add(r);
        }
        return rs;
    }

    private long doneExecute(List<Long> values) {

        int size = values.size();
        Long last = Function.doneOne(values);
        if (last != null) {
            values.remove(values.get(values.size() - 1));
        }
        long r1 = 0;
        if (values.size() > 2) {
            List<Long> temps = this.doneValues(values);
            if (temps.size() > 2) {
                r1 = this.doneExecute(temps);
            } else {
                r1 = Function.getLong(temps.get(0), temps.get(1));
            }
        } else if (values.size() == 2) {
            r1 = Function.getLong(values.get(0), values.get(1));
        } else if (values.size() == 1) {
            throw new BusinessException("error average for size 1");
        } else {
            return last;
        }
        if (last != null) {
            r1 = r1 + (last - r1) / size;
        }
        return r1;
    }

    @Override
    public Long execute(Long... values) {
        return this.execute(Arrays.asList(values));
    }

    @Override
    public Long execute(List<Long> vars) {
        List<Long> values = new ArrayList(vars);
        return this.doneExecute(values);
    }


    public static void main(String[] args) {

        int size = 201;
        ArrayList<Long> values = new ArrayList();
        long suffix = 100;
        long value = 0;
        for (int i = 0; i < size; i++){
            value += suffix;
            values.add(value);
        }
        long r1 = AverageLong.singleInstance().execute(values);
        long r2 = 0;
        for (long v : values){
            r2 = r2 + v;
        }
        r2 = r2/values.size();
        System.out.println(r1 + "---" + r2);
    }
}