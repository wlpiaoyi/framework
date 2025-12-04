package org.wlpiaoyi.framework.lab.selenium.for12123.violate.test;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.wlpiaoyi.framework.lab.selenium.for12123.BrowserBase;
import org.wlpiaoyi.framework.lab.selenium.for12123.violate.excel.ExcelWriter;
import org.wlpiaoyi.framework.lab.selenium.utils.WebElementUtils;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.data.WriterUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class BrowserCabgov extends BrowserBase {

    public static final List<String> itemTypes = new ArrayList(){{
        add("未处理");
        add("未交款");
    }};

    public BrowserCabgov(int type){
        super(type);
    }


    public void runBiz(String[] args, List<Map<String, String>> itemsList, List<String> errorCarNos, List<String> noItemCarNos, List<String> unRunCarNos){
        unRunCarNos.clear();
        unRunCarNos.addAll(Arrays.asList(args));
        for(String arg : args){
            unRunCarNos.remove(arg);
            arg = arg.trim();
            log.info("BrowserCabgov.start for. 获取车牌号:{}", arg);
            try{
                List<Map<String, String>> items = this.filterItem(arg);
                log.info("BrowserCabgov.start for try. 获取车牌号:{} {} <==================", arg, items.size());
                if(ValueUtils.isBlank(items)){
                    log.info("BrowserCabgov.start for continue. has no items not write data:{}", arg);
                    errorCarNos.add(arg);
                }else{
                    itemsList.addAll(items);
                }
            }catch (Exception e){
                log.error("BrowserCabgov.start for error. 12123违章车牌号:{}", arg, e);
                noItemCarNos.add(arg);
            }
            writeExcel(itemsList, errorCarNos, noItemCarNos);
        }
    }

    @SneakyThrows
    public boolean start(){
        log.info("BrowserCabgov.start in. 启动浏览器");
        if(!super.start()) return false;
        this.openAndLogin();
        this.checkLocal();
        String[] args = ReaderUtils.loadString(CONFIG_PATH + "/12123违章车牌号.txt", null).split("\n");
        log.info("BrowserCabgov.start prepare. args:{}", args);
        List<Map<String, String>> itemsList = new ArrayList<>();
        List<String> errorCarNos = new ArrayList<>();
        List<String> noItemCarNos = new ArrayList<>();
        List<String> unRunCarNos = new ArrayList<>();
        try{
            runBiz(args, itemsList, errorCarNos, noItemCarNos, unRunCarNos);
            int unDoNum = noItemCarNos.size();
            log.info("BrowserCabgov.doing check. {}/{}", unDoNum, args.length);
            if(Float.valueOf(unDoNum) / Float.valueOf(args.length) > 0.35){
                args = new String[unDoNum];
                for (int i = 0; i < noItemCarNos.size(); i++) {
                    args[i + errorCarNos.size()] = noItemCarNos.get(i);
                }
                noItemCarNos.clear();
                log.info("BrowserCabgov.agin alert. begin:{}", args);
                runBiz(args, itemsList, errorCarNos, noItemCarNos, unRunCarNos);
                log.info("BrowserCabgov.agin alert. end");
            }
            itemsList.clear();
        }finally {
            try{
                this.browser.quit();
            }catch (Exception e){};
            try{
                writeExcel(itemsList, errorCarNos, noItemCarNos);
            }catch (Exception e){}
            log.info("BrowserCabgov.start out");
        }
        return true;
    }


    @SneakyThrows
    public void writeExcel(List<Map<String, String>> itemsList, List<String> errorCarNos, List<String> noItemCarNos){
        log.info("BrowserCabgov.writeExcel in. 输出数据：itemsList.Size:{}", itemsList.size());
        String pathDateName = DateUtils.formatDate(new Date(), "YY年MM月dd日");
        String fileTimeName = DateUtils.formatDate(new Date(), "HHmmss");
        File dataPath = new File(DATA_PATH + "/" + pathDateName);
        if(!dataPath.exists())
            dataPath.mkdirs();
        if(itemsList.size() > 0){
            Gson gson = GsonBuilder.gsonDefault();
            WriterUtils.overwrite(new File(dataPath.getPath() + "/违法记录" + fileTimeName + ".txt"), gson.toJson(itemsList).getBytes());
            OutputStream os = new FileOutputStream(dataPath.getPath() + "/违法记录" + fileTimeName + ".xlsx");
            ExcelWriter.exportData(itemsList).write(os);
            os.flush();
            os.close();
        }
        if(ValueUtils.isNotBlank(errorCarNos)){
            log.info("BrowserCabgov.writeExcel error. 输出数据：警告-无违法记录:{}", ValueUtils.toString(errorCarNos));
            WriterUtils.overwrite(new File(dataPath.getPath() + "/警告-无违法记录-" + fileTimeName  + ".txt"), ValueUtils.toString(errorCarNos).getBytes());
        }
        if((ValueUtils.isNotBlank(noItemCarNos))){
            log.info("BrowserCabgov.writeExcel.noItem. 输出数据：异常-列表无数据:{}", ValueUtils.toString(noItemCarNos));
            WriterUtils.overwrite(new File(dataPath.getPath() + "/异常-列表无数据-" + fileTimeName  + ".txt"), ValueUtils.toString(noItemCarNos).toString().getBytes());
        }
        log.info("writeExcel.end. 输出数据：itemsList.Size:{}", itemsList.size());
    }

    List<Map<String, String>> filterItem(String value){
        log.info("BrowserCabgov.filterItem.start. 获取车牌号:{}", value);
        List<Map<String, String>> itemsList = new ArrayList<>();
        this.search(value);
        int i = 30;
        int pageTotal = 0;
        int pageIndex = 0;
        int itemTotal = 0;
        while (i -- > 0){
            try {
                Thread.sleep(1000);
                AtomicReference<List<WebElement>> webElements = new AtomicReference<>();
                AtomicReference<WebElement> webElement = new AtomicReference<>();
                try{
                    webElement.set(browser.getDriver().findElement(By.id("violationveh")));
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.warn("BrowserCabgov.filterItem.while. exception 车辆列表 ele:{}", "violationveh");
                    continue;
                }
                if(webElement.get() == null){
                    log.warn("BrowserCabgov.filterItem.while. not fund 车辆列表 ele:{}", "violationveh");
                    continue;
                }

                try{
                    WebElement dataNumEle = webElement.get().findElement(By.className("data-nums"));
                    WebElement temp = dataNumEle.findElement(By.xpath("p/span"));
                    pageTotal = Integer.parseInt(temp.getText());
                    log.info("BrowserCabgov.filterItem.while. 获取车辆列表数据总数:{}", pageTotal);
                }catch (Exception e){
                    log.warn("BrowserCabgov.filterItem.while. not fund 车辆列表数据总数");
                    pageTotal = -1;
                }

                try{
                    webElements.set(webElement.get().findElements(By.xpath("table/tbody/tr")));
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.warn("BrowserCabgov.filterItem.while. not fund 车辆列表 ele.ex:{}", "table/tbody/tr");
                    continue;
                }

                if(ValueUtils.isBlank(webElements)){
                    log.warn("BrowserCabgov.filterItem.while. not fund 车辆列表 ele:{}", "table/tbody/tr");
                    continue;
                }
//                WebElement pageNextEle = null;
                if(pageTotal < 1 || pageTotal > 10){
                    AtomicReference<List<WebElement>> pageEles = new AtomicReference();
                    AtomicReference<WebElement> pageELe = new AtomicReference<>();
                    WebElementUtils.whileDo(() -> {
                        try{
                            pageELe.set(browser.getDriver().findElement(By.id("mypagination1")));
                            Thread.sleep(1000);
                        }catch (Exception e){
                            log.warn("BrowserCabgov.filterItem.while in pages error. 获取翻页控件异常", e);
                            return false;
                        }
                        if(pageELe.get() == null){
                            log.warn("BrowserCabgov.filterItem.while in pages error. 未获取翻页控件");
                        }
                        try{
                            pageEles.set(pageELe.get().findElements(By.xpath("ul/li")));
                            Thread.sleep(1000);
                        }catch (Exception e){
                            log.warn("BrowserCabgov.filterItem.while in pages warn. ele:ul/li", e);
                            return false;
                        }
                        return true;
                    }, 10);

                    if(ValueUtils.isBlank(pageEles) || pageEles.get().size() <= 5){
                        log.warn("BrowserCabgov.filterItem.while in pages warn. 获取翻页控件分页异常");
                        continue;
                    }
                    int curPageNum = -1;
                    for(WebElement ele : pageEles.get()){
                        if("active".equals(ele.getAttribute("class"))){
                            curPageNum = Integer.parseInt(ele.getText());
                            break;
                        }
                    }
                    if(curPageNum != pageIndex + 1){
                        log.warn("BrowserCabgov.filterItem.while warn. 翻页拉取数据失败");
                        continue;
                    }
                }
                for(WebElement trEle : webElements.get()){
                    itemTotal ++;
                    List<WebElement> datas = trEle.findElements(By.xpath("td"));
                    String plateNo = WebElementUtils.getValue(datas.get(0));
                    if(!value.equals(plateNo.substring(1))){
                        throw new BusinessException("车牌号不一致");
                    }
                    String items = "";
                    for (WebElement data : datas){
                        try{
                            items += data.getText() + "|";
                        }catch (Exception e){
                            items += "|";
                        }
                    }
                    {
                        String dtype = datas.get(6).getText();
                        if(!itemTypes.contains(dtype)){
                            log.info("BrowserCabgov.filterItem.while. continue-{}.:{}", dtype, items);
                            continue;
                        }
                        log.info("BrowserCabgov.filterItem.while. doing-{}.:{}", dtype, items);
                    }

                    Map<String, String> item;
                    if(type == 0){
                        try{
                            Thread.sleep(500);
                            log.info("BrowserCabgov.filterItem.while. click detail view.a.begin");
                            WebElementUtils.click(browser, datas.get(7).findElement(By.xpath("a")));
                            log.info("BrowserCabgov.filterItem.while. click detail view.a.end");
                        }catch (Exception e){
                            throw e;
                        }
                        Thread.sleep(2000);
                        i = 30;
                        item = this.getDetailInfo();
                        item.put("状态", WebElementUtils.getValue(datas.get(4)) + "|" + WebElementUtils.getValue(datas.get(6)));
                    }else{
                        log.info("BrowserCabgov.filterItem.while. set list item begin");
                        item = new HashMap<>();
                        item.put("号牌号码", WebElementUtils.getValue(datas.get(0)));
                        item.put("违法时间", WebElementUtils.getValue(datas.get(1)));
                        item.put("违法地点", WebElementUtils.getValue(datas.get(2)));
                        item.put("违法行为", WebElementUtils.getValue(datas.get(3)));
                        item.put("采集单位", "");
                        item.put("罚款金额", "");
                        item.put("记分值", "");
                        item.put("处理时间", WebElementUtils.getValue(datas.get(5)));
                        item.put("状态", WebElementUtils.getValue(datas.get(4)) + "|" + WebElementUtils.getValue(datas.get(6)));
                    }
                    itemsList.add(item);
                    log.info("BrowserCabgov.filterItem.while. set list item end:{}", item);
                }

                if(pageTotal < 1 || pageTotal > 10){
                    WebElementUtils.whileDo(() -> {
                        try{
                            webElement.set(browser.getDriver().findElement(By.id("mypagination1")));
                            Thread.sleep(1000);
                        }catch (Exception e){
                            log.warn("BrowserCabgov.filterItem.while error. 获取翻页控件异常", e);
                            return false;
                        }
                        if(webElement.get() == null){
                            log.warn("BrowserCabgov.filterItem.while error. 未获取翻页控件");
                            return false;
                        }
                        try{
                            webElements.set(webElement.get().findElements(By.xpath("ul/li")));
                            Thread.sleep(1000);
                        }catch (Exception e){
                            log.warn("BrowserCabgov.filterItem.while warn. ele:ul/li", e);
                            return false;
                        }
                        return true;
                    }, 10);


                    if(ValueUtils.isBlank(webElements.get()) || webElements.get().size() <= 5){
                        log.warn("BrowserCabgov.filterItem.while error. 获取翻页控件分页异常");
                        break;
                    }
                    webElements.get().remove(0);
                    webElements.get().remove(0);
                    webElements.get().remove(webElements.get().size() - 1);
                    webElements.get().remove(webElements.get().size() - 1);

                    if(webElements.get().size() <= 1){
                        break;
                    }
                    List<WebElement> removes = new ArrayList<>();
                    for(WebElement ele : webElements.get()){
                        removes.add(ele);
                        if("active".equals(ele.getAttribute("class"))){
                            log.warn("BrowserCabgov.filterItem.while.while error. 获取翻页控件分页Active异常");
                            break;
                        }
                    }
                    webElements.get().removeAll(removes);
                    if(webElements.get().size() == 0){
                        break;
                    }
                    log.info("BrowserCabgov.filterItem.while.click.start. next page");
                    WebElementUtils.click(browser, webElements.get().get(0).findElement(By.xpath("a")));
                    log.info("BrowserCabgov.filterItem.while.click.end. next page");
                    pageIndex ++;
                    Thread.sleep(2000);
                }else{
                    break;
                }
                i = 30;
            }catch (BusinessException e){
                log.warn("BrowserCabgov.filterItem.while warn. i:{}", i, e);
            }catch (Exception e) {
                log.error("BrowserCabgov.filterItem.while error. i:{}", i);
                throw new RuntimeException(e);
            }
        }
        if(pageTotal <= 0){
            log.warn("BrowserCabgov.filterItem.while.end. alert pageTotal:{}", pageTotal);
        }else if(pageTotal != itemTotal){
            log.warn("BrowserCabgov.filterItem.while.end. alert pageTotal:{} != itemTotal:{}", pageTotal, itemTotal);
        }else {
            log.info("BrowserCabgov.filterItem.while.end. success pageTotal:{} == itemTotal:{}", pageTotal, itemTotal);
        }
        log.info("BrowserCabgov.filterItem.while.out. i:{}", i);
        if(i <= 0){
            throw new BusinessException("没有找到事故处理业务右上Tab");
        }
        return itemsList;
    }

    void openAndLogin(){
        log.info("BrowserCabgov.openAndLogin in. 启动浏览器");
        String errorMsg = null;
        int i = 300;
        while (i-- > 0){
            try {
                Thread.sleep(1000);
                List<WebElement> webElements = null;
                WebElement webElement = null;
                try{
                    webElement = browser.getDriver().findElement(By.className("pull-right"));
                }catch (Exception e){}
                if(webElement == null){
                    log.warn("BrowserCabgov.openAndLogin.while not fund 请选择服务类型 ele:{}", "pull-right");
                    continue;
                }
                try{
                    webElement = webElement.findElement(By.xpath("select"));
                }catch (Exception e){}
                if(webElement == null){
                    log.warn("BrowserCabgov.openAndLogin.while not fund 请选择服务类型 ele:{}", "select");
                    continue;
                }
                try{
                    webElements = webElement.findElements(By.xpath("option"));
                }catch (Exception e){}
                if(ValueUtils.isBlank(webElements)){
                    log.warn("BrowserCabgov.openAndLogin.while not fund 请选择服务类型 ele:{}", "option");
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
                log.info("BrowserCabgov.openAndLogin.while.click.start 请选择服务类型:{}", "非营运机动车信息服务");
                WebElementUtils.click(browser, webElements.get(0));
                log.info("BrowserCabgov.openAndLogin.while.click.end 请选择服务类型:{}", "非营运机动车信息服务");
                Thread.sleep(1000);
                log.info("BrowserCabgov.openAndLogin.while.click.start 交通违法查询");
                WebElementUtils.click(browser, browser.getDriver().findElement(By.id("sidebar_menu_5")));
                log.info("BrowserCabgov.openAndLogin.while.click.end 交通违法查询");
                Thread.sleep(1000);
                break;
            } catch (Exception e) {
                log.info("BrowserCabgov.openAndLogin.while error. i:{}", i);
                errorMsg = e.getMessage();
            }
        }
        if(i <= 0){
            throw new BusinessException("没有找到事故处理业务左边目录");
        }
        log.info("BrowserCabgov.openAndLogin out. 启动浏览器");
        if(ValueUtils.isNotBlank(errorMsg)){
            throw new BusinessException(errorMsg);
        }
    }

    void search(String text){
        log.info("BrowserCabgov.search in. 搜索：{}", text);
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
                    log.warn("BrowserCabgov.search while. not fund 违法查询 ele:{}", "mem-content.vehSearchForm");
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
                }catch (Exception e){
                    log.warn("BrowserCabgov.search while. not fund 违法查询开始日期控件 ele:{}", "add-on");
                    continue;
                }

                try{
                    WebElementUtils.click(browser, browser.getDriver().findElements(By.className("datetimepicker-months")).get(0).findElements(By.xpath("table/thead/tr/th")).get(1));
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.warn("BrowserCabgov.search while. 违法查询 ele:{}", "datetimepicker-months-option");
                    continue;
                }

                try{
                    String curYear = DateUtils.formatDate(DateUtils.parseToDate(LocalDate.now().plusYears(-1)), "YYYY");
                    List<WebElement> eles = browser.getDriver().findElements(By.className("datetimepicker-years")).get(0).findElements(By.xpath("table/tbody/tr/td/span"));
                    WebElement curEle = null;
                    for (WebElement ele : eles){
                        String tag = WebElementUtils.getValue(ele);
                        if(tag == null || !tag.equals(curYear)){
                            continue;
                        }
                        curEle = ele;
                        break;
                    }
                    if(curEle == null){
                        for (WebElement ele : eles){
                            if(!ele.getAttribute("class").contains("active")){
                                continue;
                            }
                            curEle = ele;
                            break;
                        }
                    }
                    if(curEle == null){
                        throw new BusinessException("没有找到当年年份按钮");
                    }
                    WebElementUtils.click(browser, curEle);
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.warn("BrowserCabgov.search while error. not fund 违法查询 ele:datetimepicker-years", e);
                    continue;
                }

                try{
                    WebElementUtils.click(browser,browser.getDriver().findElements(By.className("datetimepicker-months")).get(0).findElements(By.xpath("table/tbody/tr/td/span")).get(5));
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.warn("BrowserCabgov.search while error. not fund 违法查询 ele:datetimepicker-months", e);
                    continue;
                }

                try{
                    WebElementUtils.click(browser, browser.getDriver().findElements(By.className("datetimepicker-days")).get(0).findElements(By.xpath("table/tbody/tr/td")).get(6));
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.warn("BrowserCabgov.search while error. not fund 违法查询 ele:datetimepicker-days", e);
                    continue;
                }

                try{
                    WebElementUtils.click(browser, webElements.get(1).findElement(By.id("hpzl")).findElements(By.xpath("option")).get(3));
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.warn("BrowserCabgov.search while error. not fund 违法查询 ele:hpzl.option", e);
                    continue;
                }

                try{
                    WebElementUtils.setValue(webElements.get(2).findElement(By.id("hphm")), text);
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.warn("BrowserCabgov.search while error. not fund 违法查询 ele:hphm", e);
                    continue;
                }

                try{
                    WebElementUtils.click(browser, webElements.get(0).findElements(By.className("add-on")).get(1));
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.warn("BrowserCabgov.search while error. not fund 违法查询结束日期控件 ele:add-on", e);
                    continue;
                }
                try{
                    WebElementUtils.click(browser, browser.getDriver().findElements(By.className("datetimepicker-months")).get(1).findElements(By.className("today")).get(0));
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.warn("BrowserCabgov.search while error. not fund 违法查询结束日期控件 ele:tody", e);
                    continue;
                }
                try{
                    WebElement enDataInput = webElement.findElement(By.id("endDate2"));
                    if(enDataInput == null){
                        System.exit(0);
                    }
                    Long endDateL = Long.parseLong(WebElementUtils.getValue(enDataInput).toString());
                    if(curDateL < endDateL){
                        System.exit(0);
                    }
                }catch (Exception e){
                    log.warn("BrowserCabgov.search while error. not fund 违法查询结束日期控件 ele:tody", e);
                    continue;
                }

                try{
                    WebElementUtils.click(browser, webElements.get(3).findElement(By.xpath("button")));
                    Thread.sleep(1000);
                }catch (Exception e){
                    log.warn("BrowserCabgov.search while error. not fund 违法查询 ele:button", e);
                    continue;
                }
                errorMsg = null;
                break;
            } catch (InterruptedException e) {
                log.warn("BrowserCabgov.search while error. i:{}", i);
                throw new RuntimeException(e);
            }
        }
        log.info("BrowserCabgov.search out. {}", text);
        if(i <= 0){
            throw new BusinessException("没有找到事故处理业务右上Tab");
        }
        if(ValueUtils.isNotBlank(errorMsg)){
            throw new BusinessException(errorMsg);
        }
    }

    @SneakyThrows
    Map<String, String> getDetailInfo() {
        log.info("BrowserCabgov.getDetailInfo in.");
        try {
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
                        log.warn("BrowserCabgov.getDetailInfo while. not fund 查看详情 ele:modal-body");
                        continue;
                    }

                    try{
                        webElement = webElement.findElement(By.className("xqInfo"));
                    }catch (Exception e){}
                    if(webElement == null){
                        log.warn("BrowserCabgov.getDetailInfo while. not fund 查看详情 ele:xqInfo");
                        continue;
                    }
                    try{
                        webElements = webElement.findElements(By.xpath("form/div"));
                    }catch (Exception e){}
                    if(ValueUtils.isBlank(webElements) || webElements.size() < 1){
                        log.warn("BrowserCabgov.getDetailInfo while. not fund 查看详情 ele:form/div");
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
                                log.info("BrowserCabgov.getDetailInfo while. charles data field null, waiting ==================");
                                isGoon = true;
                                break;
                            }
                            data.put(name, value);
                        }
                        if(isGoon){
                            Thread.sleep(1000);
                            continue;
                        }
                        log.info("BrowserCabgov.getDetailInfo while. charles data success:{}", GsonBuilder.gsonDefault().toJson(data));
                        itemMap.putAll(data);
                        break;
                    }
                    errorMsg = null;
                    break;
                } catch (InterruptedException e) {
                    log.warn("BrowserCabgov.getDetailInfo while error. i:{}", i);
                    throw new RuntimeException(e);
                }
            }
            log.info("BrowserCabgov.getDetailInfo out. {}", itemMap);
            if(i <= 0){
                throw new BusinessException("没有找到事故处理业务右上Tab");
            }
            if(ValueUtils.isNotBlank(errorMsg)){
                throw new BusinessException(errorMsg);
            }
            return itemMap;
        }finally {
            log.info("BrowserCabgov.getDetailInfo click.start 查看详情: close");
            WebElement webElement = browser.getDriver().findElement(By.id("view"));
            WebElementUtils.click(browser, webElement.findElement(By.id("bind_close")));
            log.info("=BrowserCabgov.getDetailInfo click.end 查看详情: close");
            Thread.sleep(1000);
        }
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

}
