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

    protected String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIwgd+H2N2wAAPEHEi8ypKdwaB2I\n" +
            "ouHQGfI/oXpA8hJFBnq7h/OF/xVm2TN+i5Y4GOCK2TdfgtGa10ed0xwUb13eu6oFtuo1VHCAiSzC\n" +
            "CbIVutyVysY4l7HvhAJvH1KlHRLRQU4sFNNgdrdYJwSV4hcUU62pgBGIyDFadTetVnW/AgMBAAEC\n" +
            "gYAziVd+IEe27XNrMl4SRM6BFJr+TbwWUCrSyWtS4uMFLCTba/Bu9Nfh368/vKmLCLvBjd+g+XxM\n" +
            "KeZGnTnBKJTihnKw4AwqmVN1Sr1RTnXwJ6eNGSitNEqaYhGU4aEwr+714ZkVsVY5v7vTjZJ2hTDr\n" +
            "ksdZd0llGHG1umy7CYyE0QJBAMVPc6813nJ6rF/v8KQqVfIhO1qChb4BH47zaegMGOS4NYEgdNjK\n" +
            "YmOIHh47+GvVQj5aTbmPScXZySEJ4Z5eYQ8CQQC1zqzDaPTN4Ts46JfrpNJhUjJOFr/dqAUfifln\n" +
            "UsGYrPtthviDrMzemnT+hq9HIXRM+fYsWn8QN0/teainakBRAkBYvty0kNElwoFngT9GR3hyuHm+\n" +
            "wvgutsif/mHDKjXEIgqGsrd7jsPkKqQJS0X4EmqCKxHMhWNUJxmsz4n4NlEHAkA09qR1uNm4MGkk\n" +
            "Rv4a88Ul/OASx6XVWOFFMtipNP6ZD6ufWLaFBY4ZOz3h+DKPsjtDQX5ppWNmwfZS5CIxw05BAkAn\n" +
            "HGet1e6kl9bGv+8LXsE2/JHHr97dS52I6xWkdW5yp5/OmV0X90NF4P7Fb5zE870lWG3/orBdRqgp\n" +
            "4JTodTCj";
    protected String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMIHfh9jdsAADxBxIvMqSncGgdiKLh0BnyP6F6\n" +
            "QPISRQZ6u4fzhf8VZtkzfouWOBjgitk3X4LRmtdHndMcFG9d3ruqBbbqNVRwgIkswgmyFbrclcrG\n" +
            "OJex74QCbx9SpR0S0UFOLBTTYHa3WCcEleIXFFOtqYARiMgxWnU3rVZ1vwIDAQAB";

    protected final String CONFIG_PATH = System.getProperty("user.dir") + "/config/selenium";
    protected final Browser browser;
    protected final String cookies;
    protected final Long curDateL;

    protected final int type;

    private Long loadCurDateValue(){
        log.info("BrowserBase.loadCurDateValue in. 读取到期配置文件");
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
//        browser = new Browser().setOptionHeadless(false).setUrl("https://sc.122.gov.cn/views/memrent/vehlist.html");
        browser = new Browser().setOptionHeadless(false).setUrl("https://hi.122.gov.cn/views/memrent/vehlist.html");
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


    protected boolean start(){
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