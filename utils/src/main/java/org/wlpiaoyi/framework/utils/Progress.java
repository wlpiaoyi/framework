package org.wlpiaoyi.framework.utils;

import lombok.Getter;
import lombok.Setter;

public class Progress {

    static final int PROGRESS_BAR = 50;    //控制输出的进度条宽度

    @Getter
    static int MAX_RATE = 100;
    @Getter @Setter
    volatile int rate = 0;


    public void begin(String title){
        StringBuffer kg = new StringBuffer();
        for(int i=0; i < PROGRESS_BAR; i++){
            kg.append("_");
        }
        System.out.print(title + ":00%[>"+kg.toString()+"]");
        rate = 0;
        while (rate <= MAX_RATE){
            printCurrentNum(rate);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

    public void end(){
        this.setRate(Progress.getMAX_RATE());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.setRate(Progress.getMAX_RATE() + 1);
    }

    private void focusGoto(){
        for(int i=PROGRESS_BAR + 6; i > 0; i--){
            System.out.print('\b');
        }
    }

    private void printCurrentNum(int i) {
        String num = "000" + i;
        num = num.substring(num.length() - 3);
        StringBuffer s = new StringBuffer(num + "%[");
        focusGoto();
        int prec = (i * 100) / 100;
        for (int index = 0; index < PROGRESS_BAR; index++) {
            int c = (index * 100) / PROGRESS_BAR;
            if (c < prec) {
                s.append("■");
            } else {
                s.append("_");
            }
        }
        s.append("]");
        System.out.print(s.toString());
    }
}
