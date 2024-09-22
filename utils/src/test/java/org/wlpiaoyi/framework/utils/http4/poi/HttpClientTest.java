package org.wlpiaoyi.framework.utils.http4.poi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.http4.HttpClient;
import org.wlpiaoyi.framework.utils.http4.request.Request;
import org.wlpiaoyi.framework.utils.http4.response.Response;

import java.io.IOException;
import java.util.Random;

public class HttpClientTest {

    @Before
    public void setUp() throws Exception {}

//    public static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
//            30, 200, 300, TimeUnit.SECONDS,
//            new ArrayBlockingQueue<>(50),
//            new ThreadFactory(){ public Thread newThread(Runnable r) {
//                return new Thread(r, "schema_task_pool_" + r.hashCode());
//            }}, new ThreadPoolExecutor.DiscardOldestPolicy());

    public static void runThread(int threadCount, Runnable runnable){
        Random random = new Random();
        for (int i = 0; i < threadCount; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final long timer = System.currentTimeMillis();
                    final int tag = random.nextInt() / 10000;
                    System.out.println("======>" + timer + "," + tag + "|testDataSyn.start:");
                    while (beginTimer > System.currentTimeMillis()){
                    }
                    runnable.run();
//                System.out.println("run:" + Thread.currentThread().hashCode());
                    System.out.println("<======" + timer + "," + tag + "|testDataSyn.end:" );
                }
            }).start();
        }
    }

    private static final String HeaderAuthorization = "Bearer e5edbc05346e4d6eb5f9a9ed87cf4010";

    private static final long beginTimer = System.currentTimeMillis() + 6000;

    /**
     * <p><b>{@code @description:}</b>
     * 数据同步任务查询列表
     * </p>
     *
     * <p><b>@param</b> <b></b>
     * {@link }
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/3/21 17:27</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    @Test
    public void testDataSyn() throws IOException, InterruptedException {
        final String url = "http://36.138.30.52:1888/admin-api/report/shareTask/page?pageSize=10&pageNo=1";
        runThread(200, new Runnable() {
            @Override
            public void run() {
                Response<String> response = HttpClient.instance(
                                Request.initJson(url)
                                        .setMethod(Request.Method.Get)
                                        .setHeader("Authorization", HeaderAuthorization)
                                        .setProxy("127.0.0.1", 8888)
                        )
                        .setRpClazz(String.class)
                        .response();

            }
        });

        while (true){
            new Thread(new Runnable() {
                @Override
                public void run() {
                }
            }).start();
            Thread.sleep(1000);
        }

    }


    @After
    public void tearDown() throws Exception {

    }
}
