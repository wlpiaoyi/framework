package org.wlpiaoyi.framework.lab.selenium.for12123;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.wlpiaoyi.framework.lab.selenium.Browser;
import org.wlpiaoyi.framework.lab.selenium.utils.WebElementUtils;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.security.RsaCipher;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
public class BrowserBase {


    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAI1zh4zfLhks3J8aI8KNa6vqeMFo\n" +
            "VE4w6k1iv3ehIR/I8EfnmxhyPMYtWp0Y3fo95Zn7kMqJPqetYPMM06f4iUclgZjP+FvMV7CxNVfM\n" +
            "rXQhA/1XtM6hOA9PowCYHwWUIp8wgGGNtE/MZNjuCLvfvGqAfKPykky7b4ejwqD+xStHAgMBAAEC\n" +
            "gYAHdnQURdq6tS8K0Su7s+Sy2aMMi2RkJpG8NVke/6803exYttHPqvpd92IKv96AckgbSJK2hMyX\n" +
            "7/5gmal5scybyvucDukXv/bTHhkwpR97tWTz0emiv8K+cFUlp6MKqVJXg8ZzeOsmpvsIV2NQk9mf\n" +
            "IAbFXH/07JjgN6lP/+x6pQJBANsPTgqWy3Zk0CWxaQe7DlyxtR2JFWOYsWUqnAd+HmCFcVTP0hjT\n" +
            "Zx07e3n5wjLff/vDsupB9C3397WUiyNbWCUCQQClTeglDLLQOk+fUqZKS4IlOzxg6LsCRalQvtaa\n" +
            "FCX24XfSv/iYGqE+JabNu8g1b0tMOcEFv48XUZ5LoMkQjxP7AkBlrFrai1bwIqaBeDB5iBaIa2rW\n" +
            "xJOK4IolnHtC9wR+ZDFP3g1zvFs1tDABUy0Rk67BWfmmxOnilB8Cxmk2BeWJAkA3QO1BxRbYB0Wq\n" +
            "CaRP3SFpdH1gHyqzPbm0pbVx1x5BgWfd6BEeNniDH268AfKP+d1/Yyaj1z3rG3r/6ISMpmaVAkA/\n" +
            "8RLymj8PIJHvVtBkxP0pHsnhMmlKwKO4C1xu26HtQXu/GDwlKnayQGTqq2Oiuu0hki+HQBwmBLjF\n" +
            "pRbgUsVd";
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCNc4eM3y4ZLNyfGiPCjWur6njBaFROMOpNYr93\n" +
            "oSEfyPBH55sYcjzGLVqdGN36PeWZ+5DKiT6nrWDzDNOn+IlHJYGYz/hbzFewsTVXzK10IQP9V7TO\n" +
            "oTgPT6MAmB8FlCKfMIBhjbRPzGTY7gi737xqgHyj8pJMu2+Ho8Kg/sUrRwIDAQAB";

    protected final String DATA_PATH = System.getProperty("user.dir") + "/data";
    protected final String CONFIG_PATH = System.getProperty("user.dir") + "/fw_config/selenium";
    @Getter
    protected final Browser browser;
    protected final String cookies;

//    protected final String localName = "海南省";
//    protected final String browserUlr = "https://hi.122.gov.cn/views/memrent/vehlist.html";
//    protected final String localName = "四川省";
//    protected final String browserUlr = "https://sc.122.gov.cn/views/memrent/vehlist.html";
    protected final String localName;
    protected final String browserUlr;
    private final Long curDateL;

    protected final int type;

    private String[] loadCurDateValue(){
        String path = CONFIG_PATH + "/cur_date.dat";
        log.info("BrowserBase.loadCurDateValue in. 读取到期配置文件:{}", path);

        try {
            byte[] value = ReaderUtils.loadBytes(new File(path));
            RsaCipher cipher = RsaCipher.build(0).setPrivateKey(this.privateKey).setPublicKey(this.publicKey).loadConfig();
            String dText = new String(
                    cipher.decrypt(
                            DataUtils.base64Decode(value)
                    ),
                    StandardCharsets.UTF_8
            );
            log.info("BrowserBase.loadCurDateValue end. 读取到期配置文件");
            return dText.split(",");
        } catch (IOException e) {
            log.error("BrowserBase.loadCurDateValue error. 读取配置文件错误", e);
            throw new RuntimeException(e);
        }
    }

    protected interface Validate{
        long getNowTime();
    }

    protected void checkValid(Validate validate){
        try{
            log.info("BrowserBase.checkValid in.");
            long vTime = DateUtils.formatDate(this.curDateL + "", "yyyyMMdd").getTime();
            long nowTime = validate.getNowTime();
            log.info("BrowserBase.checkValid. vTime:{} nowTime:{}", DateUtils.formatDate(new Date(vTime)), DateUtils.formatDate(new Date(nowTime)));
            if(vTime < nowTime){
                System.exit(0);
            }
        } catch (Exception e) {
            log.error("BrowserBase.checkValid error. 验证到期时间错误", e);
            System.exit(0);
        }
    }

    private String loadHeadUserAgent(){
        String path = CONFIG_PATH + "/header_useragent.txt";
        log.info("BrowserBase.loadHeadUserAgent in. 读取请求头配置文件:{}", path);
        try {
            File file = new File(path);
            if(!file.exists()) return null;
            byte[] value = ReaderUtils.loadBytes(file);
            return new String(value);
        } catch (IOException e) {
            log.warn("BrowserBase.loadHeadUserAgent error. 读取球球头配置文件错误", e);
            throw new RuntimeException(e);
        }finally {
            log.info("BrowserBase.loadHeadUserAgent end. 读取请求头配置文件");
        }
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
        String[] curDateValue = this.loadCurDateValue();
        this.curDateL = Long.parseLong(curDateValue[0]);
        this.localName = curDateValue[1];
        this.browserUlr = curDateValue[2];
        try {
            this.cookies = ReaderUtils.loadString(CONFIG_PATH + "/cookies.txt", null).replaceAll("\n","").replaceAll("\r","");
        } catch (IOException e) {
            log.error("BrowserBase.create error. 读取cookies错误", e);
            throw new RuntimeException(e);
        }
        log.warn("BrowserBase.create cookies:{}", cookies);

        browser = Browser.createDefault()
                .setUserAgent(this.loadHeadUserAgent())
                .setDisableAutomationFlag(true)
                .setStealthMode(true)
                .setTimeoutMs(60000)
                .setDriverPath(CONFIG_PATH +"/chromedriver");
        File file = new File(CONFIG_PATH +"/chrome-app/chrome.exe");
        if(file.exists()){
            browser.setBinaryPath(file.getAbsolutePath());
        }
        log.info("BrowserBase.create chromedriver:{}", CONFIG_PATH +"/chromedriver");
        browser.init();
        log.info("BrowserBase.create");


    }


    public boolean start(){
        log.info("BrowserBase.start in. 启动浏览器");
        try{
            browser.navigateTo(browserUlr);
            if(ValueUtils.isNotBlank(this.cookies)){
                String args[] = this.cookies.split("; ");
                Set<Cookie> cookies = new HashSet<>();
                for (String arg : args){
                    String as[] = arg.split("=");
                    cookies.add(new Cookie(as[0], as[1]));
                }
                this.browser.setCookies(cookies);
                browser.navigateTo(browserUlr);
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