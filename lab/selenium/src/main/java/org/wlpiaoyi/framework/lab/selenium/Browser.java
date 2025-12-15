package org.wlpiaoyi.framework.lab.selenium;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * Selenium 浏览器封装类 <br/>
 * 支持 Chrome 浏览器，包含反检测和代理等功能
 * 主要功能：
 * 1. Chrome浏览器自动化控制
 * 2. 反检测机制（防止被网站识别为自动化程序）
 * 3. 代理设置支持（HTTP、SOCKS4、SOCKS5）
 * 4. 设备模拟（移动端/桌面端）
 * 5. 无头模式支持
 * 6. 截图功能
 * 7. Cookie管理
 * 8. 多窗口/标签页管理
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025/12/14 16:56</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
@Slf4j
@Getter
public class Browser {

    // ============ 常量定义 ============

    /** 默认超时时间（毫秒） */
    public static final long DEFAULT_TIMEOUT_MS = 30000;

    /** 默认用户代理（模拟真实浏览器） */
    public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";

    /** 默认语言设置 */
    public static final String DEFAULT_LANGUAGE = "zh-CN";

    // ============ 实例变量 ============

    /** Selenium WebDriver 实例 */
    private WebDriver driver;

    /** 浏览器窗口尺寸 */
    private Dimension dimension;

    /** 额外的浏览器能力配置 */
    private final Map<String, String> capabilities = new HashMap<>();

    // ============ 配置参数 ============

    /** 页面加载和脚本执行的超时时间（毫秒） */
    private long timeoutMs = DEFAULT_TIMEOUT_MS;

    /** 是否启用无头模式（不显示浏览器界面） */
    private boolean headless = false;

    /** 是否加载图片 */
    private boolean loadImages = true;

    /** 是否启用反检测模式 */
    private boolean stealthMode = true;

    /** 是否禁用自动化标志（防止被检测为自动化程序） */
    private boolean disableAutomationFlag = true;

    /** 是否启用代理 */
    private boolean enableProxy = false;

    // ============ 路径配置 ============

    /** Chrome浏览器可执行文件路径 */
    private String binaryPath;

    /** Chrome用户数据目录路径（用于保存cookies、历史记录等） */
    private String userDataPath;

    /** ChromeDriver可执行文件路径 */
    private String driverPath;

    // ============ 网络配置 ============

    /** 代理服务器地址（格式：host:port） */
    private String proxyServer;

    /** 代理类型：http、socks4、socks5 */
    private String proxyType = "http";

    // ============ 设备模拟 ============

    /** 设备名称（用于移动端设备模拟） */
    private String deviceName;

    /** 自定义设备模拟配置 */
    private Map<String, Object> mobileEmulation;

    // ============ 语言和用户代理 ============

    /** 浏览器语言设置 */
    private String language = DEFAULT_LANGUAGE;

    /** 自定义用户代理字符串 */
    private String userAgent = DEFAULT_USER_AGENT;

    // ============ 状态变量 ============

    /** 当前页面的URL */
    private String currentUrl;

    /** WebDriver等待对象（用于显式等待） */
    private WebDriverWait wait;

    // ============ 核心方法 ============

    /**
     * 构建 ChromeOptions 配置对象
     * 该方法整合所有配置参数，创建并配置 ChromeOptions 实例
     *
     * @return 配置完成的 ChromeOptions 实例
     */
    private ChromeOptions buildChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        // 基本参数
        addBasicArguments(options);

        // 反检测参数
        if (stealthMode) {
            addStealthModeArguments(options);
        }

        // 性能优化参数
        addPerformanceArguments(options);

        // 代理设置
        if (enableProxy && ValueUtils.isNotBlank(proxyServer)) {
            addProxyArguments(options);
        }

        // 用户数据目录（用于持久化cookies和设置）
        if (ValueUtils.isNotBlank(userDataPath)) {
            options.addArguments("user-data-dir=" + userDataPath);
        }

