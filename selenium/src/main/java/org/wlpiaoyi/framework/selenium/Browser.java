package org.wlpiaoyi.framework.selenium;

import lombok.Getter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.wlpiaoyi.framework.utils.OSUtils;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Browser {


    @Getter
    private WebDriver driver;

    @Getter
    private Dimension dimension;

    @Getter
    private boolean optionHeadless;

    @Getter
    private String proxyServer;

    @Getter
    private String deviceName;

    @Getter
    private String url;

    @Getter
    private Map<String, String> cookies;

    @Getter
    private String optionLang;

    @Getter
    private String driverPath;

    public Browser(){
        this.deviceName = "iPhone X";
        this.optionLang = "zh_CN.UTF-8";
    }


    public void openDriver(){

        ChromeOptions option = new ChromeOptions();

        if(this.isOptionHeadless()) option.addArguments("headless");

        if(this.optionLang != null && this.optionLang.length() > 0) option.addArguments("lang=" + this.optionLang);

        option.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        if(this.deviceName != null && this.deviceName.length() > 0){
            final String deviceName = this.deviceName;
            option.setExperimentalOption("mobileEmulation", new HashMap<String, String>(){{
                put("deviceName", deviceName);
            }});
        }
        if(this.proxyServer != null && this.proxyServer.length() > 0){
            option.addArguments("proxy-server=http://" + this.proxyServer);
        }


        if(OSUtils.isWindows()){
            if(StringUtils.isBlank(this.driverPath))
                throw new BusinessException("the driverPath can't be null!");
        }
        if(StringUtils.isBlank(this.driverPath)){
            ChromeDriverService driverService = ChromeDriverService.createDefaultService();
            this.driver = new ChromeDriver(driverService,option);
        }else {
            ChromeDriverService driverService = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File(this.driverPath))
                    .usingAnyFreePort().build();
            this.driver = new ChromeDriver(driverService,option);
        }

        if(this.url == null)
            throw new BusinessException("the url can't be null");

        this.driver.get(this.url);
        if(this.cookies != null){
            for (Map.Entry<String, String> entry : this.cookies.entrySet()){
                driver.manage().addCookie(new Cookie(entry.getKey(), entry.getValue()));
            }
        }
        this.driver.get(this.url);

        if(this.dimension == null){
            this.driver.manage().window().maximize();
        }else{
            this.driver.manage().window().setSize(this.dimension);
        }

        this.driver = driver;
    }

    /**
     * 在当前窗口打开新的连接
     * @param link
     */
    public void openLinkInThis(String link){
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("window.location.href='" + link + "'");
    }

    /**
     * 关闭窗口
     */
    public void quit(){
        this.driver.quit();
    }

    public Browser setProxyServer(String proxyServer) {
        this.proxyServer = proxyServer;
        return this;
    }

    public Browser setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    public Browser setUrl(String url) {
        this.url = url;
        return this;
    }

    public Browser setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
        return this;
    }

    public Browser setOptionHeadless(boolean optionHeadless) {
        this.optionHeadless = optionHeadless;
        return this;
    }

    public Browser setOptionLang(String optionLang) {
        this.optionLang = optionLang;
        return this;
    }

    public Browser setDriverPath(String driverPath) {
        this.driverPath = driverPath;
        return this;
    }
}
