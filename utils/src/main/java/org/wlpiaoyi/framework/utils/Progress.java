package org.wlpiaoyi.framework.utils;

import lombok.Getter;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.util.concurrent.*;

import static java.lang.String.format;

/**
 * <p><b>{@code @description:}</b>  控制台进度条</p>
 * <p><b>{@code @date:}</b>         2024/2/18 14:23</p>
 * <p><b>{@code @author:}</b>       wlpiaoyi</p>
 * <p><b>{@code @version:}</b>      1.0</p>
 */
public class Progress {

    private static final int PROGRESS_BAR = 50;    //控制输出的进度条宽度

    private String title = "title";
    private String runTimeMemo = "run-time";
    private String remainTimeMemo = "remain-time";
    private int unitSize = PROGRESS_BAR + this.runTimeMemo.length() + this.remainTimeMemo.length() + 20;
    public static int MAX_RATE = 100;
    @Getter
    volatile float rate = 0;
    volatile boolean isRunning = false;
    long beginTimer;
    private static Progress xProgress = null;
    private final Object synFlag = new Object();


    public static Progress singleInstance(){
        if(xProgress != null){
            return xProgress;
        }
        synchronized (Progress.class){
            if(xProgress != null){
                return xProgress;
            }
            xProgress = new Progress();
        }
        return xProgress;
    }
    private String paresUnitTime(long timer){
        if(timer < 10){
            return "0" + timer;
        }
        return timer + "";

    }
    private String paresTime(long timer){
        timer = timer / 1000;
        long s = timer % 60;
        long m = (timer / 60) % 60;
        long h = Math.min(timer / 3600, 99);
        return paresUnitTime(h) + ":" + paresUnitTime(m) + ":" + paresUnitTime(s);
    }

    private String runTime(){
        return this.paresTime(System.currentTimeMillis() - this.beginTimer);
    }
    private String remainTime(){
        long runTime = System.currentTimeMillis() - this.beginTimer;
        long totalTime = (long) (runTime / (this.rate / 100.f));
        return paresTime(totalTime - runTime);
    }

    private void printTimer(){
        System.out.print(" " + this.runTimeMemo + ":" + this.runTime());
        System.out.print(" " + this.remainTimeMemo + ":" + this.remainTime());
    }

    private ScheduledFuture<?> future;
    private ScheduledExecutorService service;

