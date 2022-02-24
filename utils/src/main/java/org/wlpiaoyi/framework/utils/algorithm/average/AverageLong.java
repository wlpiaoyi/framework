package org.wlpiaoyi.framework.utils.algorithm.average;

import org.wlpiaoyi.framework.utils.algorithm.Algorithm;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author wlpiaoyi
 * @Date 2022/2/17 5:29 PM
 * @Version 1.0
 */
public class AverageLong implements Algorithm<Long> {

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
            long r = Utils.toLong(v1, v2);
            rs.add(r);
        }
        return rs;
    }

    private long doneExecute(List<Long> values) {
        int size = values.size();
        Long last = Utils.doneOne(values);
        if (last != null) {
            values.remove(values.get(values.size() - 1));
        }
        long r1 = 0;
        if (values.size() > 2) {
            List<Long> temps = this.doneValues(values);
            if (temps.size() > 2) {
                r1 = this.doneExecute(temps);
            } else {
                r1 = Utils.toLong(temps.get(0), temps.get(1));
            }
        } else if (values.size() == 2) {
            r1 = Utils.toLong(values.get(0), values.get(1));
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
        long v = 2;
        ArrayList<Long> values = new ArrayList() {{
            add(Long.MAX_VALUE);
            add(Long.MAX_VALUE - v);
            add(Long.MAX_VALUE);
            add(Long.MAX_VALUE - v);
            add(Long.MAX_VALUE);
            add(Long.MAX_VALUE - v);
            add(Long.MAX_VALUE);
            add(Long.MAX_VALUE - v);
            add(Long.MAX_VALUE);
            add(Long.MAX_VALUE - v);
        }};
        long r1 = AverageLong.singleInstance().execute(values).longValue();
//        double r2 = 0.0;
//        for (double v : values){
//            r2 = r2 + v;
//        }
//        r2 = r2/values.size();
        System.out.println();
    }
}