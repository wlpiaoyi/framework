package org.wlpiaoyi.framework.lab.selenium;

import lombok.extern.slf4j.Slf4j;
import org.wlpiaoyi.framework.lab.selenium.for12123.BrowserBase;
import org.wlpiaoyi.framework.utils.ValueUtils;

@Slf4j
public class AppRun {

    public static void main(String[] args) {
        try {
            log.info("启动参数:{}", args);
            if(ValueUtils.isBlank(args)){
                throw new RuntimeException("请输入参数[0:拉取违章信息详情, 1:拉取违章信息列表,2:绑定司机]");
            }
            BrowserBase bc = null;
            int type = Integer.parseInt(args[0]);
            switch (type){
                case 0: case 1: {
                    bc = new org.wlpiaoyi.framework.lab.selenium.for12123.violate.test.BrowserCabgov(type);
                }
                break;
                case 2: {
                    bc = new org.wlpiaoyi.framework.lab.selenium.for12123.lease.test.BrowserCabgov(type);
                }
                break;
            }
            bc.start();
        }catch (Exception e){
            log.error("启动失败", e);
        }finally {
            log.info("运行结束");
        }
    }
}
