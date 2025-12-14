//package org.wlpiaoyi.framework.lab.selenium.for12123.violate.test;
//
//import com.google.gson.Gson;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.openqa.selenium.Cookie;
//import org.wlpiaoyi.framework.lab.selenium.Browser;
//import org.wlpiaoyi.framework.utils.ValueUtils;
//import org.wlpiaoyi.framework.utils.data.DataUtils;
//import org.wlpiaoyi.framework.utils.data.ReaderUtils;
//import org.wlpiaoyi.framework.utils.gson.GsonBuilder;
//
//import java.io.IOException;
//import java.util.*;
//
//@Slf4j
//public class DyTest {
//
//    protected final String CONFIG_PATH = System.getProperty("user.dir") + "/config/selenium";
//    protected Browser browser;
//    protected String browserUlr = "file:///D:/Object/.Java/framework/lab/selenium/src/main/resources/%E7%88%AC%E8%99%AB%E6%A3%80%E6%B5%8B.html";
////    protected String cookies = "abRequestId=3c30a978-d8f2-57c5-9ca8-00e959afadd9; webBuild=5.0.3; xsecappid=xhs-pc-web; loadts=1765007865357; a1=19af2aab60dzgvmy2adll8wr193m366fslgjmvu3p50000428142; webId=3828ae325d570a74329b8a009f6c7cad; gid=yj0iJ00S0fMSyj0iJ00DK1h78fukh6vJ0f11kqUYEFyjqV286qKKi68884JYy4J8Jj2KWSJJ; web_session=040069b652f27723c6c0a9a0063b4bdc042607; unread={%22ub%22:%226921be8d000000001e007dac%22%2C%22ue%22:%2269339a8a000000001f0066de%22%2C%22uc%22:21}; acw_tc=0ad62de117650096744206131e28352b5486a502a51c6916ef38d77443f70f; websectiga=cf46039d1971c7b9a650d87269f31ac8fe3bf71d61ebf9d9a0a87efb414b816c; sec_poison_id=c06bf1f0-cedf-49f7-adff-1389d3d36904";
//
//    protected void init(){
//        log.info("BrowserBase.create in. 创建浏览器实例");
////        log.warn("BrowserBase.create cookies:{}", cookies);
//        browser = new Browser().setOptionHeadless(false).setUrl(this.browserUlr);
//        log.info("BrowserBase.create  创建浏览器实例:{}", this.browser.getUrl());
//        this.browser.setOptionLoadimg(true);
//        this.browser.setDriverPath(CONFIG_PATH +"/chromedriver");
////        Runtime.getRuntime().addShutdownHook(new RTMServer(this.browser));
//        // 添加一个shutdown hook
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            log.info("检测到程序即将关闭...");
//            log.warn("browser start quit");
//            try {
//                this.browser.getDriver().close();
//            }catch (Exception e){}
//            try {
//                this.browser.getDriver().quit();
//            }catch (Exception e){}
//            try {
//                this.browser.quit();
//            }catch (Exception e){}
//            log.warn("browser quit success");
//        }));
//    }
//
//    public boolean start(){
//        log.info("BrowserBase.start in. 启动浏览器");
//        try{
//            browser.openChromeDriver();
//            browser.openDriver();
////            if(ValueUtils.isNotBlank(this.cookies)){
////                String args[] = this.cookies.split("; ");
////                Set<Cookie> cookies = new HashSet<>();
////                for (String arg : args){
////                    String as[] = arg.split("=");
////                    cookies.add(new Cookie(as[0], as[1]));
////                }
////                this.browser.setCookies(cookies);;
////                browser.openDriver();
////            }else {
////                log.info("BrowserBase.start cookies is null");
////            }
//        }catch (Exception e) {
//            log.error("BrowserBase.start set cookies error", e);
//            browser.quit();
//            return false;
//        }finally {
//            log.info("BrowserBase.start end");
//        }
//        return true;
//    }
//
//
//    public static void main(String[] args) throws IOException {
//
//        Gson gson = GsonBuilder.gsonDefault();
//        List list = gson.fromJson(ReaderUtils.loadString(DataUtils.USER_DIR + "/config/selenium/conversations.json", null), List.class);
//        new DyTest().test();
//    }
//    @SneakyThrows
//    public void test(){
//        this.init();
//        Thread.sleep(1000 + new Random().nextInt() % 1000);
//        this.start();
//        System.out.println("test");
//    }
//
//
//}
