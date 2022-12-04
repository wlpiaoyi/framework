package org.wlpiaoyi.framework.mybatis.utils.average;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.algorithm.AverageUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * @Author wlpiaoyi
 * @Date 2022/5/31 09:15
 * @Version 1.0
 */
public class AverageTest {

    @Before
    public void setUp() throws Exception {}




    @Test
    public void testDouble() throws IOException {

        int size = 200001;
        ArrayList<Double> values = new ArrayList();
        double suffix = 10.5;
        double value = 10000000000000d;

        double avg = 0;
        for (int i = 0; i < size; i++){
            value -= suffix;
            System.out.println("index:" + (i + 1) + "--" + new BigDecimal(value));
            if(size / 2 + 1 == i){
                avg = value;
            }
            values.add(value);
        }
        double r1 = AverageUtils.getDouble(values);
        double r2 = 0.0;
        for (double v : values){
            r2 = r2 + v;
        }
        r2 = r2/values.size();
        System.out.println(new BigDecimal(r1) + "---" + new BigDecimal(avg) + "---" + new BigDecimal(r2));
    }



    @After
    public void tearDown() throws Exception {

    }
}
