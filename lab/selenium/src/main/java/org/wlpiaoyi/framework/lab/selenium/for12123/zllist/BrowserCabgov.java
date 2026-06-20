package org.wlpiaoyi.framework.lab.selenium.for12123.zllist;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.wlpiaoyi.framework.lab.selenium.for12123.BrowserBase;
import org.wlpiaoyi.framework.lab.selenium.for12123.zllist.excel.ZlListExcelWriter;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class BrowserCabgov extends BrowserBase {


    public BrowserCabgov(int type){
        super(type);
    }


    @SneakyThrows
    public boolean start(){
        log.info("BrowserCabgov.start in. 启动浏览器");
        if(!super.start()) return false;
        this.openAndLogin();
        this.checkLocal();
        String[] args = ReaderUtils.loadString(CONFIG_PATH + "/12123违章车牌号.txt", null).split("\n");
        log.info("BrowserCabgov.start prepare. args:{}", args);
        List<RentalContract> itemsList = new ArrayList<>();
        List<String> errorCarNos = new ArrayList<>();
        try{
            for (String value : args) {
                value = value.trim();
                if(ValueUtils.isBlank(value)){
                    continue;
                }
                try {
                    List<RentalContract> items = this.search(value);
                    if(ValueUtils.isBlank(items)){
                        errorCarNos.add(value);
                    }else{
                        itemsList.addAll(items);
                    }
                } catch (Exception e) {
                    log.error("BrowserCabgov.start search error. 车牌号:{}", value, e);
                    errorCarNos.add(value);
                }
            }
        }finally {
            try{
                writeExcel(itemsList, errorCarNos);
            }catch (Exception e){
                log.error("BrowserCabgov.start writeExcel error", e);
            }
            try{
                this.browser.quit();
            }catch (Exception e){}
            log.info("BrowserCabgov.start out");
        }
        return true;
    }

    @SneakyThrows
    void writeExcel(List<RentalContract> itemsList, List<String> errorCarNos){
        log.info("BrowserCabgov.writeExcel in. itemsList.Size:{}", itemsList.size());
        String pathDateName = DateUtils.formatDate(new Date(), "YY年MM月dd日");
        String fileTimeName = DateUtils.formatDate(new Date(), "HHmmss");
        File dataPath = new File(DATA_PATH + "/" + pathDateName);
        if(!dataPath.exists()){
            dataPath.mkdirs();
        }
        if(!itemsList.isEmpty()){
            List<Map<String, String>> mapList = new ArrayList<>();
            for (RentalContract item : itemsList) {
                mapList.add(item.toMap());
            }
            Gson gson = GsonBuilder.gsonDefault();
            WriterUtils.overwrite(new File(dataPath.getPath() + "/违章记录" + fileTimeName + ".txt"), gson.toJson(mapList).getBytes());
            OutputStream os = new FileOutputStream(dataPath.getPath() + "/违章记录" + fileTimeName + ".xlsx");
            ZlListExcelWriter.exportData(mapList).write(os);
            os.flush();
            os.close();
        }
        if(ValueUtils.isNotBlank(errorCarNos)){
            log.info("BrowserCabgov.writeExcel error. 无违章记录记录:{}", ValueUtils.toString(errorCarNos));
            WriterUtils.overwrite(new File(dataPath.getPath() + "/警告-无违章记录-" + fileTimeName + ".txt"), ValueUtils.toString(errorCarNos).getBytes());
        }
        log.info("BrowserCabgov.writeExcel end. itemsList.Size:{}", itemsList.size());
    }

    List<RentalContract> search(String carNo){
        List<RentalContract> result = new ArrayList<>();
        carNo = carNo.trim();
        WebElement contextEl;
        try{
            contextEl = browser.getDriver().findElement(By.id("jsrcx"));
        }catch (Exception e){
            throw new BusinessException("没有找到搜索框");
        }
        {
            WebElement inputCarNoEl;
            try{
                inputCarNoEl = contextEl.findElement(By.id("hphm"));;
            }catch (Exception e){
                throw new BusinessException("没有找到车牌号输入框");
            }
            WebElementUtils.setValue(inputCarNoEl, carNo);
        }

        {
            //开始时间输入
            WebElement inputEndDateEl;
            try{
                inputEndDateEl = contextEl.findElement(By.id("endDate2"));;
            }catch (Exception e){
                throw new BusinessException("没有找到结束时间输入框");
            }
            var endDate = DateUtils.parseLocalDateTime(WebElementUtils.getValue(inputEndDateEl));
            this.selectedYearMonth(endDate.plusMonths(-7));
        }
        {
            //点击搜索按钮
            var searchBtn = contextEl.findElement(By.id("jdcquery"));
            WebElementUtils.click(browser, searchBtn);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        {
            // 检查数据结果
            if(!this.checkData(carNo)){
                throw new BusinessException("数据结果检查失败");
            }
        }


        int pageTotal = 0;
        int pageIndex = 0;
        int itemTotal = 0;
        int waitCount = 0;
        while (waitCount ++ < 30){
            try{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //获取数据表格
                var dataTable = browser.getDriver().findElement(By.id("tscVeh"));
                var dataBody = WebElementUtils.getChildrenByTag(dataTable, "tbody");
                if(ValueUtils.isBlank(dataBody)){
                    continue;
                }
                var dataRows = WebElementUtils.getChildrenByTag(dataBody.get(0), "tr");
                for (WebElement dataRow : dataRows) {
                    var tds = WebElementUtils.getChildrenByTag(dataRow, "td");
                    if(ValueUtils.isBlank(tds) || tds.size() < 4) {
                        dataRows = null;
                        break;
                    }
                    RentalContract item = parseRow(tds);
                    log.info("BrowserCabgov.search.rows.item:{}", item);
                    if(item == null){
                        continue;
                    }
                    if(ValueUtils.isBlank(item.getViolationCount())){
                        continue;
                    }
                    if(Integer.parseInt(item.getViolationCount()) <= 0){
                        continue;
                    }
                    if(!"正常".equals(item.getStatus())){
                        continue;
                    }
                    log.info("BrowserCabgov.search.rows.item 正常");
                    WebElementUtils.doubleClick(browser.getDriver(), dataRow);
                    var detail = this.checkDetailData();
                    item.applyDetail(detail);
                    result.add(item);
                    log.info("BrowserCabgov.search.rows.item detail:{}", detail);
                }
                if(ValueUtils.isBlank(dataRows)) continue;
                var tscVehPage = browser.getDriver().findElement(By.id("tscVeh_page"));
                try{
                    WebElement dataNumEle = tscVehPage.findElement(By.className("data-nums"));
                    WebElement temp = dataNumEle.findElement(By.xpath("p/span"));
                    pageTotal = Integer.parseInt(temp.getText());
                    log.info("BrowserCabgov.search. 获取合同列表数据总数:{}", pageTotal);
                }catch (Exception e){
                    log.warn("BrowserCabgov.search. not fund 合同列表数据总数");
                    pageTotal = -1;
                }
                if(pageTotal < 10){
                    waitCount = 0;
                    break;
                }

                AtomicReference<List<WebElement>> pageEles = new AtomicReference();
                AtomicReference<WebElement> pageELe = new AtomicReference<>();
                WebElementUtils.whileDo((times) -> {
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

                pageEles.get().remove(0);
                pageEles.get().remove(0);
                pageEles.get().remove(pageEles.get().size() - 1);
                pageEles.get().remove(pageEles.get().size() - 1);


                if(pageEles.get().size() <= 1){
                    waitCount = 999;
                    break;
                }
                List<WebElement> removes = new ArrayList<>();
                for(WebElement ele : pageEles.get()){
                    removes.add(ele);
                    if("active".equals(ele.getAttribute("class"))){
                        log.info("BrowserCabgov.filterItem.while.while. 活动翻页控件分页");
                        break;
                    }
                }
                pageEles.get().removeAll(removes);
                if(pageEles.get().size() == 0){
                    waitCount = 999;
                    break;
                }
                log.info("BrowserCabgov.filterItem.while.click.start. next page");
                WebElementUtils.click(browser, pageEles.get().get(0).findElement(By.xpath("a")));
                log.info("BrowserCabgov.filterItem.while.click.end. next page");
                pageIndex ++;
                waitCount = 0;

            }catch (Exception e){
                log.warn("BrowserCabgov.search.rows while. i:{}", waitCount, e);
            }
        }

        return result;
    }

    public RentalContractDetail checkDetailData() {
        log.info("BrowserCabgov.checkDetailData in");
        RentalContractDetail detail = null;
        int waitCount = 0;
        while (waitCount++ < 10) {
            try {
                Thread.sleep(500);
                var memContent = browser.getDriver().findElement(By.id("mem-content"));
                var tableDetail = memContent.findElement(By.cssSelector("table._data.api-illegalrecord"));
                detail = parseDetailTable(tableDetail);
                if (ValueUtils.isNotBlank(detail.getContractNo())) {
                    log.info("BrowserCabgov.checkDetailData end. detail:{}", detail);
                    break;
                }
            } catch (Exception e) {
                log.warn("BrowserCabgov.checkDetailData wait:{}", waitCount, e);
            }
        }
        if(detail == null) {
            throw new BusinessException("解析违章记录详情失败");
        }
        backToListPage();
        return detail;
    }

    private void backToListPage() {
        log.info("BrowserCabgov.backToListPage in");
        browser.back();
        log.info("BrowserCabgov.backToListPage end");
    }


    private RentalContractDetail parseDetailTable(WebElement tableDetail) {
        return RentalContractDetail.builder()
                .contractNo(getDetailCellValue(tableDetail, "htbh"))
                .idCardNo(getDetailCellValue(tableDetail, "sfzmhm"))
                .validityStart(getDetailCellValue(tableDetail, "zlkssj"))
                .leaseType(getDetailCellValue(tableDetail, "htlx"))
                .validityEnd(getDetailCellValue(tableDetail, "zljssj"))
                .signDate(getDetailCellValue(tableDetail, "htqdrq"))
                .plateType(getDetailCellValue(tableDetail, "hpzlStr"))
                .carNo(getDetailCellValue(tableDetail, "hphm"))
                .build();
    }

    private String getDetailCellValue(WebElement tableDetail, String cellId) {
        WebElement td;
        try {
            td = tableDetail.findElement(By.id(cellId));
        } catch (Exception e) {
            log.warn("BrowserCabgov.getDetailCellValue 未找到字段:{}", cellId);
            return null;
        }
        String text = WebElementUtils.getValue(td);
        return ValueUtils.isBlank(text) ? null : text.trim();
    }

    public boolean readItems(){

        int waitCount = 0;
        while (waitCount ++ < 30){
            try{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //获取数据表格
                var dataTable = browser.getDriver().findElement(By.id("tscVeh"));
                var dataBody = WebElementUtils.getChildrenByTag(dataTable, "tbody");
                if(ValueUtils.isBlank(dataBody)){
                    continue;
                }
                var dataRows = WebElementUtils.getChildrenByTag(dataBody.get(0), "tr");
                int rightRow = 0;
                for (WebElement dataRow : dataRows) {
                    var tds = WebElementUtils.getChildrenByTag(dataRow, "td");
                    if(tds.size() < 9){
                        continue;
                    }
                    RentalContract item = parseRow(tds);
                    if(item == null){
                        continue;
                    }
                }
                return rightRow == dataRows.size();
            }catch (Exception e){
                log.warn("BrowserCabgov.search.rows while. i:{}", waitCount, e);
            }
        }
        return false;
    }

    public boolean checkData(String carNo){

        int waitCount = 0;
        while (waitCount ++ < 30){
            try{
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                //获取数据表格
                var dataTable = browser.getDriver().findElement(By.id("tscVeh"));
                var dataBody = WebElementUtils.getChildrenByTag(dataTable, "tbody");
                if(ValueUtils.isBlank(dataBody)){
                    continue;
                }
                var dataRows = WebElementUtils.getChildrenByTag(dataBody.get(0), "tr");
                int rightRow = 0;
                for (WebElement dataRow : dataRows) {
                    var tds = WebElementUtils.getChildrenByTag(dataRow, "td");
                    if(tds.size() < 9){
                        continue;
                    }
                    RentalContract item = parseRow(tds);
                    if(item == null){
                        continue;
                    }
                    if(!carNo.equals(item.getCarNo()) && !carNo.equals(stripPlatePrefix(item.getCarNo()))){
                        log.warn("BrowserCabgov.search 车牌号不一致, expect:{}, actual:{}", carNo, item.getCarNo());
                        continue;
                    }
                    rightRow ++;
                    log.info("BrowserCabgov.search 解析违章记录:{}", item);
                }
                return rightRow == dataRows.size();
            }catch (Exception e){
                log.warn("BrowserCabgov.search.rows while. i:{}", waitCount, e);
            }
        }
        return false;
    }

    private RentalContract parseRow(List<WebElement> tds){
        String contractNo = getCellValue(tds.get(0));
        if(ValueUtils.isBlank(contractNo)){
            return null;
        }
        return RentalContract.builder()
                .contractNo(contractNo)
                .carNo(getCellValue(tds.get(1)))
                .plateType(getCellValue(tds.get(2)))
                .validityStart(getCellValue(tds.get(3)))
                .validityEnd(getCellValue(tds.get(4)))
                .violationCount(getCellValue(tds.get(5)))
                .lessee(getCellValue(tds.get(6)))
                .signTime(getCellValue(tds.get(7)))
                .status(getCellValue(tds.get(8)))
                .build();
    }

    private String getCellValue(WebElement td){
        String value = td.getAttribute("title");
        if(ValueUtils.isBlank(value)){
            value = WebElementUtils.getValue(td);
        }
        if(ValueUtils.isNotBlank(value)){
            return value.trim();
        }
        return null;
    }

    private String stripPlatePrefix(String carNo){
        if(ValueUtils.isBlank(carNo) || carNo.length() <= 1){
            return carNo;
        }
        return carNo.substring(1);
    }


    void selectedYearMonth(LocalDateTime dateTime){

        var inputStartDateEl = browser.getDriver().findElement(By.id("startDate2"));
        var parentEl = WebElementUtils.getParentSafely(inputStartDateEl);
        var spanAddEL = parentEl.findElement(By.className("add-on"));
        WebElementUtils.click(browser, spanAddEL);
        int index = 0;
        {
            WebElement yearMonthEle = browser.getDriver().findElements(By.className("datetimepicker-months")).get(index);
            if(yearMonthEle == null){
                throw new RuntimeException("未找到时间选择器");
            }
            List<WebElement> yearMonthTableEle = WebElementUtils.getChildrenByTag(yearMonthEle, "table");
            if(ValueUtils.isBlank(yearMonthTableEle)){
                throw new RuntimeException("未找到日历Table");
            }
            List<WebElement> yearMonthTableTHeadEle = WebElementUtils.getChildrenByTag(yearMonthTableEle.get(0), "thead");
            List<WebElement> yearMonthTableTBodyEle = WebElementUtils.getChildrenByTag(yearMonthTableEle.get(0), "tbody");
            if (ValueUtils.isBlank(yearMonthTableTHeadEle)){
                throw new RuntimeException("未找到日历TableHead");
            }
            if (ValueUtils.isBlank(yearMonthTableTBodyEle)){
                throw new RuntimeException("未找到日历TableBody");
            }
            {
                List<WebElement> elements = WebElementUtils.getVisibleChildren(yearMonthTableTHeadEle.get(0));
                if (ValueUtils.isBlank(elements)){
                    throw new RuntimeException("未找到日历TableHeadElement");
                }
                String yearEleValueStr = WebElementUtils.getValue(WebElementUtils.getChildrenByClass(elements.get(0), "switch").get(0));
                if(ValueUtils.isBlank(yearEleValueStr)){
                    throw new RuntimeException("未找到日历YearValue");
                }
                Long yearEleValueSuffix = Long.parseLong(yearEleValueStr) - dateTime.getYear();
                if(yearEleValueSuffix != 0){
                    WebElement skipEle;
                    if(yearEleValueSuffix > 0){
                        skipEle = WebElementUtils.getChildrenByClass(elements.get(0), "prev").get(0);
                    }else{
                        skipEle = WebElementUtils.getChildrenByClass(elements.get(0), "next").get(0);
                    }
                    for (int i = 0; i < Math.abs(yearEleValueSuffix); i++) {
                        WebElementUtils.click(this.browser, skipEle);
                    }
                }
            }
            {
                List<WebElement> elements = WebElementUtils.getVisibleChildren(WebElementUtils.getVisibleChildren(WebElementUtils.getVisibleChildren(yearMonthTableTBodyEle.get(0)).get(0)).get(0));
                if (ValueUtils.isBlank(elements)){
                    throw new RuntimeException("未找到日历TableBodyElement");
                }
                if(elements.size() != 12){
                    throw new RuntimeException("日历TableBodyElement数量不对");
                }
                WebElementUtils.click(this.browser, elements.get(dateTime.getMonth().getValue() - 1));
            }
        }
        {
            WebElement monthDayEle = browser.getDriver().findElements(By.className("datetimepicker-days")).get(index);
            List<WebElement> monthDayTableEle = WebElementUtils.getChildrenByTag(monthDayEle, "table");
            if(ValueUtils.isBlank(monthDayTableEle)){
                throw new RuntimeException("未找到日历月Table");
            }
            List<WebElement> monthDayTableTBodyEle = WebElementUtils.getChildrenByTag(monthDayTableEle.get(0), "tbody");
            if (ValueUtils.isBlank(monthDayTableTBodyEle)){
                throw new RuntimeException("未找到日历约TableBody");
            }
            List<WebElement> dayBodyTrEle = WebElementUtils.getVisibleChildren(monthDayTableTBodyEle.get(0));
            if (ValueUtils.isBlank(dayBodyTrEle)){
                throw new RuntimeException("未找到日历月DayBodyTrElement");
            }
            List<WebElement> dayEles = new ArrayList<>();
            int tagi = -1;
            for (WebElement ele : dayBodyTrEle) {
                List<WebElement> dayBodyTrTdEle = WebElementUtils.getVisibleChildren(ele);
                for (WebElement tdEle : dayBodyTrTdEle){
                    int value = Integer.parseInt(WebElementUtils.getValue(tdEle));
                    if(tagi == -1){
                        if(value != 1){
                            continue;
                        }
                        tagi = 0;
                    }else if(tagi == 0){
                        if(value == 1){
                            tagi = 1;
                            break;
                        }
                    }
                    dayEles.add(tdEle);
                }
            }
            if(dayEles.size() < dateTime.getDayOfMonth()){
                throw new RuntimeException("日历天数不对:" + dateTime.getMonth());
            }
            WebElementUtils.click(this.browser, dayEles.get(dateTime.getDayOfMonth() - 1));
        }
    }


    void openAndLogin(){
        String errorMsg = null;
        int i = 300;
        while (i-- > 0){
            if(this.browser.isClosed()){
                throw new BusinessException("浏览器已关闭");
            }
            try {
                Thread.sleep(1000);
                if(this.getBrowser().isClosed()){
                    log.warn("BrowserCabgov.openAndLogin.while warn. 浏览器已关闭");
                    break;
                }
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
                WebElementUtils.whileDo(new WebElementUtils.Runnable() {
                    @Override
                    public boolean run(int times) {
                        WebElementUtils.click(browser, browser.getDriver().findElement(By.id("sidebar_menu_93")));
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        return Objects.requireNonNull(browser.getDriver().findElement(By.id("sidebar_menu_93"))
                                .getAttribute("class")).contains("active");
                    }
                }, 3);
                log.info("==========< click 违章记录");
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

}
