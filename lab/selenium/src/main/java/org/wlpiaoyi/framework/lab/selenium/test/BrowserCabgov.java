package org.wlpiaoyi.framework.lab.selenium.test;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.wlpiaoyi.framework.lab.selenium.Browser;
import org.wlpiaoyi.framework.lab.selenium.excel.ExcelWriter;
import org.wlpiaoyi.framework.lab.selenium.utils.WebElementUtils;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.data.WriterUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;
import org.wlpiaoyi.framework.utils.http.HttpClient;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

@Slf4j
public class BrowserCabgov {

    private final String CONFIG_PATH = System.getProperty("user.dir") + "/config/selenium";
    private Browser browser;
    private String cookies = null;

    public BrowserCabgov(){
        browser = new Browser().setOptionHeadless(false).setUrl("https://sc.122.gov.cn/views/memfyy/violation.html");
//        this.browser.setOptionHeadless(true);
        this.browser.setOptionLoadimg(true);
        this.browser.setDriverPath(CONFIG_PATH +"/chromedriver.126.0.v");
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

        String[] args = ReaderUtils.loadString(CONFIG_PATH + "/car_no.txt", null).split("\n");
        log.info("start charles data");
        List<Map<String, String>> itemsList = new ArrayList<>();
        try{
            for(String arg : args){
                arg = arg.replaceAll("\r", "");
                arg = arg.replaceAll("\n", "");
                log.info(">charles data by car_no:{} ==================>", arg);
                try{
                    this.exce(arg, itemsList);
                    log.info("<charles data by car_no:{} <==================", arg);
                }catch (Exception e){
                    log.error("<charles data error by car_no:{} <==================", arg, e);
                }
                writeExcel(itemsList);
            }
        }finally {
            try{
                this.browser.quit();
            }catch (Exception e){};
            try{
                writeExcel(itemsList);
            }catch (Exception e){}
        }
        return true;
    }


    @SneakyThrows
    public void writeExcel(List<Map<String, String>> itemsList){
        Gson gson = GsonBuilder.gsonDefault();
        String fileName = DateUtils.formatDate(new Date(), "YYMMDDHHmmss");
        WriterUtils.overwrite(new File(CONFIG_PATH + "/" + fileName  + ".txt"), gson.toJson(itemsList).getBytes());
        OutputStream os = new FileOutputStream(CONFIG_PATH + "/" + fileName + ".xlsx");
        ExcelWriter.exportData(itemsList).write(os);
        os.flush();
        os.close();
    }

