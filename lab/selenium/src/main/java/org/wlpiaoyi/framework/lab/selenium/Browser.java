package org.wlpiaoyi.framework.lab.selenium;

import lombok.Getter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.*;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 偶然发现网页上有个异常提示：
 * “此网页上的问题导致 Internet Explorer 关闭并重新打开该选项卡”
 * 解决方法：
 * 第一步：
 * 打开IE，工具->Internet选项->高级->重置，弹出窗口，选项“删除个人设置”打上勾，确定重置，回到原始默认状态；
 * 第二步：
 * 禁用smartscreen，打开IE，工具->Internet选项->安全->选择Internet->自定义级别，找到使用 SmartScreen 筛选器，选择禁用，然后确定；
 * 第三步：
 * 打开IE，工具->Internet 选项，点击高级标签，在“加速的图形"下"使用软件呈现而不使用GPU呈现"前面打勾。
 * 第四步：
 * 重启浏览器，不需要重启电脑
 * ————————————————
 * 版权声明：本文为CSDN博主「yinlin330」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。
 * 原文链接：https://blog.csdn.net/yinlin330/article/details/86004720
 */
public class Browser {

    public static long OUTTIMEMILLISECONDS = 30000;

    @Getter
    private WebDriver driver;

    @Getter
    private Dimension dimension;

    @Getter
    private boolean optionHeadless;

    @Getter
    private boolean optionLoadimg;

    @Getter
    private String binaryPath;

    @Getter
    private String userDataPath;

    @Getter
    private String proxyServer;

    @Getter
    private String deviceName;

    @Getter
    private String url;

    @Getter
    private Set<Cookie> cookies;

    @Getter
    private String optionLang;

    @Getter
    private String driverPath;

    public Browser(){
//        this.deviceName = "IPhone X";
        this.optionLang = "zh_CN.UTF-8";
    }

    public void openFirfoxDriver(){
        System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/dev/null");

        FirefoxOptions options = new FirefoxOptions();
        if(!this.isOptionLoadimg()){
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("permissions.default.image", 2);//不显示图片
            profile.setPreference("permissions.default.stylesheet", 2);
            options.setProfile(profile);
        }

        if(!StringUtils.isBlank(this.binaryPath))options.setBinary(this.binaryPath);

        options.setHeadless(this.isOptionHeadless());
        if(this.optionLang != null && this.optionLang.length() > 0) options.addArguments("lang=" + this.optionLang);
        if(this.proxyServer != null && this.proxyServer.length() > 0){
            options.addArguments("proxy-server=http://" + this.proxyServer);
        }

        GeckoDriverService driverService;
        if(StringUtils.isBlank(this.driverPath)){
            driverService = GeckoDriverService.createDefaultService();
        }else {
            driverService = new GeckoDriverService.Builder()
                    .usingDriverExecutable(new File(this.driverPath))
                    .usingAnyFreePort().build();
        }
        this.driver = new FirefoxDriver(driverService, options);
        this.openDriver();
    }

    public void openChromeDriver(){
        ChromeOptions options = new ChromeOptions();
        //以最高权限运行
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-popup-blocking"); // 禁用阻止弹出窗口
//        options.addArguments("no-sandbox"); // 启动无沙盒模式运行
        options.addArguments("disable-extensions"); // 禁用扩展
        options.addArguments("no-default-browser-check"); // 默认浏览器检查
        if(!this.isOptionLoadimg()){
            HashMap<String, Object> prefs = new HashMap<String, Object>();
            prefs.put("profile.managed_default_content_settings.images", 2);
            prefs.put("profile.managed_default_content_settings.stylesheets", 2);
            options.setExperimentalOption("prefs", prefs);
            options.addArguments("--disable-gpu");
        }

        if(!StringUtils.isBlank(this.binaryPath))options.setBinary(this.binaryPath);
        if(this.isOptionHeadless()){
            options.addArguments("--headless");
        }
        if(this.optionLang != null && this.optionLang.length() > 0) options.addArguments("lang=" + this.optionLang);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36");
        if(this.deviceName != null && this.deviceName.length() > 0){
            final String deviceName = this.deviceName;
            options.setExperimentalOption("mobileEmulation", new HashMap<String, String>(){{
                put("deviceName", deviceName);
            }});
        }

        if(this.proxyServer != null && this.proxyServer.length() > 0){
            if(this.proxyServer.startsWith("http://")){
                options.addArguments("proxy-server=http://" + this.proxyServer);
            }else {
                options.addArguments("proxy-server=socks://" + this.proxyServer);
            }
        }

        if(!StringUtils.isBlank(this.userDataPath)){
            options.addArguments("user-data-dir="+this.userDataPath);
        }

        if(StringUtils.isBlank(this.driverPath)){
            ChromeDriverService driverService = ChromeDriverService.createDefaultService();
            this.driver = new ChromeDriver(driverService,options);
        }else {
            ChromeDriverService driverService = new ChromeDriverService.Builder()
                    .usingDriverExecutable(new File(this.driverPath))
                    .usingAnyFreePort().build();
            this.driver = new ChromeDriver(driverService,options);
        }
    }

