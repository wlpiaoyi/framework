package org.wlpiaoyi.framework.lab.selenium;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.lab.selenium.for12123.BrowserBase;
import org.wlpiaoyi.framework.utils.ValueUtils;

@Slf4j
public class AppRun {
    static BrowserBase bc = null;

    public static void main(String[] args) {
        try {
            log.info("启动参数:{}", args);
            if(ValueUtils.isBlank(args)){
                throw new RuntimeException("请输入参数[0:拉取违章信息详情, 1:拉取违章信息列表,2:绑定司机]");
            }
            int type = Integer.parseInt(args[0]);
            switch (type){
                case 0: case 1: {
                    bc = new org.wlpiaoyi.framework.lab.selenium.for12123.violate.test.BrowserCabgov(type);
                    bc.start();
                }
                break;
                case 2: {
                    bc = new org.wlpiaoyi.framework.lab.selenium.for12123.lease.test.BrowserCabgov(type);
                    bc.start();
                }
                break;
                case 100: {
                    {
                        bc = new org.wlpiaoyi.framework.lab.selenium.for12123.violate.test.BrowserCabgov(0);
                        new Thread(() -> {
                            bc.start();
                        }).start();
                        try {
                            Thread.sleep((long) (1000 * 60 * 1.5));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        bc.getBrowser().quit();
                        try { Thread.sleep(1000); } catch (InterruptedException e) {}
                        bc.getBrowser().quit();
                    }
                    try {
                        Thread.sleep((long) (1000));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    {
                        bc = new org.wlpiaoyi.framework.lab.selenium.for12123.violate.test.BrowserCabgov(1);
                        new Thread(() -> {
                            bc.start();
                        }).start();
                        try {
                            Thread.sleep((long) (1000 * 60 * 1.5));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        bc.getBrowser().quit();
                        try { Thread.sleep(1000); } catch (InterruptedException e) {}
                        bc.getBrowser().quit();
                    }
                    try {
                        Thread.sleep((long) (1000));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    {
                        bc = new org.wlpiaoyi.framework.lab.selenium.for12123.lease.test.BrowserCabgov(2);
                        BrowserBase finalBc = bc;
                        new Thread(() -> {
                            finalBc.start();
                        }).start();
                        try {
                            Thread.sleep((long) (1000 * 60 * 1.5));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        bc.getBrowser().quit();
                        try {
                            Thread.sleep((long) (1000));
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
                break;
            }
        }catch (Exception e){
            log.error("启动失败", e);
        }finally {
            log.info("运行结束");
        }
    }
}
