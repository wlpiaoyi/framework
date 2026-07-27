package org.wlpiaoyi.framework.utils.snowflake;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

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
        IdWorker idWorker = new IdWorker((byte) 5, (byte)2, timerEpoch);
//        System.out.println("IdWorker:" + idWorker);
//        for (int i = 0; i < 5; i++) {
//            long id = idWorker.nextId();
//            System.out.println("Id:" + id);
//            System.out.println("Id to binary string:" + Long.toBinaryString(id));
//            System.out.println("Id getWorkerId:" + idWorker.getWorkerId(id));
//            System.out.println("Id getDatacenterId:" + idWorker.getDatacenterId(id));
//            System.out.println("Id getHappenTimestamp:" + idWorker.getHappenTimestamp(id));
//            System.out.println("Id getSequence:" + idWorker.getSequence(id));
//        }
//        timerEpoch -= 11198;
//        idWorker = new IdWorker((byte)5, (byte)5, (byte)6,  (byte)6, (byte)4, timerEpoch);
//        System.out.println("IdWorker:" + idWorker);
//        for (int i = 0; i < 5; i++) {
//            long id = idWorker.nextId();
//            System.out.println("Id:" + id);
//            System.out.println("Id to binary string:" + Long.toBinaryString(id));
//            System.out.println("Id getWorkerId:" + idWorker.getWorkerId(id));
//            System.out.println("Id getDatacenterId:" + idWorker.getDatacenterId(id));
//            System.out.println("Id getHappenTimestamp:" + idWorker.getHappenTimestamp(id));
//            System.out.println("Id getSequence:" + idWorker.getSequence(id));
//        }
        byte workerIdBits = 3;
        byte datacenterIdBits = 3;
        byte sequenceBits = 12;
        byte workerId = (byte) 1;
        byte datacenterId = 2;

        var dates = new ArrayList<LocalDateTime>(){{
            add(DateUtils.parseLocalDateTime("2020-01-01 08:00:00"));
            add(DateUtils.parseLocalDateTime("2000-01-01 08:00:00"));
            add(DateUtils.parseLocalDateTime("1980-01-01 08:00:00"));
            add(DateUtils.parseLocalDateTime("1960-01-01 08:00:00"));
            add(DateUtils.parseLocalDateTime("1940-01-01 08:00:00"));
            add(DateUtils.parseLocalDateTime("1920-01-01 08:00:00"));
            add(DateUtils.parseLocalDateTime("1900-01-01 08:00:00"));
            add(DateUtils.parseLocalDateTime("1475-01-01 08:00:00"));
        }};
        for (LocalDateTime date : dates) {
            timerEpoch = DateUtils.parseTimestamp(date);
            idWorker = new IdWorker(workerIdBits, datacenterIdBits, sequenceBits, workerId, datacenterId, timerEpoch);
            var id = idWorker.nextId();
            System.out.println("==================================================");
            System.out.println("timerEpoch:" + DateUtils.formatLocalDateTime(date));
            System.out.println("id:        " + id);
            System.out.println("binaryId:  " + Long.toBinaryString(id));
            System.out.println("binaryMax: " + Long.toBinaryString(Long.MIN_VALUE - 1L));
        }
        System.out.println("==================================================");
        System.out.println("最大机器数量:" + (long)Math.pow(2, workerIdBits + datacenterIdBits));
        System.out.println("最大并发量/ms:" + (long)Math.pow(2, sequenceBits));

    }

    @After
    public void tearDown() throws Exception {

    }
}

