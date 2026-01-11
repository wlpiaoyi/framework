package org.wlpiaoyi.framework.lab.selenium.for12123;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.wlpiaoyi.framework.lab.selenium.Browser;
import org.wlpiaoyi.framework.lab.selenium.utils.WebElementUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.security.RsaCipher;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class BrowserBase {

    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJ+8wUIHMZHsdSJKfXkBaYRHKzuN\n" +
            "SP23Q6Jvsi0X/GVN3320ylAvs7QSwRWS+FEXMRqpzMcpivjUarz7q/Qw58WFH0aMzmx2pmxhlkUs\n" +
            "Y/LxUVAtM1DfPKPohZ+a06D9tk4+hnGwVQGEwX9oBwp78VZzMzdAY5KdbceD2oQS/qP7AgMBAAEC\n" +
            "gYBF2FLofBzAoZPWGpwifOuWW0gcEfsIdUmtjQlrjkFeSl6eqJ6N0U3SPyEOPeU2D934uqY/r3qE\n" +
            "sty5JZJag8fTdP8StHJzUm2b2Mr6Sfb/ROTBhx6mdN5+S0wr8M3I6918ZZ1qAiIFuAkKWsQGxbvU\n" +
            "FUwQVrHEtDJNjXeS4miiAQJBANTNwdPRIBzw0w5kakWPRYQ9HoWE3TSXdf9rxNpDcgpFftsR7QbF\n" +
            "6SSBIKl7xGLqaxQFbAmZdQNt4WEfEwpBJOsCQQDAKW1kd7nL6FtgSLKMoEexFLVWNLD0f9g0MTtC\n" +
            "+kxz4jprqGhdaLfNwJAqnmnjQm+4p/Ra7wjSij8LYz8PWvkxAkBnivYUqlyFuGf5SMKstdmNTm/b\n" +
            "Z5p6THgNn9JYoRiMBuSCk2ZRNVsLeAj8bkxQFN+lDj5TLWfSE1TmfMg25RuhAkBvEkMJ1G5PX3IZ\n" +
            "uEuEH0zxHTAnsPMrkA3vNRm1ACpavUPZYJFalKHRSuHJ0KER3B/pkyMZwJrP31rLgUU84e+xAkA4\n" +
            "mpZ88Vn8vDbOZY35DbgH6hipcIh09tN/V03v/TBsfD/pDbEaiU8LIvK8jm1Z9qhl4rahXj4XdImv\n" +
            "FmkVhTLy";
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfvMFCBzGR7HUiSn15AWmERys7jUj9t0Oib7It\n" +
            "F/xlTd99tMpQL7O0EsEVkvhRFzEaqczHKYr41Gq8+6v0MOfFhR9GjM5sdqZsYZZFLGPy8VFQLTNQ\n" +
            "3zyj6IWfmtOg/bZOPoZxsFUBhMF/aAcKe/FWczM3QGOSnW3Hg9qEEv6j+wIDAQAB";
    protected final String localName = "四川省";
    protected final String browserUlr = "https://sc.122.gov.cn/views/memrent/vehlist.html";

//    protected final String localName = "海南省";
//    protected final String browserUlr = "https://hi.122.gov.cn/views/memrent/vehlist.html";;

    protected final String DATA_PATH = System.getProperty("user.dir") + "/data";
    protected final String CONFIG_PATH = System.getProperty("user.dir") + "/config/selenium";
    protected final Browser browser;
    protected final String cookies;
    protected final Long curDateL;

    protected final int type;

    private Long loadCurDateValue(){
        log.info("BrowserBase.loadCurDateValue in. " +
                "读取到期配置文件");
        try {
            byte[] value = ReaderUtils.loadBytes(new File(CONFIG_PATH + "/cur_date.dat"));
            RsaCipher cipher = RsaCipher.build(0).setPrivateKey(this.privateKey).setPublicKey(this.publicKey).loadConfig();
            String dText = new String(
                    cipher.decrypt(
                            DataUtils.base64Decode(value)
                    ),
                    StandardCharsets.UTF_8
            );
            return Long.parseLong(dText);
        } catch (IOException e) {
            log.error("BrowserBase.loadCurDateValue error. 读取配置文件错误", e);
        }
        log.info("BrowserBase.loadCurDateValue end. 读取到期配置文件");
        return 0L;
    }

    protected boolean checkLocal(){
        int i = 10;
        while (i -- > 0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            try{
                WebElement ele = this.browser.getDriver().findElement(By.id("district-name"));
                if(ele == null){
                    continue;
                }
                String text = WebElementUtils.getValue(ele);
                if(text.equals(localName)){
                    return true;
                }
            }catch (Exception e){
            }
        }
        return false;
    }

    protected BrowserBase(int type){
        log.info("BrowserBase.create in. 创建浏览器实例");
        this.type = type;
        log.warn("BrowserBase.create type:{}", type);
        this.curDateL = this.loadCurDateValue();
        try {
            this.cookies = ReaderUtils.loadString(CONFIG_PATH + "/cookies.txt", null).replaceAll("\n","").replaceAll("\r","");
        } catch (IOException e) {
            log.error("BrowserBase.create error. 读取cookies错误", e);
            throw new RuntimeException(e);
        }
        log.warn("BrowserBase.create cookies:{}", cookies);
        browser = new Browser().setOptionHeadless(false).setUrl(this.browserUlr);
        log.info("BrowserBase.create  创建浏览器实例:{}", this.browser.getUrl());

//        this.browser.setOptionHeadless(true);
        this.browser.setOptionLoadimg(true);
        this.browser.setDriverPath(CONFIG_PATH +"/chromedriver");
//        Runtime.getRuntime().addShutdownHook(new RTMServer(this.browser));
        // 添加一个shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("检测到程序即将关闭...");
            log.warn("browser start quit");
            try {
                this.browser.getDriver().close();
            }catch (Exception e){}
            try {
                this.browser.getDriver().quit();
            }catch (Exception e){}
            try {
                this.browser.quit();
            }catch (Exception e){}
            log.warn("browser quit success");
        }));

    }


    public boolean start(){
        log.info("BrowserBase.start in. 启动浏览器");
        try{
            browser.openChromeDriver();
            browser.openDriver();
            if(ValueUtils.isNotBlank(this.cookies)){
                String args[] = this.cookies.split("; ");
                Set<Cookie> cookies = new HashSet<>();
                for (String arg : args){
                    String as[] = arg.split("=");
                    cookies.add(new Cookie(as[0], as[1]));
                }
                this.browser.setCookies(cookies);;
                browser.openDriver();
            }else {
                log.info("BrowserBase.start cookies is null");
            }
        }catch (Exception e) {
            log.error("BrowserBase.start set cookies error", e);
            browser.quit();
            return false;
        }finally {
            log.info("BrowserBase.start end");
        }
        return true;
    }


    protected boolean clickFeed(){
        log.info("BrowserBase.clickFeed in");
        try{
            WebElement webElement = browser.getDriver().findElement(By.className("aui_state_highlight"));
            if(webElement != null){
                WebElementUtils.click(browser, webElement);
                log.info("BrowserBase.clickFeed click success");
                return true;
            }
        }catch (Exception e){
            log.warn("click feed error:{}", e.getMessage());
        }
        return false;

    }
}