    public void openDriver(){
        if(this.dimension == null){
            this.driver.manage().window().maximize();
        }else{
            this.driver.manage().window().setSize(this.dimension);
        }

        if(this.url == null)
            throw new BusinessException("the url can't be null");
        this.driver.manage().timeouts().pageLoadTimeout(OUTTIMEMILLISECONDS, TimeUnit.MILLISECONDS).setScriptTimeout(OUTTIMEMILLISECONDS, TimeUnit.MILLISECONDS);
        this.driver.get(this.url);
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
        if(this.driver != null) throw new BusinessException("can't set this value after loaded driver!!");
        this.proxyServer = proxyServer;
        return this;
    }

    public Browser setDeviceName(String deviceName) {
        if(this.driver != null) throw new BusinessException("can't set this value after loaded driver!!");
        this.deviceName = deviceName;
        return this;
    }

    public Browser setUrl(String url) {
        this.url = url;
        if(this.driver != null){
            this.driver.get(url);
        }
        return this;
    }

    public Browser setDimension(Dimension dimension) {
        this.dimension = dimension;
        if(this.driver != null){
            this.driver.manage().window().setSize(this.dimension);
        }
        return this;
    }

    public Browser setCookies(Set<Cookie> cookies) {
        this.cookies = cookies;
        if(this.driver == null) return this;
        synchronized (this.driver){
            this.driver.manage().deleteAllCookies();
            if(this.cookies == null || this.cookies.isEmpty()) return this;
            for (Cookie cookie : this.cookies){
                this.driver.manage().addCookie(cookie);
            }
        }
        return this;
    }

    public void addCookie(Cookie cookie) {
        if(this.driver == null) throw new BusinessException("can't set this value before loaded driver!!");
        this.driver.manage().addCookie(cookie);
    }

    public Browser setOptionHeadless(boolean optionHeadless) {
        if(this.driver != null) throw new BusinessException("can't set this value after loaded driver!!");
        this.optionHeadless = optionHeadless;
        return this;
    }

    public Browser setOptionLang(String optionLang) {
        if(this.driver != null) throw new BusinessException("can't set this value after loaded driver!!");
        this.optionLang = optionLang;
        return this;
    }

    public Browser setOptionLoadimg(boolean optionLoadimg) {
        if(this.driver != null) throw new BusinessException("can't set this value after loaded driver!!");
        this.optionLoadimg = optionLoadimg;
        return this;
    }

    public Browser setBinaryPath(String binaryPath) {
        if(this.driver != null) throw new BusinessException("can't set this value after loaded driver!!");
        this.binaryPath = binaryPath;
        return this;
    }

    public Browser setDriverPath(String driverPath) {
        if(this.driver != null) throw new BusinessException("can't set this value after loaded driver!!");
        this.driverPath = driverPath;
        return this;
    }

    public Browser setUserDataPath(String userDataPath) {
        if(this.driver != null) throw new BusinessException("can't set this value after loaded driver!!");
        this.userDataPath = userDataPath;
        return this;
    }
}
