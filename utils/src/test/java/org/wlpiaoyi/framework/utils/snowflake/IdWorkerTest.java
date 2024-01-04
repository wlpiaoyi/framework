package org.wlpiaoyi.framework.utils.snowflake;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.DateUtils;

/**
 * {@code @author:}         wlpia
 * {@code @description:}    雪花算法测试
 * {@code @date:}           2023-12-25 17:32:39
 * {@code @version:}:       1.0
 */
public class IdWorkerTest {
    @Before
    public void setUp() throws Exception {

    }


    @Test
    public void test() throws Exception {
        Long timerEpoch = System.currentTimeMillis();
        IdWorker idWorker = new IdWorker((byte) 1, (byte) 2, timerEpoch);
//        IdWorker idWorker = new IdWorker((byte)4, (byte)4, (byte)4,  5, 2, timerEpoch);
        for (int i = 0; i < 100; i++) {
            long id = idWorker.nextId();
            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
            System.out.println("timestamp assert:" + (idWorker.getTimestamp(id) == idWorker.getLastTimestamp())
                    + ", idWorker.getTimestamp.id:" + idWorker.getTimestamp(id)
                    + ", sequence assert:" + (idWorker.getSequence() == idWorker.getSequence(id))
                    + ", idWorker.getSequence.id:" + idWorker.getSequence(id));
        }
        timerEpoch = DateUtils.toTimestamp(DateUtils.formatToLoaTolDateTime("2021-01-01 08:00:00"));
        idWorker = new IdWorker((byte) 1, (byte) 2, timerEpoch);
        long id = idWorker.nextId();
        System.out.println(Long.toBinaryString(id));
        System.out.println(id);
        timerEpoch = DateUtils.toTimestamp(DateUtils.formatToLoaTolDateTime("2010-01-01 08:00:00"));
        idWorker = new IdWorker((byte) 1, (byte) 2, timerEpoch);
        id = idWorker.nextId();
        System.out.println(Long.toBinaryString(id));
        System.out.println(id);
        timerEpoch = DateUtils.toTimestamp(DateUtils.formatToLoaTolDateTime("2000-01-01 08:00:00"));
        idWorker = new IdWorker((byte) 1, (byte) 2, timerEpoch);
        id = idWorker.nextId();
        System.out.println(Long.toBinaryString(id));
        System.out.println(id);
        timerEpoch = DateUtils.toTimestamp(DateUtils.formatToLoaTolDateTime("1970-01-01 08:00:00"));
        idWorker = new IdWorker((byte) 1, (byte) 2, timerEpoch);
        id = idWorker.nextId();
        System.out.println(Long.toBinaryString(id));
        System.out.println(id);
        timerEpoch = DateUtils.toTimestamp(DateUtils.formatToLoaTolDateTime("1960-01-01 08:00:00"));
        idWorker = new IdWorker((byte) 1, (byte) 2, timerEpoch);
        id = idWorker.nextId();
        System.out.println(Long.toBinaryString(id));
        System.out.println(id);
        timerEpoch = DateUtils.toTimestamp(DateUtils.formatToLoaTolDateTime("1954-04-20 02:30:00"));
        idWorker = new IdWorker((byte) 1, (byte) 2, timerEpoch);
        id = idWorker.nextId();
        System.out.println(Long.toBinaryString(id));
        System.out.println(id);
    }

    @After
    public void tearDown() throws Exception {

    }
}