        // 设备模拟
        if (ValueUtils.isNotBlank(deviceName)) {
            Map<String, String> mobileEmulation = new HashMap<>();
            mobileEmulation.put("deviceName", deviceName);
            options.setExperimentalOption("mobileEmulation", mobileEmulation);
        } else if (this.mobileEmulation != null && !this.mobileEmulation.isEmpty()) {
            options.setExperimentalOption("mobileEmulation", this.mobileEmulation);
        }

        // 指定Chrome浏览器可执行文件路径
        if (ValueUtils.isNotBlank(binaryPath)) {
            options.setBinary(binaryPath);
        }

        // 实验性选项
        addExperimentalOptions(options);

        return options;
    }

    /**
     * 添加基本浏览器参数
     * 这些参数是Chrome浏览器启动时的命令行参数
     *
     * @param options ChromeOptions对象
     */
    private void addBasicArguments(ChromeOptions options) {
        options.addArguments(
                "--no-sandbox",                    // 禁用沙盒模式（在Docker等环境中需要）
                "--disable-popup-blocking",       // 禁用弹出窗口阻止
                "--disable-gpu",                  // 禁用GPU加速（在无头模式下建议禁用）
                "--disable-extensions",           // 禁用扩展程序
                "--no-default-browser-check",     // 不检查默认浏览器
                "--disable-web-security",         // 禁用同源策略（用于测试）
                "--allow-running-insecure-content", // 允许运行不安全内容
                "--disable-notifications",        // 禁用通知
                "--disable-infobars",             // 禁用信息栏（如"Chrome正受到自动测试软件控制"）
                "--disable-bundled-ppapi-flash",  // 禁用内置Flash
                "--disable-dev-shm-usage",        // 避免使用/dev/shm内存不足的问题（在Docker中常见）
                "--lang=" + language,             // 设置浏览器语言
                "--user-agent=" + userAgent       // 设置用户代理
        );

        // 无头模式
        if (headless) {
            // Chrome 112+ 推荐使用新的无头模式，渲染更稳定
            options.addArguments("--headless=new");
        }
    }

    /**
     * 添加反检测相关参数
     * 这些参数有助于避免被网站识别为自动化程序
     *
     * @param options ChromeOptions对象
     */
    private void addStealthModeArguments(ChromeOptions options) {
        options.addArguments(
                // 禁用自动化控制特征（关键的反检测参数）
                "--disable-blink-features=AutomationControlled",
                // 禁用WebRTC IP泄漏保护（减少浏览器指纹特征）
                "--disable-features=WebRtcHideLocalIpsWithMdns"
        );

        // 隐藏自动化特征
        if (disableAutomationFlag) {
            // 排除自动化相关的switches
            options.setExperimentalOption("excludeSwitches",
                    Arrays.asList("enable-automation", "enable-logging"));
            // 禁用自动化扩展
            options.setExperimentalOption("useAutomationExtension", false);
        }
    }

    /**
     * 添加性能优化相关参数
     * 这些参数可以提升浏览器性能和减少资源消耗
     *
     * @param options ChromeOptions对象
     */
    private void addPerformanceArguments(ChromeOptions options) {
        Map<String, Object> prefs = new HashMap<>();

        // 不加载图片时，设置内容类型为阻止
        if (!loadImages) {
            prefs.put("profile.managed_default_content_settings.images", 2);      // 2表示阻止
            prefs.put("profile.managed_default_content_settings.stylesheets", 2); // CSS
            prefs.put("profile.managed_default_content_settings.plugins", 2);     // 插件
            prefs.put("profile.managed_default_content_settings.popups", 2);      // 弹出窗口
            prefs.put("profile.managed_default_content_settings.geolocation", 2); // 地理位置
            prefs.put("profile.managed_default_content_settings.notifications", 2); // 通知
        }

        // 禁用保存密码提示
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);

        // 禁用内置PDF查看器，直接下载PDF文件
        prefs.put("plugins.always_open_pdf_externally", true);

        // 如果有偏好设置，添加到选项
        if (!prefs.isEmpty()) {
            options.setExperimentalOption("prefs", prefs);
        }
    }

    /**
     * 添加代理相关参数
     * 支持HTTP、SOCKS4、SOCKS5代理
     *
     * @param options ChromeOptions对象
     */
    private void addProxyArguments(ChromeOptions options) {
        String proxyArg;
        switch (proxyType.toLowerCase()) {
            case "socks4":
                proxyArg = "socks4://" + proxyServer;
                break;
            case "socks5":
                proxyArg = "socks5://" + proxyServer;
                break;
            case "http":
            default:
                proxyArg = "http://" + proxyServer;
                break;
        }
        options.addArguments("--proxy-server=" + proxyArg);
    }

    /**
     * 添加实验性选项
     * 这些是通过setExperimentalOption设置的选项
     *
     * @param options ChromeOptions对象
     */
    private void addExperimentalOptions(ChromeOptions options) {
        Map<String, Object> experimentalOptions = new HashMap<>();

        // 禁用密码保存提示（已在prefs中设置，这里为了兼容性保留）
        experimentalOptions.put("credentials_enable_service", false);
        experimentalOptions.put("profile.password_manager_enabled", false);

        // 添加其他自定义的实验性选项
        for (Map.Entry<String, String> entry : capabilities.entrySet()) {
            experimentalOptions.put(entry.getKey(), entry.getValue());
        }

        // 添加排除switches的实验性选项
        if (!experimentalOptions.isEmpty()) {
            options.setExperimentalOption("excludeSwitches",
                    Collections.singletonList("enable-automation"));
        }
    }

    /**
     * 创建 ChromeDriver 服务
     * 配置ChromeDriver的启动参数和服务选项
     *
     * @return 配置完成的ChromeDriverService实例
     */
    private ChromeDriverService createChromeDriverService() {
        ChromeDriverService.Builder builder = new ChromeDriverService.Builder();

        // 指定ChromeDriver可执行文件路径
        if (ValueUtils.isBlank(driverPath)) {
            throw new BusinessException("请指定ChromeDriver可执行文件路径");
        }
        {
            File driverFile = new File(driverPath);
            if (!driverFile.exists()) {
                throw new BusinessException("ChromeDriver可执行文件不存在");
            }
            builder.usingDriverExecutable(driverFile);
        }

        // 静默模式，减少控制台输出
        builder.withSilent(true);

        return builder.build();
    }

    /**
     * 初始化浏览器实例
     * 这是核心的初始化方法，会创建WebDriver实例并进行基础配置
     *
     * @throws BusinessException 如果初始化失败
     */
    public void init() {
        try {
            // 1. 构建Chrome选项
            ChromeOptions options = buildChromeOptions();

            // 2. 创建ChromeDriver服务
            ChromeDriverService service = createChromeDriverService();

            // 3. 创建ChromeDriver实例
            this.driver = new ChromeDriver(service, options);

            // 4. 设置窗口大小
            if (dimension != null) {
                driver.manage().window().setSize(dimension);
            } else {
                driver.manage().window().maximize(); // 默认最大化窗口
            }

            // 5. 设置各种超时
            driver.manage().timeouts()
                    .pageLoadTimeout(timeoutMs, TimeUnit.MILLISECONDS)  // 页面加载超时
                    .setScriptTimeout(timeoutMs, TimeUnit.MILLISECONDS) // 脚本执行超时
                    .implicitlyWait(timeoutMs, TimeUnit.MILLISECONDS);  // 隐式等待超时

            // 6. 创建WebDriverWait实例（用于显式等待）
            this.wait = new WebDriverWait(driver, 60);

            // 7. 应用反检测脚本
            if (stealthMode) {
                applyAntiDetectionScripts();
            }

            log.info("Browser initialized successfully");

            // 添加一个shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("检测到程序即将关闭...");
                log.warn("browser start quit");
                this.quit();
                log.warn("browser quit success");
            }));

        } catch (Exception e) {
            log.error("Failed to initialize browser", e);
            throw new BusinessException("Failed to initialize browser", e);
        }
    }

    /**
     * 应用反检测JavaScript脚本
     * 通过执行JavaScript来修改浏览器的navigator属性，避免被检测为自动化程序
     */
    private void applyAntiDetectionScripts() {
        try {
            // 使用文本块定义JavaScript反检测脚本
            String script = """
                // 移除navigator.webdriver属性（自动化程序的关键标志）
                Object.defineProperty(navigator, 'webdriver', {
                    get: () => undefined
                });
                
                // 修改语言属性，使其更像真实浏览器
                Object.defineProperty(navigator, 'languages', {
                    get: () => ['%s', 'en-US', 'en']
                });
                
                // 修改插件属性，避免插件数量为0（自动化程序的典型特征）
                Object.defineProperty(navigator, 'plugins', {
                    get: () => [1, 2, 3, 4, 5]
                });
                
                // 修改chrome属性，避免chrome.runtime不存在
                window.chrome = {
                    runtime: {},
                    loadTimes: function() {},
                    csi: function() {},
                    app: {}
                };
                """.formatted(language);

            // 执行JavaScript脚本
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(script);

        } catch (Exception e) {
            log.warn("Failed to apply anti-detection scripts", e);
        }
    }

    /**
     * 导航到指定URL
     *
     * @param url 要访问的URL地址
     * @throws BusinessException 如果浏览器未初始化或导航失败
     */
    public void navigateTo(String url) {
        if (driver == null) {
            throw new BusinessException("Browser not initialized. Call init() first.");
        }

        try {
            driver.get(url);
            this.currentUrl = url;
            log.debug("Navigated to: {}", url);
        } catch (Exception e) {
            log.error("Failed to navigate to: {}", url, e);
            throw new BusinessException("Failed to navigate to URL: " + url, e);
        }
    }

    /**
     * 在当前窗口打开新链接
     * 使用JavaScript修改当前窗口的location.href
     *
     * @param link 要打开的链接
     * @throws BusinessException 如果浏览器未初始化或操作失败
     */
    public void openLinkInCurrentWindow(String link) {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }

        try {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("window.location.href = arguments[0]", link);
            this.currentUrl = link;
            log.debug("Opened link in current window: {}", link);
        } catch (Exception e) {
            log.error("Failed to open link in current window: {}", link, e);
            throw new BusinessException("Failed to open link: " + link, e);
        }
    }

    /**
     * 在新标签页打开链接
     * 使用JavaScript的window.open方法
     *
     * @param link 要打开的链接
     * @throws BusinessException 如果浏览器未初始化或操作失败
     */
    public void openLinkInNewTab(String link) {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }

        try {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("window.open(arguments[0], '_blank')", link);
            log.debug("Opened link in new tab: {}", link);
        } catch (Exception e) {
            log.error("Failed to open link in new tab: {}", link, e);
            throw new BusinessException("Failed to open link in new tab: " + link, e);
        }
    }

    /**
     * 执行JavaScript脚本
     *
     * @param script 要执行的JavaScript代码
     * @param args 传递给脚本的参数
     * @return 脚本执行结果的返回值
     * @throws BusinessException 如果浏览器未初始化或执行失败
     */
    public Object executeScript(String script, Object... args) {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }

        try {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            return executor.executeScript(script, args);
        } catch (Exception e) {
            log.error("Failed to execute script", e);
            throw new BusinessException("Failed to execute script", e);
        }
    }

    /**
     * 等待元素出现
     * 使用显式等待等待指定定位器的元素出现在DOM中
     *
     * @param locator 元素定位器
     * @return 找到的WebElement
     * @throws BusinessException 如果WebDriverWait未初始化
     */
    public WebElement waitForElement(By locator) {
        if (wait == null) {
            throw new BusinessException("WebDriverWait not initialized");
        }

        return wait.until(d -> d.findElement(locator));
    }

    /**
     * 等待元素可点击
     * 等待元素不仅存在，而且可见并可点击
     *
     * @param locator 元素定位器
     * @return 可点击的WebElement
     * @throws BusinessException 如果WebDriverWait未初始化
     */
    public WebElement waitForElementClickable(By locator) {
        if (wait == null) {
            throw new BusinessException("WebDriverWait not initialized");
        }

        return wait.until(d -> {
            WebElement element = d.findElement(locator);
            // 检查元素是否可见且可用
            return (element != null && element.isDisplayed() && element.isEnabled()) ? element : null;
        });
    }

    /**
     * 获取所有窗口句柄
     * 用于多窗口/多标签页管理
     *
     * @return 所有窗口句柄的集合
     * @throws BusinessException 如果浏览器未初始化
     */
    public Set<String> getWindowHandles() {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }
        return driver.getWindowHandles();
    }

    /**
     * 切换到指定窗口
     *
     * @param windowHandle 目标窗口的句柄
     * @throws BusinessException 如果浏览器未初始化
     */
    public void switchToWindow(String windowHandle) {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }
        driver.switchTo().window(windowHandle);
    }

    /**
     * 获取当前窗口句柄
     *
     * @return 当前窗口句柄
     * @throws BusinessException 如果浏览器未初始化
     */
    public String getCurrentWindowHandle() {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }
        return driver.getWindowHandle();
    }

    /**
     * 关闭当前窗口
     *
     * @throws BusinessException 如果浏览器未初始化
     */
    public void closeCurrentWindow() {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }
        driver.close();
    }

    /**
     * 截取屏幕截图
     *
     * @return 截图数据的字节数组
     * @throws BusinessException 如果浏览器未初始化或截图失败
     */
    public byte[] takeScreenshot() {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }

        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            return ts.getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error("Failed to take screenshot", e);
            throw new BusinessException("Failed to take screenshot", e);
        }
    }

    // ============ Cookie管理 ============

    /**
     * 设置Cookies
     * 先清除所有现有cookies，然后添加新的cookies
     *
     * @param cookies 要设置的Cookie集合
     * @throws BusinessException 如果浏览器未初始化
     */
    public void setCookies(Set<Cookie> cookies) {
        if (cookies == null || cookies.isEmpty()) {
            log.info("没有cookies需要设置");
            return;
        }

        // 过滤无效的cookies
        Set<Cookie> validCookies = cookies.stream()
                .filter(this::isValidCookie)
                .collect(Collectors.toSet());

        if (validCookies.size() != cookies.size()) {
            log.warn("过滤了 {} 个无效的cookie", cookies.size() - validCookies.size());
        }

        setAllCookies(validCookies);
    }
    /**
     * 验证Cookie对象是否有效
     */
    public boolean isValidCookie(Cookie cookie) {
        if (cookie == null) {
            return false;
        }

        // 检查必需字段
        if (cookie.getName() == null || cookie.getName().isEmpty()) {
            return false;
        }

        // value可以为空，但通常应该有值
        if (cookie.getValue() == null) {
            log.debug("Cookie [{}] 的value为空", cookie.getName());
        }

        return true;
    }
    private void setAllCookies(Set<Cookie> cookies) {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }

        // 同步操作，防止并发问题
        synchronized (driver) {
            driver.manage().deleteAllCookies();
            for (Cookie cookie : cookies) {
                driver.manage().addCookie(cookie);
            }
        }
    }

    /**
     * 获取所有Cookies
     *
     * @return Cookie集合
     * @throws BusinessException 如果浏览器未初始化
     */
    public Set<Cookie> getCookies() {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }
        return driver.manage().getCookies();
    }

    /**
     * 添加单个Cookie
     *
     * @param cookie 要添加的Cookie
     * @throws BusinessException 如果浏览器未初始化
     */
    public void addCookie(Cookie cookie) {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }
        driver.manage().addCookie(cookie);
    }

    /**
     * 删除指定名称的Cookie
     *
     * @param name Cookie名称
     * @throws BusinessException 如果浏览器未初始化
     */
    public void deleteCookie(String name) {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }
        driver.manage().deleteCookieNamed(name);
    }

    /**
     * 删除所有Cookies
     *
     * @throws BusinessException 如果浏览器未初始化
     */
    public void deleteAllCookies() {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }
        driver.manage().deleteAllCookies();
    }

    // ============ 导航控制 ============

    /**
     * 刷新当前页面
     *
     * @throws BusinessException 如果浏览器未初始化
     */
    public void refresh() {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }
        driver.navigate().refresh();
    }

    /**
     * 后退到上一个页面
     *
     * @throws BusinessException 如果浏览器未初始化
     */
    public void back() {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }
        driver.navigate().back();
    }

    /**
     * 前进到下一个页面
     *
     * @throws BusinessException 如果浏览器未初始化
     */
    public void forward() {
        if (driver == null) {
            throw new BusinessException("Browser not initialized");
        }
        driver.navigate().forward();
    }

    /**
     * 关闭浏览器
     * 释放所有资源，包括driver和wait对象
     */
    public void quit() {
        if (driver != null) {
            try {
                driver.close();
            }catch (Exception e){}
            try {
                driver.quit();
            }catch (Exception e){}
            try {
                driver = null;
                wait = null;
            } catch (Exception e) {
                log.error("Error while closing browser", e);
            }
        }else {
            log.warn("Browser already closed");
        }
    }

    // ============ 链式配置方法 ============
    // 这些方法支持链式调用，方便配置浏览器参数

    /**
     * 设置超时时间（毫秒）
     *
     * @param timeoutMs 超时时间（毫秒）
     * @return Browser实例（支持链式调用）
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setTimeoutMs(long timeoutMs) {
        if (driver != null) {
            throw new BusinessException("Cannot set timeout after driver is initialized");
        }
        this.timeoutMs = timeoutMs;
        return this;
    }

    /**
     * 设置是否启用无头模式
     *
     * @param headless true启用无头模式，false显示浏览器界面
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setHeadless(boolean headless) {
        if (driver != null) {
            throw new BusinessException("Cannot set headless mode after driver is initialized");
        }
        this.headless = headless;
        return this;
    }

    /**
     * 设置是否加载图片
     *
     * @param loadImages true加载图片，false不加载图片
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setLoadImages(boolean loadImages) {
        if (driver != null) {
            throw new BusinessException("Cannot set loadImages after driver is initialized");
        }
        this.loadImages = loadImages;
        return this;
    }

    /**
     * 设置是否启用反检测模式
     *
     * @param stealthMode true启用反检测，false禁用
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setStealthMode(boolean stealthMode) {
        if (driver != null) {
            throw new BusinessException("Cannot set stealthMode after driver is initialized");
        }
        this.stealthMode = stealthMode;
        return this;
    }

    /**
     * 设置是否禁用自动化标志
     *
     * @param disableAutomationFlag true禁用自动化标志，false不禁用
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setDisableAutomationFlag(boolean disableAutomationFlag) {
        if (driver != null) {
            throw new BusinessException("Cannot set disableAutomationFlag after driver is initialized");
        }
        this.disableAutomationFlag = disableAutomationFlag;
        return this;
    }

    /**
     * 设置Chrome浏览器可执行文件路径
     *
     * @param binaryPath Chrome可执行文件路径
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setBinaryPath(String binaryPath) {
        if (driver != null) {
            throw new BusinessException("Cannot set binaryPath after driver is initialized");
        }
        this.binaryPath = binaryPath;
        return this;
    }

    /**
     * 设置用户数据目录路径
     *
     * @param userDataPath 用户数据目录路径
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setUserDataPath(String userDataPath) {
        if (driver != null) {
            throw new BusinessException("Cannot set userDataPath after driver is initialized");
        }
        this.userDataPath = userDataPath;
        return this;
    }

    /**
     * 设置ChromeDriver可执行文件路径
     *
     * @param driverPath ChromeDriver可执行文件路径
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setDriverPath(String driverPath) {
        if (driver != null) {
            throw new BusinessException("Cannot set driverPath after driver is initialized");
        }
        this.driverPath = driverPath;
        return this;
    }

    /**
     * 设置代理服务器
     *
     * @param proxyServer 代理服务器地址（格式：host:port）
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setProxyServer(String proxyServer) {
        if (driver != null) {
            throw new BusinessException("Cannot set proxy after driver is initialized");
        }
        this.proxyServer = proxyServer;
        this.enableProxy = ValueUtils.isNotBlank(proxyServer);
        return this;
    }

    /**
     * 设置代理类型
     *
     * @param proxyType 代理类型：http、socks4、socks5
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setProxyType(String proxyType) {
        if (driver != null) {
            throw new BusinessException("Cannot set proxyType after driver is initialized");
        }
        this.proxyType = proxyType;
        return this;
    }

    /**
     * 设置设备名称（用于移动端设备模拟）
     *
     * @param deviceName 设备名称（如"iPhone X"）
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setDeviceName(String deviceName) {
        if (driver != null) {
            throw new BusinessException("Cannot set deviceName after driver is initialized");
        }
        this.deviceName = deviceName;
        return this;
    }

    /**
     * 设置自定义设备模拟配置
     *
     * @param mobileEmulation 设备模拟配置映射
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setMobileEmulation(Map<String, Object> mobileEmulation) {
        if (driver != null) {
            throw new BusinessException("Cannot set mobileEmulation after driver is initialized");
        }
        this.mobileEmulation = mobileEmulation;
        return this;
    }

    /**
     * 设置浏览器语言
     *
     * @param language 语言代码（如"zh-CN"、"en-US"）
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setLanguage(String language) {
        if (driver != null) {
            throw new BusinessException("Cannot set language after driver is initialized");
        }
        this.language = language;
        return this;
    }

    /**
     * 设置用户代理
     *
     * @param userAgent 用户代理字符串
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser setUserAgent(String userAgent) {
        if (driver != null) {
            throw new BusinessException("Cannot set userAgent after driver is initialized");
        }
        this.userAgent = userAgent;
        return this;
    }

    /**
     * 设置浏览器窗口尺寸
     * 可以在driver初始化前或后设置
     *
     * @param dimension 窗口尺寸
     * @return Browser实例
     */
    public Browser setDimension(Dimension dimension) {
        this.dimension = dimension;
        if (driver != null) {
            driver.manage().window().setSize(dimension);
        }
        return this;
    }

    /**
     * 添加自定义浏览器能力
     *
     * @param key 能力键
     * @param value 能力值
     * @return Browser实例
     * @throws BusinessException 如果driver已初始化
     */
    public Browser addCapability(String key, String value) {
        if (driver != null) {
            throw new BusinessException("Cannot add capability after driver is initialized");
        }
        this.capabilities.put(key, value);
        return this;
    }

    // ============ 静态工厂方法 ============

    /**
     * 创建默认配置的Browser实例
     *
     * @return 默认配置的Browser实例
     */
    public static Browser createDefault() {
        return new Browser()
                .setHeadless(false)
                .setLoadImages(true)
                .setStealthMode(true)
                .setTimeoutMs(60000);
    }

    /**
     * 创建无头模式的Browser实例
     *
     * @return 配置为无头模式且不加载图片的Browser实例
     */
    public static Browser createHeadless() {
        return new Browser()
                .setHeadless(true)
                .setLoadImages(true)
                .setStealthMode(true)
                .setTimeoutMs(60000);
    }

    /**
     * 创建带代理的Browser实例
     *
     * @param proxyServer 代理服务器地址
     * @return 配置了代理和反检测模式的Browser实例
     */
    public static Browser createWithProxy(String proxyServer) {
        return new Browser()
                .setProxyServer(proxyServer)
                .setHeadless(false)
                .setLoadImages(true)
                .setStealthMode(true)
                .setTimeoutMs(60000);
    }
}