    void exce(String value, List<Map<String, String>> itemsList){
        this.openAndLogin();
        this.search(value);
        String errorMsg = null;
        int i = 30;
        i = 30;
        while (i -- > 0){
            try {
                Thread.sleep(1000);
                List<WebElement> webElements = null;
                WebElement webElement = null;
                try{
                    webElement = browser.getDriver().findElement(By.id("violationveh"));
                    Thread.sleep(1000);
                }catch (Exception e){
                    continue;
                }
                if(webElement == null){ continue; }


                try{
                    webElements = webElement.findElements(By.xpath("table/tbody/tr"));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                if(ValueUtils.isBlank(webElements)){
                    continue;
                }
                for(WebElement trEle : webElements){
                    List<WebElement> datas = trEle.findElements(By.xpath("td"));
                    if(!"未交款".equals(datas.get(6).getText())){
                        continue;
                    }
                    try{
                        Thread.sleep(500);
                        WebElementUtils.click(browser, datas.get(7).findElement(By.xpath("a")));
                    }catch (Exception e){
                        throw e;
                    }
                    Thread.sleep(2000);
                    i = 30;
                    Map<String, String> item = this.view();
                    item.put("状态", datas.get(4).getText() + "|" +datas.get(6).getText());
                    itemsList.add(item);
                }
                try{
                    webElement = browser.getDriver().findElement(By.id("mypagination1"));
                    Thread.sleep(1000);
                }catch (Exception e){ break;}
                if(webElement == null){ break; }

                try{
                    webElements = webElement.findElements(By.xpath("ul/li"));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}
                if(ValueUtils.isBlank(webElements) || webElements.size() <= 5){
                    break;
                }
                webElements.remove(0);
                webElements.remove(0);
                webElements.remove(webElements.size() - 1);
                webElements.remove(webElements.size() - 1);

                if(webElements.size() <= 1){
                    break;
                }
                List<WebElement> removes = new ArrayList<>();
                for(WebElement ele : webElements){
                    removes.add(ele);
                    if("active".equals(ele.getAttribute("class"))){
                        break;
                    }
                }
                webElements.removeAll(removes);
                if(webElements.size() == 0){
                    break;
                }
                WebElementUtils.click(browser, webElements.get(0).findElement(By.xpath("a")));
                Thread.sleep(2000);
                i = 30;
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if(i <= 0){
            throw new BusinessException("没有找到事故处理业务右上Tab");
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

    void openAndLogin(){

        String errorMsg = null;
        int i = 300;
        while (i-- > 0){
            try {
                Thread.sleep(1000);
                List<WebElement> webElements = null;
//                try{
//                    webElements = browser.getDriver().findElements(By.xpath("/html/body/div"));
//                }catch (Exception e){}
//                if(ValueUtils.isBlank(webElements)){
//                    continue;
//                }
//                if(ValueUtils.isBlank(webElements) || webElements.size() < 2){
//                    continue;
//                }
                WebElement webElement = null;
                try{
                    webElement = browser.getDriver().findElement(By.className("pull-right"));
                }catch (Exception e){}
                if(webElement == null){
                    continue;
                }
                try{
                    webElement = webElement.findElement(By.xpath("select"));
                }catch (Exception e){}
                if(webElement == null){
                    continue;
                }
                try{
                    webElements = webElement.findElements(By.xpath("option"));
                }catch (Exception e){}
                if(webElement == null){
                    continue;
                }
                if(ValueUtils.isBlank(webElements) || webElements.size() < 1){
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

                WebElementUtils.click(browser, webElements.get(0));
                Thread.sleep(1000);
                WebElementUtils.click(browser, browser.getDriver().findElement(By.id("sidebar_menu_5")));
                Thread.sleep(1000);
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if(i <= 0){
            throw new BusinessException("没有找到事故处理业务左边目录");
        }
        if(ValueUtils.isNotBlank(errorMsg)){
            throw new BusinessException(errorMsg);
        }
    }

    void search(String text){

        String errorMsg = null;
        int i = 30;
        while (i -- > 0){
            try {
                Thread.sleep(1000);
                List<WebElement> webElements = null;
                WebElement webElement = null;
                try{
                    webElement = browser.getDriver().findElement(By.id("mem-content")).findElement(By.id("vehSearchForm"));
                }catch (Exception e){}
                if(webElement == null){
                    continue;
                }
                try{
                    webElements = webElement.findElements(By.xpath("div"));
                }catch (Exception e){}
                if(ValueUtils.isBlank(webElements) || webElements.size() < 4){
                    continue;
                }
                Thread.sleep(1000);

                try{
                    WebElementUtils.click(browser, webElements.get(0).findElements(By.className("add-on")).get(0));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.click(browser, browser.getDriver().findElements(By.className("datetimepicker-months")).get(0).findElements(By.xpath("table/thead/tr/th")).get(1));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.click(browser, browser.getDriver().findElements(By.className("datetimepicker-years")).get(0).findElements(By.xpath("table/tbody/tr/td/span")).get(1));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.click(browser,browser.getDriver().findElements(By.className("datetimepicker-months")).get(0).findElements(By.xpath("table/tbody/tr/td/span")).get(0));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.click(browser, browser.getDriver().findElements(By.className("datetimepicker-days")).get(0).findElements(By.xpath("table/tbody/tr/td")).get(6));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.click(browser, webElements.get(1).findElement(By.id("hpzl")).findElements(By.xpath("option")).get(3));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.setValue(webElements.get(2).findElement(By.id("hphm")), text);
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}

                try{
                    WebElementUtils.click(browser, webElements.get(3).findElement(By.xpath("button")));
                    Thread.sleep(1000);
                }catch (Exception e){ continue;}
                errorMsg = null;
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if(i <= 0){
            throw new BusinessException("没有找到事故处理业务右上Tab");
        }
        if(ValueUtils.isNotBlank(errorMsg)){
            throw new BusinessException(errorMsg);
        }
    }

    @SneakyThrows
    Map<String, String> view(){
        try{

            Map<String, String> itemMap = new HashMap<>();
            String errorMsg = null;
            int i = 30;
            while (i -- > 0){
                try {
                    Thread.sleep(1000);
                    List<WebElement> webElements = null;
                    WebElement webElement = null;
                    try{
                        webElement = browser.getDriver().findElement(By.id("view"));
                    }catch (Exception e){}
                    if(webElement == null){
                        continue;
                    }

                    try{
                        webElement = webElement.findElement(By.className("modal-body"));
                    }catch (Exception e){}
                    if(webElement == null){
                        continue;
                    }

                    try{
                        webElement = webElement.findElement(By.className("xqInfo"));
                    }catch (Exception e){}
                    if(webElement == null){
                        continue;
                    }
                    try{
                        webElements = webElement.findElements(By.xpath("form/div"));
                    }catch (Exception e){}
                    if(ValueUtils.isBlank(webElements) || webElements.size() < 1){
                        continue;
                    }
                    Thread.sleep(1000);
                    while (true){
                        Map<String, String> data = new HashMap<>();
                        boolean isGoon = false;
                        for (WebElement ele : webElements){
                            if(ele.findElements(By.xpath("span")).size() < 2){
                                continue;
                            }
                            List<WebElement> spans = ele.findElements(By.xpath("span"));
                            String name = spans.get(0).getText();
                            String value = spans.get(1).getText();
                            if(ValueUtils.isBlank(name) || ValueUtils.isBlank(value)){
                                log.info("charles data field null, waiting ==================");
                                isGoon = true;
                                break;
                            }
                            data.put(name, value);
                        }
                        if(isGoon){
                            Thread.sleep(1000);
                            webElements = webElement.findElements(By.xpath("form/div"));
                            continue;
                        }
                        log.info("charles data success:{}", GsonBuilder.gsonDefault().toJson(data));
                        itemMap.putAll(data);
                        break;
                    }
                    errorMsg = null;
                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            if(i <= 0){
                throw new BusinessException("没有找到事故处理业务右上Tab");
            }
            if(ValueUtils.isNotBlank(errorMsg)){
                throw new BusinessException(errorMsg);
            }
            return itemMap;
        }finally {
            WebElement webElement = browser.getDriver().findElement(By.id("view"));
            WebElementUtils.click(browser, webElement.findElement(By.id("bind_close")));
            Thread.sleep(1000);
        }
    }

    public Map<String, String> querySurvielDetail(String hphm, String xh, String cjjg, String cookies) throws IOException, InterruptedException {
        Thread.sleep(5000);
        String url = "https://sc.122.gov.cn/user/m/tsc/vio/querySurvielDetail";
        Response<Map> response = HttpClient.instance(
                        Request.initJson(url)
                                .setHeader("Host","sc.122.gov.cn")
                                .setHeader("Origin","https://sc.122.gov.cn")
                                .setHeader("Referer","https://sc.122.gov.cn/views/memfyy/violation.html")
                                .setHeader("Sec-Ch-Ua","\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"")
                                .setHeader("Sec-Ch-Ua-Mobile","?0")
                                .setHeader("Sec-Ch-Ua-Platform:","\"Windows\"")
                                .setHeader("Sec-Fetch-Dest","empty")
                                .setHeader("Sec-Fetch-Mode","cors")
                                .setHeader("Sec-Fetch-Site","same-origin")
                                .setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                                .setHeader("X-Requested-With", "XMLHttpRequest")
                                .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .setHeader("Cookie", cookies)
                                .setMethod(Request.Method.Post)
                                .setParam("hpzl","52")
                                .setParam("hphm","川" + hphm)
                                .setParam("xh", xh)
                                .setParam("cjjg",cjjg)
                )
                .setRpClazz(Map.class)
                .response();
        System.out.println(url + "?" + hphm + "," + xh);
        Gson gson = GsonBuilder.gsonDefault();
        if(response.getStatusCode() != 200){
            throw new BusinessException("列表请求错误：" + gson.toJson(response.getBody()));
        }
        Map<String, String> dict = new HashMap(){{
            put("hpzlStr","号牌种类");
            put("hphm", "号牌号码");
            put("wfsj", "违法时间");
            put("wfdz", "违法地点");
            put("wfms", "违法行为");
            put("cjjgmc", "采集单位");
            put("fkje", "罚款金额");
            put("wfjfs", "记分值");
        }};
        Map<String, String> item = new HashMap<>();;
        for (String key : dict.keySet()){
            item.put(dict.get(key), MapUtils.getValueByKeyPath(response.getBody(), "data." + key, "", String.class));
        }
        return item;
    }


    public int suriquery(String hphm, int page, String cookies, List<Map<String, String>> itemsList) throws IOException, InterruptedException {
        String url = "https://sc.122.gov.cn/user/m/uservio/suriquery";
        Response<Map> response = HttpClient.instance(
                        Request.initJson(url)
                                .setHeader("Host","sc.122.gov.cn")
                                .setHeader("Origin","https://sc.122.gov.cn")
                                .setHeader("Referer","https://sc.122.gov.cn/views/memfyy/violation.html")
                                .setHeader("Sec-Ch-Ua","\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"")
                                .setHeader("Sec-Ch-Ua-Mobile","?0")
                                .setHeader("Sec-Ch-Ua-Platform:","\"Windows\"")
                                .setHeader("Sec-Fetch-Dest","empty")
                                .setHeader("Sec-Fetch-Mode","cors")
                                .setHeader("Sec-Fetch-Site","same-origin")
                                .setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                                .setHeader("X-Requested-With", "XMLHttpRequest")
                                .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .setHeader("Cookie", cookies)
                                .setMethod(Request.Method.Post)
                                .setParam("startDate","20200105")
                                .setParam("endDate","20240709")
                                .setParam("hpzl","52")
                                .setParam("hphm","川" + hphm)
                                .setParam("page",page + "")
                                .setParam("type","0")
                )
                .setRpClazz(Map.class)
                .response();
        System.out.println(url + "?"+ hphm + "," + page);
        Gson gson = GsonBuilder.gsonDefault();
        if(response.getStatusCode() != 200){
            throw new BusinessException("列表请求错误：" + gson.toJson(response.getBody()));
        }
        Integer totalPages = MapUtils.getValueByKeyPath(response.getBody(),"data.totalPages", -1, Integer.class);
        List<Map> content = MapUtils.getValueByKeyPath(response.getBody(),"data.content", null, List.class);
        if(ValueUtils.isBlank(content)){
            System.out.printf("没有数据了：" + hphm);
            return 0;
        }
        for (Map item : content){
            Integer isHandle = MapUtils.getInteger(item, "clbj", 0);
            Integer isPay = MapUtils.getInteger(item, "jkbj", 0);
            if(isHandle == 0 || isPay == 0){
                this.querySurvielDetail(hphm, MapUtils.getString(item, "xh"), MapUtils.getString(item, "cjjg"), isHandle, isPay, cookies, itemsList);
            }
        }
        return totalPages - page;
    }
    public void querySurvielDetail(String hphm, String xh, String cjjg, Integer isHandle, Integer isPay, String cookies, List<Map<String, String>> itemsList) throws IOException, InterruptedException {
        Thread.sleep(5000);
        String url = "https://sc.122.gov.cn/user/m/tsc/vio/querySurvielDetail";
        Response<Map> response = HttpClient.instance(
                        Request.initJson(url)
                                .setHeader("Host","sc.122.gov.cn")
                                .setHeader("Origin","https://sc.122.gov.cn")
                                .setHeader("Referer","https://sc.122.gov.cn/views/memfyy/violation.html")
                                .setHeader("Sec-Ch-Ua","\"Not/A)Brand\";v=\"8\", \"Chromium\";v=\"126\", \"Google Chrome\";v=\"126\"")
                                .setHeader("Sec-Ch-Ua-Mobile","?0")
                                .setHeader("Sec-Ch-Ua-Platform:","\"Windows\"")
                                .setHeader("Sec-Fetch-Dest","empty")
                                .setHeader("Sec-Fetch-Mode","cors")
                                .setHeader("Sec-Fetch-Site","same-origin")
                                .setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                                .setHeader("X-Requested-With", "XMLHttpRequest")
                                .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .setHeader("Cookie", cookies)
                                .setMethod(Request.Method.Post)
                                .setParam("hpzl","52")
                                .setParam("hphm","川" + hphm)
                                .setParam("xh", xh)
                                .setParam("cjjg",cjjg)
                )
                .setRpClazz(Map.class)
                .response();
        System.out.println(url + "?" + hphm + "," + xh);
        Gson gson = GsonBuilder.gsonDefault();
        if(response.getStatusCode() != 200){
            throw new BusinessException("列表请求错误：" + gson.toJson(response.getBody()));
        }
        Map<String, String> dict = new HashMap(){{
            put("hpzlStr","号牌种类");
            put("hphm", "号牌号码");
            put("wfsj", "违法时间");
            put("wfdz", "违法地点");
            put("wfms", "违法行为");
            put("cjjgmc", "采集单位");
            put("fkje", "罚款金额");
            put("wfjfs", "记分值");
        }};
        Map<String, String> item = new HashMap<>();
        item.put("状态", (isHandle == 1 ? "已处理" : "未处理") + "|" + (isPay == 1 ? "已交款" : "未交款"));
        for (String key : dict.keySet()){
            item.put(dict.get(key), MapUtils.getValueByKeyPath(response.getBody(), "data." + key, "", String.class));
        }
        itemsList.add(item);
    }

    public static void main(String[] args) {
        BrowserCabgov bc = new BrowserCabgov();
        bc.start();
    }
}
