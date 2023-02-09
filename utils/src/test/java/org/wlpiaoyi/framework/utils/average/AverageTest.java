package org.wlpiaoyi.framework.utils.average;

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

        int size = 10001;
        ArrayList<Double> values = new ArrayList();
        double suffix = 1.125;
        double value = 99999999999999.5;

        for (int i = 0; i < size; i++){
            value -= suffix;
//            System.out.println("index:" + (i + 1) + "--" + new BigDecimal(value));
            System.out.println(new BigDecimal(value));
            values.add(value);
        }
        double avg_r = AverageUtils.getDouble(values);
        double res_r = 0.0;
        for (double v : values){
            res_r = res_r + v;
        }
        res_r = res_r/values.size();
        System.out.println("avg_r:" + new BigDecimal(avg_r) + "   res_r:" + new BigDecimal(res_r));
    }



    @After
    public void tearDown() throws Exception {

    }
}