    /**
     * <p><b>{@code @description:}</b>
     * 倒计时名称
     * </p>
     *
     * <p><b>@param</b> <b>title</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/2/18 14:38</p>
     * <p><b>{@code @return:}</b>{@link Progress}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public Progress setTitle(String title) {
        if(this.isRunning){
            throw new BusinessException("Can't change value when task running");
        }
        this.title = title;
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 倒计时运行时间
     * </p>
     *
     * <p><b>@param</b> <b>runTimeMemo</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/2/18 14:38</p>
     * <p><b>{@code @return:}</b>{@link Progress}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public Progress setRunTimeMemo(String runTimeMemo) {
        if(this.isRunning){
            throw new BusinessException("Can't change value when task running");
        }
        this.runTimeMemo = runTimeMemo;
        this.unitSize = PROGRESS_BAR + this.runTimeMemo.length() + this.remainTimeMemo.length() + 20;
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 倒计时剩余时间
     * </p>
     *
     * <p><b>@param</b> <b>remainTimeMemo</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/2/18 14:39</p>
     * <p><b>{@code @return:}</b>{@link Progress}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public Progress setRemainTimeMemo(String remainTimeMemo) {
        if(this.isRunning){
            throw new BusinessException("Can't change value when task running");
        }
        this.remainTimeMemo = remainTimeMemo;
        this.unitSize = PROGRESS_BAR + this.runTimeMemo.length() + this.remainTimeMemo.length() + 20;
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 倒计时剩余时间
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/2/18 14:39</p>
     * <p><b>{@code @return:}</b>{@link Progress}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public Progress init(){
        synchronized (this.synFlag){
            if(this.future != null && !this.future.isCancelled()){
                throw new BusinessException("Can't create a schedule when there has a alive schedule");
            }
            this.destroy(true);
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            this.future = service.scheduleAtFixedRate(() -> {
                if(!this.isRunning){
                    return;
                }
                if(rate < MAX_RATE){
                    printCurrentNum(rate);
                }else{
                    printCurrentNum(MAX_RATE);
                    System.out.println();
                    this.end();
                }
            }, 0L, 50L, TimeUnit.MILLISECONDS);
            this.service = service;
        }
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 销毁
     * </p>
     *
     * <p><b>@param</b> <b>mayInterruptIfRunning</b>
     * {@link boolean}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/2/18 14:39</p>
     * <p><b>{@code @return:}</b>{@link Progress}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public Progress destroy(boolean mayInterruptIfRunning){
        if(this.future != null){
            this.future.cancel(mayInterruptIfRunning);
        }
        if(this.service != null){
            this.service.shutdownNow();
        }
        return this;
    }

    private Progress(){
    }

    /**
     * <p><b>{@code @description:}</b>
     * 开始计时
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/2/18 14:39</p>
     * <p><b>{@code @return:}</b>{@link Progress}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public final Progress begin(){
        if(isRunning){
            throw new BusinessException("has a running task!");
        }
        synchronized (this.synFlag){
            if(isRunning){
                throw new BusinessException("has a running task!");
            }
            if(this.future == null || this.future.isCancelled()){
                throw new BusinessException("Schedule is null");
            }
            this.beginTimer = System.currentTimeMillis();
            StringBuilder kg = new StringBuilder();
            for(int i=0; i < PROGRESS_BAR; i++){
                kg.append(undoFlag);
            }
            System.out.print(this.title + ":00.0%[>"+kg.toString()+"]");
            this.printTimer();
            rate = 0;
            this.isRunning = true;
        }
        return this;
    }

    /**
     * <p><b>{@code @description:}</b>
     * 结束计时
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/2/18 14:40</p>
     * <p><b>{@code @return:}</b>{@link Progress}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public final Progress end(){
        this.setRate(Progress.MAX_RATE);
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.setRate(Progress.MAX_RATE + 1);
        this.isRunning = false;
        return this;
    }

    private void focusGoto(){
        for(int i = unitSize + 8; i > 0; i--){
            System.out.print('\b');
        }
    }

    public void setRate(float rate) {
        if (!this.isRunning) {
            return;
        }
        this.rate = rate;
    }

    private static final String doneFlag = "■";
    private static final String doing1Flag = "□";
    private static final String doing2Flag = "◩";
    private static final String undoFlag = " ";

    private synchronized void printCurrentNum(float i) {
        String num = "00" + (int)(i * 100);
        if (i >= MAX_RATE) {
            i = MAX_RATE;
            num = "100.0";
        }else{
            if(num.length() == 3){
                num = num.substring(0, num.length() - 1) + "0" + num.substring(num.length() - 1);
            }else if(num.length() > 4){
                num = num.substring(num.length() - 4);
            }
            num = num.substring(0, num.length() - 2) + "." + num.substring(num.length() - 2);
        }
        StringBuffer s = new StringBuffer(num + "%[");
        focusGoto();
        final float prec = i;
        for (int index = 0; index < PROGRESS_BAR; index++) {
            float c = (index * MAX_RATE) / (float) PROGRESS_BAR;
            if (c < prec) {
                if(prec - c < 1.f){
                    s.append(doing1Flag);
                } else if(prec - c < 2.f){
                    s.append(doing2Flag);
                }else{
                    s.append(doneFlag);
                };
            }else {
                s.append(undoFlag);
            }
        }
        s.append("]");
        System.out.print(s.toString());
        this.printTimer();
    }

//    public static void main(String[] args) {
//
//        Progress progress = Progress.singleInstance().init();
//        progress.setTitle("测试标题来了").begin();
//        while (progress.getRate() <= Progress.MAX_RATE){
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            progress.setRate(progress.getRate() + 0.1f);
//        }
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        progress.setRunTimeMemo("运行时间").setRemainTimeMemo("剩余时间").setTitle("test2").begin();
//        while (progress.getRate() <= Progress.MAX_RATE){
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            progress.setRate(progress.getRate() + 0.1f);
//        }
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        progress.destroy(true);
//
//        progress.init().setTitle("哈哈这个呢").begin();
//        while (progress.getRate() <= Progress.MAX_RATE){
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            progress.setRate(progress.getRate() + 0.1f);
//        }
//        try {
//            Thread.sleep(200);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        progress.destroy(true);
//    }
}
