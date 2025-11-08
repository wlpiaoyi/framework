package org.wlpiaoyi.framework.lab.selenium.for12123.lease.test;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.wlpiaoyi.framework.lab.selenium.Browser;
import org.wlpiaoyi.framework.lab.selenium.for12123.lease.excel.ExcelWriter;
import org.wlpiaoyi.framework.lab.selenium.utils.WebElementUtils;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.data.WriterUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;
import org.wlpiaoyi.framework.utils.security.RsaCipher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class BrowserCabgov {

    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIwgd+H2N2wAAPEHEi8ypKdwaB2I\n" +
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
    private String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCMIHfh9jdsAADxBxIvMqSncGgdiKLh0BnyP6F6\n" +
            "QPISRQZ6u4fzhf8VZtkzfouWOBjgitk3X4LRmtdHndMcFG9d3ruqBbbqNVRwgIkswgmyFbrclcrG\n" +
            "OJex74QCbx9SpR0S0UFOLBTTYHa3WCcEleIXFFOtqYARiMgxWnU3rVZ1vwIDAQAB";

    private int type = 0;
    private final String CONFIG_PATH = System.getProperty("user.dir") + "/config/selenium";
    private final Browser browser;
    private String cookies = null;
    private Long curDateL = 0L;

    private void loadCurDateValue(){
        log.info("in. 读取到期配置文件");
        try {
            byte[] value = ReaderUtils.loadBytes(new File(CONFIG_PATH + "/cur_date.dat"));
            RsaCipher cipher = RsaCipher.build(0).setPrivateKey(this.privateKey).setPublicKey(this.publicKey).loadConfig();
            String dText = new String(
                    cipher.decrypt(
                            DataUtils.base64Decode(value)
                    ),
                    StandardCharsets.UTF_8
            );
            this.curDateL = Long.parseLong(dText);
        } catch (IOException e) {
            log.error("读取配置文件错误", e);
        }
        log.info("end. 读取到期配置文件");
    }

    public BrowserCabgov(int type){
        this.type = type;
        this.loadCurDateValue();
        log.warn("charles type:{}", type);
        browser = new Browser().setOptionHeadless(false).setUrl("https://sc.122.gov.cn/views/memrent/vehlist.html");
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
    @SneakyThrows
    public boolean start(){
        log.info("prepare charles data");
        this.cookies = ReaderUtils.loadString(CONFIG_PATH + "/cookies.txt", null).replaceAll("\n","").replaceAll("\r","");
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
                log.info("cookies is null");
                this.cookies = null;
            }
        }catch (Exception e) {
            log.error("set cookies error", e);
            browser.quit();
            return false;
        }
        this.openAndLogin();
        try{
            WebElement addBoxEle = this.openAddBox();
            SubmitHT submitHT = SubmitHT.builder()
                    .carNo("川ADG3769")
                    .htNo("123123")
                    .name("刘海燕")
                    .cardId("622322198408253422")
                    .htSignTime(LocalDateTime.now())
                    .leaseStartTime(LocalDateTime.now().plusDays(1))
                    .leaseEndTime(LocalDateTime.now().plusDays(2))
                    .build();
            this.submitHT(submitHT, addBoxEle);
        }finally {
            try{
                this.browser.quit();
            }catch (Exception e){};
        }
        return true;
    }

    void submitHT(SubmitHT submitHT, WebElement addBoxEle){
        WebElement cardNoEle = addBoxEle.findElement(By.id("hphm_lr"));
        WebElement htNoEle = addBoxEle.findElement(By.id("htbh_lr"));
        WebElement htSignTimeEle = addBoxEle.findElement(By.id("htqdsj_lr"));

        WebElementUtils.setValue(cardNoEle, submitHT.getCarNo());
        WebElementUtils.setValue(htNoEle, submitHT.getHtNo());
        List<WebElement> addOnEles = WebElementUtils.getChildrenByClass(WebElementUtils.getParentSafely(htSignTimeEle), "add-on");
        WebElementUtils.click(browser, addOnEles.get(0));
        WebElement yearMonthEle = browser.getDriver().findElements(By.className("datetimepicker-months")).get(2);
        List<WebElement> yearMonthTableEle = WebElementUtils.getChildrenByTag(yearMonthEle, "table");
        List<WebElement> yearMonthTableTHeadEle = WebElementUtils.getChildrenByTag(yearMonthTableEle.get(0), "thead");
        List<WebElement> yearMonthTableTBodyEle = WebElementUtils.getChildrenByTag(yearMonthTableEle.get(0), "tbody");
        List<WebElement> yearMonthTableTFootEle = WebElementUtils.getChildrenByTag(yearMonthTableEle.get(0), "tfoot");

        System.out.println();
    }

    WebElement openAddBox(){
        try{
            Thread.sleep(1000);
        }catch (Exception e){}

        String errorMsg = null;
        int i = 300;
        while (i-- > 0){
            try {
                Thread.sleep(1000);
                WebElement webElement = null;
                try{
                    webElement = browser.getDriver().findElement(By.id("mem-content"));
                }catch (Exception e){}
                if(webElement == null){
                    log.warn("========== not fund 租赁合同管理ctx ele:{}", "mem-content");
                    continue;
                }
                try{
                    webElement = webElement.findElement(By.id("jsrcx"));
                }catch (Exception e){}
                if(webElement == null){
                    log.warn("========== not fund 租赁合同管理search ele:{}", "jsrcx");
                    continue;
                }
                try{
                    webElement = webElement.findElement(By.id("htlrBtn"));
                }catch (Exception e){}
                if(webElement == null){
                    log.warn("========== not fund 租赁合同管理addBtn ele:{}", "htlrBtn");
                    continue;
                }
                if (ValueUtils.isBlank(this.cookies)){
                    int ti = 10;
                    while (ti -- > 0){
                        try{
                            Thread.sleep(1000);
                            if(clickFeed()){
                                break;
                            }else if(ti > 5){
                                break;
                            }
                        }catch (Exception e){}
                    }
                }

                WebElementUtils.click(browser, webElement);
                log.info("==========< click 打开新增弹框:{}", "业务办理");
                Thread.sleep(1000);
                WebElement boxEle = browser.getDriver().findElement(By.id("htlrModal"));
                String ariaHidden = boxEle.getAttribute("aria-hidden");
                if(!"false".equals(ariaHidden)){
                    log.error("==========< click 新增弹框为打开:{}", "业务办理");
                    throw new BusinessException("新增弹框没有打开");
                }
                return boxEle;
            } catch (Exception e) {
                e.printStackTrace();
                errorMsg = e.getMessage();
            }
        }
        if(i <= 0){
            throw new BusinessException("没有找到事故处理业务左边目录");
        }
        if(ValueUtils.isNotBlank(errorMsg)){
            throw new BusinessException(errorMsg);
        }
        return null;
    }




    void openAndLogin(){

        String errorMsg = null;
        int i = 300;
        while (i-- > 0){
            try {
                Thread.sleep(1000);
                WebElement webElement = null;
                try{
                    webElement = browser.getDriver().findElement(By.id("hello"));
                }catch (Exception e){}
                if(webElement == null){
                    log.warn("========== not fund 请选择服务类型base ele:{}", "hello");
                    continue;
                }
                try{
                    webElement = webElement.findElement(By.className("pull-right"));
                }catch (Exception e){}
                if(webElement == null){
                    log.warn("========== not fund 请选择服务类型 ele:{}", "pull-right");
                    continue;
                }
                try{
                    webElement = webElement.findElement(By.xpath("select"));
                }catch (Exception e){}
                if(webElement == null){
                    log.warn("========== not fund 请选择服务类型 ele:{}", "select");
                    continue;
                }

                if (ValueUtils.isBlank(this.cookies)){
                    int ti = 10;
                    while (ti -- > 0){
                        try{
                            Thread.sleep(1000);
                            if(clickFeed()){
                                break;
                            }else if(ti > 5){
                                break;
                            }
                        }catch (Exception e){}
                    }
                }
                new Select(webElement).selectByIndex(1);
                log.info("==========< click 请选择服务类型:{}", "业务办理");
                Thread.sleep(1000);
                WebElementUtils.click(browser, browser.getDriver().findElement(By.id("sidebar_menu_93")));
                log.info("==========< click 租赁合同");
                Thread.sleep(1000);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                errorMsg = e.getMessage();
            }
        }
        if(i <= 0){
            throw new BusinessException("没有找到事故处理业务左边目录");
        }
        if(ValueUtils.isNotBlank(errorMsg)){
            throw new BusinessException(errorMsg);
        }
    }


    boolean clickFeed(){
        try{
            WebElement webElement = browser.getDriver().findElement(By.className("aui_state_highlight"));
            if(webElement != null){
                WebElementUtils.click(browser, webElement);
                return true;
            }
        }catch (Exception e){
            log.warn("click feed error:{}", e.getMessage());
        }
        return false;

    }
