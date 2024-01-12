package org.wlpiaoyi.framework.utils;

import lombok.Getter;
import lombok.Setter;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

public class Progress {

    private static final int PROGRESS_BAR = 50;    //控制输出的进度条宽度
    private static final int UNIT_SIZE = PROGRESS_BAR + 17 + 20;
    public static int MAX_RATE = 100;
    @Getter
    volatile float rate = 0;
    volatile boolean isRunning = false;
    long beginTimer;
    private static Progress xProgress = null;
    private Object synFlag = new Object();


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
        System.out.print(" RunTime:" + this.runTime());
        System.out.print(" RemainTime:" + this.remainTime());
    }

    private Progress(){}

    public final boolean begin(String title){
        if(isRunning){
            return false;
        }
        synchronized (this.synFlag){
            if(isRunning){
                return false;
            }
            this.beginTimer = System.currentTimeMillis();
            this.isRunning = true;
            try{

                StringBuffer kg = new StringBuffer();
                for(int i=0; i < PROGRESS_BAR; i++){
                    kg.append(undoFlag);
                }
                System.out.print(title + ":00.0%[>"+kg.toString()+"]");
                this.printTimer();
                rate = 0;
                while (rate < MAX_RATE){
                    printCurrentNum(rate);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                printCurrentNum(MAX_RATE);
                System.out.println();
            }finally {
                this.synFlag = false;
            }
        }
        return true;
    }

    public final void end(){
        this.setRate(Progress.MAX_RATE);
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.setRate(Progress.MAX_RATE + 1);
        this.isRunning = false;
    }

    private void focusGoto(){
        for(int i=UNIT_SIZE + 8; i > 0; i--){
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
//        Progress progress = new Progress();
//        new Thread(() -> progress.begin("test")).start();
//        while (progress.getRate() <= Progress.MAX_RATE){
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            progress.setRate(progress.getRate() + 0.01f);
//        }
//    }

//    public static void main(String[] args) {
//
//        Progress progress = new Progress();
//        new Thread(() -> progress.begin("test")).start();
//        progress.beginTimer = System.currentTimeMillis() - 56000;
//        while (progress.getRate() <= Progress.MAX_RATE){
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            for(int i=8; i > 0; i--){
//                System.out.print('\b');
//            }
//            System.out.print(progress.paresTime(System.currentTimeMillis() - progress.beginTimer));
//        }
//    }
}