//
//    public Map<String, String> querySurvielDetail(String hphm, String xh, String cjjg, String cookies) throws IOException, InterruptedException {
//        Thread.sleep(5000);
//        String url = "https://sc.122.gov.cn/user/m/tsc/vio/querySurvielDetail";
//        Response<Map> response = HttpClient.instance(
//                        Request.initJson(url)
//                                .setHeader("Host","sc.122.gov.cn")
//                                .setHeader("Origin","https://sc.122.gov.cn")
//                                .setHeader("Referer","https://sc.122.gov.cn/views/memfyy/violation.html")
//                                .setHeader("Sec-Ch-Ua","\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"")
//                                .setHeader("Sec-Ch-Ua-Mobile","?0")
//                                .setHeader("Sec-Ch-Ua-Platform:","\"Windows\"")
//                                .setHeader("Sec-Fetch-Dest","empty")
//                                .setHeader("Sec-Fetch-Mode","cors")
//                                .setHeader("Sec-Fetch-Site","same-origin")
//                                .setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
//                                .setHeader("X-Requested-With", "XMLHttpRequest")
//                                .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                                .setHeader("Cookie", cookies)
//                                .setMethod(Request.Method.Post)
//                                .setParam("hpzl","52")
//                                .setParam("hphm","川" + hphm)
//                                .setParam("xh", xh)
//                                .setParam("cjjg",cjjg)
//                )
//                .setRpClazz(Map.class)
//                .response();
//        System.out.println(url + "?" + hphm + "," + xh);
//        Gson gson = GsonBuilder.gsonDefault();
//        if(response.getStatusCode() != 200){
//            throw new BusinessException("列表请求错误：" + gson.toJson(response.getBody()));
//        }
//        Map<String, String> dict = new HashMap(){{
//            put("hpzlStr","号牌种类");
//            put("hphm", "号牌号码");
//            put("wfsj", "违法时间");
//            put("wfdz", "违法地点");
//            put("wfms", "违法行为");
//            put("cjjgmc", "采集单位");
//            put("fkje", "罚款金额");
//            put("wfjfs", "记分值");
//        }};
//        Map<String, String> item = new HashMap<>();;
//        for (String key : dict.keySet()){
//            item.put(dict.get(key), MapUtils.getValueByKeyPath(response.getBody(), "data." + key, "", String.class));
//        }
//        return item;
//    }
//
//
//    public int suriquery(String hphm, int page, String cookies, List<Map<String, String>> itemsList) throws IOException, InterruptedException {
//        String url = "https://sc.122.gov.cn/user/m/uservio/suriquery";
//        Response<Map> response = HttpClient.instance(
//                        Request.initJson(url)
//                                .setHeader("Host","sc.122.gov.cn")
//                                .setHeader("Origin","https://sc.122.gov.cn")
//                                .setHeader("Referer","https://sc.122.gov.cn/views/memfyy/violation.html")
//                                .setHeader("Sec-Ch-Ua","\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"")
//                                .setHeader("Sec-Ch-Ua-Mobile","?0")
//                                .setHeader("Sec-Ch-Ua-Platform:","\"Windows\"")
//                                .setHeader("Sec-Fetch-Dest","empty")
//                                .setHeader("Sec-Fetch-Mode","cors")
//                                .setHeader("Sec-Fetch-Site","same-origin")
//                                .setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
//                                .setHeader("X-Requested-With", "XMLHttpRequest")
//                                .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                                .setHeader("Cookie", cookies)
//                                .setMethod(Request.Method.Post)
//                                .setParam("startDate","20200105")
//                                .setParam("endDate","20240709")
//                                .setParam("hpzl","52")
//                                .setParam("hphm","川" + hphm)
//                                .setParam("page",page + "")
//                                .setParam("type","0")
//                )
//                .setRpClazz(Map.class)
//                .response();
//        System.out.println(url + "?"+ hphm + "," + page);
//        Gson gson = GsonBuilder.gsonDefault();
//        if(response.getStatusCode() != 200){
//            throw new BusinessException("列表请求错误：" + gson.toJson(response.getBody()));
//        }
//        Integer totalPages = MapUtils.getValueByKeyPath(response.getBody(),"data.totalPages", -1, Integer.class);
//        List<Map> content = MapUtils.getValueByKeyPath(response.getBody(),"data.content", null, List.class);
//        if(ValueUtils.isBlank(content)){
//            System.out.printf("没有数据了：" + hphm);
//            return 0;
//        }
//        for (Map item : content){
//            Integer isHandle = MapUtils.getInteger(item, "clbj", 0);
//            Integer isPay = MapUtils.getInteger(item, "jkbj", 0);
//            if(isHandle == 0 || isPay == 0){
//                this.querySurvielDetail(hphm, MapUtils.getString(item, "xh"), MapUtils.getString(item, "cjjg"), isHandle, isPay, cookies, itemsList);
//            }
//        }
//        return totalPages - page;
//    }
//    public void querySurvielDetail(String hphm, String xh, String cjjg, Integer isHandle, Integer isPay, String cookies, List<Map<String, String>> itemsList) throws IOException, InterruptedException {
//        Thread.sleep(5000);
//        String url = "https://sc.122.gov.cn/user/m/tsc/vio/querySurvielDetail";
//        Response<Map> response = HttpClient.instance(
//                        Request.initJson(url)
//                                .setHeader("Host","sc.122.gov.cn")
//                                .setHeader("Origin","https://sc.122.gov.cn")
//                                .setHeader("Referer","https://sc.122.gov.cn/views/memfyy/violation.html")
//                                .setHeader("Sec-Ch-Ua","\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"")
//                                .setHeader("Sec-Ch-Ua-Mobile","?0")
//                                .setHeader("Sec-Ch-Ua-Platform:","\"Windows\"")
//                                .setHeader("Sec-Fetch-Dest","empty")
//                                .setHeader("Sec-Fetch-Mode","cors")
//                                .setHeader("Sec-Fetch-Site","same-origin")
//                                .setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
//                                .setHeader("X-Requested-With", "XMLHttpRequest")
//                                .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                                .setHeader("Cookie", cookies)
//                                .setMethod(Request.Method.Post)
//                                .setParam("hpzl","52")
//                                .setParam("hphm","川" + hphm)
//                                .setParam("xh", xh)
//                                .setParam("cjjg",cjjg)
//                )
//                .setRpClazz(Map.class)
//                .response();
//        System.out.println(url + "?" + hphm + "," + xh);
//        Gson gson = GsonBuilder.gsonDefault();
//        if(response.getStatusCode() != 200){
//            throw new BusinessException("列表请求错误：" + gson.toJson(response.getBody()));
//        }
//        Map<String, String> dict = new HashMap(){{
//            put("hpzlStr","号牌种类");
//            put("hphm", "号牌号码");
//            put("wfsj", "违法时间");
//            put("wfdz", "违法地点");
//            put("wfms", "违法行为");
//            put("cjjgmc", "采集单位");
//            put("fkje", "罚款金额");
//            put("wfjfs", "记分值");
//        }};
//        Map<String, String> item = new HashMap<>();
//        item.put("状态", (isHandle == 1 ? "已处理" : "未处理") + "|" + (isPay == 1 ? "已交款" : "未交款"));
//        for (String key : dict.keySet()){
//            item.put(dict.get(key), MapUtils.getValueByKeyPath(response.getBody(), "data." + key, "", String.class));
//        }
//        itemsList.add(item);
//    }

    public static void main(String[] args) {
        BrowserCabgov bc = new BrowserCabgov(args.length) ;
        bc.start();
    }
}
