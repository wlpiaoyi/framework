package org.wlpiaoyi.framework.lab.selenium.for12123.lease.test;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.wlpiaoyi.framework.lab.selenium.for12123.BrowserBase;
import org.wlpiaoyi.framework.lab.selenium.for12123.lease.excel.ExcelReaderUtil;
import org.wlpiaoyi.framework.lab.selenium.utils.WebElementUtils;
import org.wlpiaoyi.framework.utils.DateUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.data.WriterUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class BrowserCabgov extends BrowserBase {


    public BrowserCabgov(int type) {
        super(type);
    }

    @SneakyThrows
    public boolean start(){
        log.info("BrowserCabgov.start in. 启动浏览器");
        if(!super.start()) return false;
        this.openAndLogin();
        this.checkLocal();
        try{
            String filePath = CONFIG_PATH + "\\12123司机信息表.xlsx";
            List<SubmitHT> submitHTList = ExcelReaderUtil.readExcelToSubmitHTList(filePath);
            log.info("BrowserCabgov.start 已经读取到Excel数据:{}条", submitHTList.size());
            String curTimeName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            File erroFile = new File(DATA_PATH + "\\12123司机信息错误-" + curTimeName + ".txt");
            for (SubmitHT submitHT : submitHTList) {
                if(this.browser.isClosed()){
                    log.warn("BrowserCabgov.start 浏览器已关闭");
                    break;
                }
                try {
                    log.info("BrowserCabgov.start 准备打开绑定窗口,绑定数据:{}", submitHT.toString());
                    WebElement addBoxEle = this.openAddBox();
                    log.info("BrowserCabgov.start 打开绑定窗口成功, 准备提交数据");
                    this.submitHT(submitHT, addBoxEle);
                    log.info("BrowserCabgov.start 提交数据成功, 确认提交");
                }catch (Exception e){
                    log.error("BrowserCabgov.start 提交合同信息失败:{}", submitHT.toString(), e);
                    // 检查父目录是否存在，不存在则创建
                    File parentDir = erroFile.getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        boolean dirsCreated = parentDir.mkdirs();
                        if (!dirsCreated) {
                            throw new IOException("创建目录失败: " + parentDir.getAbsolutePath());
                        }
                    }
                    // 检查文件是否存在，不存在则创建
                    if (!erroFile.exists()) {
                        boolean fileCreated = erroFile.createNewFile();
                        if (!fileCreated) {
                            throw new IOException("创建文件失败: " + erroFile.getAbsolutePath());
                        }
                    }
                    WriterUtils.append(erroFile, submitHT.getCardId() + ":" + e.getMessage() + "\r\n", StandardCharsets.UTF_8);
                }
            }
        }catch (Exception e){
            log.error("error", e);
        }finally {
            try{
                this.browser.quit();
            }catch (Exception e){};
            log.info("BrowserCabgov.start end");
        }
        return true;
    }

    void submitHT(SubmitHT submitHT, WebElement addBoxEle){
        log.info("BrowserCabgov.submitHT in. 准备选择车辆类型");
        var webElements = addBoxEle.findElement(By.id("hpzl_lr")).findElements(By.xpath("option"));
        WebElementUtils.click(browser, webElements.getLast());
        log.info("BrowserCabgov.submitHT 选择车辆类型成功");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        log.info("BrowserCabgov.submitHT 准备选择租赁类型");
        webElements = addBoxEle.findElement(By.id("zllx_lr")).findElements(By.xpath("option"));
        WebElementUtils.click(browser, webElements.getLast());
        log.info("BrowserCabgov.submitHT 选择租赁类型成功");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }


        log.info("BrowserCabgov.submitHT 获取填写车牌号组件");
        WebElement carNoEle = addBoxEle.findElement(By.id("hphm_lr"));
        log.info("BrowserCabgov.submitHT 获取合同号组件");
        WebElement htNoEle = addBoxEle.findElement(By.id("htbh_lr"));
        log.info("BrowserCabgov.submitHT 获取合同签订时间组件");
        WebElement htSignTimeEle = addBoxEle.findElement(By.id("htqdsj_lr"));
        log.info("BrowserCabgov.submitHT 获取租借开始时间组件");
        WebElement leaseStartTimeEle = addBoxEle.findElement(By.id("zlkssj_lr"));
        log.info("BrowserCabgov.submitHT 获取租借结束时间组件");
        WebElement leaseEndTimeEle = addBoxEle.findElement(By.id("zljssj_lr"));
        log.info("BrowserCabgov.submitHT 获取身份证号码组件");
        WebElement cardIdEle = addBoxEle.findElement(By.id("sfzmhm_lr"));
        log.info("BrowserCabgov.submitHT 获取保存按钮组件");
        WebElement saveEle = addBoxEle.findElement(By.id("htlrSave"));

        log.info("BrowserCabgov.submitHT 开始填写车牌号");
        WebElementUtils.setValue(carNoEle, submitHT.getCarNo());
        log.info("BrowserCabgov.submitHT 填写车牌号成功");
        log.info("BrowserCabgov.submitHT 开始填写合同号");
        WebElementUtils.setValue(htNoEle, submitHT.getHtNo());
        log.info("BrowserCabgov.submitHT 填写合同号成功");
        {
            log.info("BrowserCabgov.submitHT 开始填写合同签订时间");
            List<WebElement> addOnEles = WebElementUtils.getChildrenByClass(WebElementUtils.getParentSafely(htSignTimeEle), "add-on");
            if(ValueUtils.isBlank(addOnEles)){
                throw new RuntimeException("BrowserCabgov.submitHT 未找到时间触发器");
            }
            WebElementUtils.click(browser, addOnEles.get(0));
            WebElement yearMonthEle = browser.getDriver().findElements(By.className("datetimepicker-months")).get(2);
            if(yearMonthEle == null){
                throw new RuntimeException("BrowserCabgov.submitHT 未找到时间选择器");
            }
            this.checkValid();
            WebElementUtils.click(browser, addOnEles.get(0));
            yearMonthEle = browser.getDriver().findElements(By.className("datetimepicker-months")).get(2);
            if(yearMonthEle == null){
                throw new RuntimeException("BrowserCabgov.submitHT 未找到时间选择器");
            }
            if(submitHT.getHtSignTime() == null){
                this.selectedNow(yearMonthEle);
            }else{
                this.selectedYearMonth(2, submitHT.getHtSignTime());
            }
            log.info("BrowserCabgov.submitHT 填写合同签订时间成功");

        }
        {
            log.info("BrowserCabgov.submitHT 开始填写租借开始时间");
            List<WebElement> addOnEles = WebElementUtils.getChildrenByClass(WebElementUtils.getParentSafely(leaseStartTimeEle), "add-on");
            if(ValueUtils.isBlank(addOnEles)){
                throw new RuntimeException("未找到时间触发器");
            }
            WebElementUtils.click(browser, addOnEles.get(0));
            this.selectedYearMonth(3, submitHT.getLeaseStartTime());
            log.info("BrowserCabgov.submitHT 填写租借开始时间成功");
        }
        {
            log.info("BrowserCabgov.submitHT 开始填写租借结束时间");
            List<WebElement> addOnEles = WebElementUtils.getChildrenByClass(WebElementUtils.getParentSafely(leaseEndTimeEle), "add-on");
            if(ValueUtils.isBlank(addOnEles)){
                throw new RuntimeException("未找到时间触发器");
            }
            WebElementUtils.click(browser, addOnEles.get(0));
            this.selectedYearMonth(4, submitHT.getLeaseEndTime());
            log.info("BrowserCabgov.submitHT 填写租借结束时间成功");
        }
        log.info("BrowserCabgov.submitHT 开始填写身份证号码");
        WebElementUtils.setValue(cardIdEle, submitHT.getCardId());
        log.info("BrowserCabgov.submitHT 填写身份证号码成功");
        log.info("BrowserCabgov.submitHT 开始点击保存按钮");
        WebElementUtils.click(browser, saveEle);
        log.info("BrowserCabgov.submitHT 点击保存按钮成功");
        int i = 30;
        Alert alert = null;
        while (i -- > 0){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            try {
                alert = this.browser.getDriver().switchTo().alert();
                break;
            } catch (Exception e) {
            }
        }
        if(alert == null){
            throw new RuntimeException("保存失败,未知错误");
        }
        String alertText = alert.getText();
        alert.accept();
        if(alertText.equals("保存成功")){
            return;
        }
        throw new RuntimeException("保存失败[" + alertText + "]");
    }

    void checkValid(){
        try{
            WebElement yearMonthEle = browser.getDriver().findElements(By.className("datetimepicker-months")).get(2);
            if(yearMonthEle == null){
                throw new RuntimeException("未找到时间选择器");
            }
            List<WebElement> yearMonthTableEle = WebElementUtils.getChildrenByTag(yearMonthEle, "table");
            if(ValueUtils.isBlank(yearMonthTableEle)){
                throw new RuntimeException("未找到日历Table");
            }
            WebElementUtils.click(browser, yearMonthTableEle.get(0).findElement(By.xpath("tfoot/tr/th")));
            LocalDateTime dt = DateUtils.formatToLoaTolDateTime(WebElementUtils.getValue(browser.getDriver().findElement(By.id("htqdsj_lr"))), "yyyy-MM-dd HH:mm");
            if(DateUtils.parseToTimestamp(dt) > DateUtils.formatToDate(this.curDateL + "", "yyyyMMdd").getTime()){
                System.exit(0);
            }
        } catch (Exception e) {
            log.error("BrowserCabgov.checkValid 获取时间选择器异常", e);
            System.exit(0);
        }

    }
    void selectedYearMonth(int index, LocalDateTime dateTime){
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

        {
            WebElement monthDayEle = browser.getDriver().findElements(By.className("datetimepicker-hours")).get(index);
            List<WebElement> dayHoursTableEle = WebElementUtils.getChildrenByTag(monthDayEle, "table");
            if(ValueUtils.isBlank(dayHoursTableEle)){
                throw new RuntimeException("未找到日历天Table");
            }
            List<WebElement> dayHoursTBodyEle = WebElementUtils.getChildrenByTag(dayHoursTableEle.get(0), "tbody");
            if (ValueUtils.isBlank(dayHoursTBodyEle)){
                throw new RuntimeException("未找到日历天TableBody");
            }
            List<WebElement> hoursBodyTrEle = WebElementUtils.getVisibleChildren(dayHoursTBodyEle.get(0));
            if (ValueUtils.isBlank(hoursBodyTrEle)){
                throw new RuntimeException("未找到日历天HoursBodyTrElement");
            }
            List<WebElement> hoursBodyTdEle = WebElementUtils.getVisibleChildren(hoursBodyTrEle.get(0));
            if (ValueUtils.isBlank(hoursBodyTdEle)){
                throw new RuntimeException("未找到日历天HoursBodyTdElement");
            }
            List<WebElement> hourEles = WebElementUtils.getVisibleChildren(hoursBodyTdEle.get(0));
            if(ValueUtils.isBlank(hourEles) || hourEles.size() != 24){
                throw new RuntimeException("日历小时数不对:" + dateTime.getMonth());
            }
            WebElementUtils.click(this.browser, hourEles.get(dateTime.getHour()));
        }


        {
            WebElement monthDayEle = browser.getDriver().findElements(By.className("datetimepicker-minutes")).get(index);
            List<WebElement> hoursMinutesTableEle = WebElementUtils.getChildrenByTag(monthDayEle, "table");
            if(ValueUtils.isBlank(hoursMinutesTableEle)){
                throw new RuntimeException("未找到日历天小时Table");
            }
            List<WebElement> hoursMinutesTBodyEle = WebElementUtils.getChildrenByTag(hoursMinutesTableEle.get(0), "tbody");
            if (ValueUtils.isBlank(hoursMinutesTBodyEle)){
                throw new RuntimeException("未找到日历天小时TableBody");
            }
            List<WebElement> minutesBodyTrEle = WebElementUtils.getVisibleChildren(hoursMinutesTBodyEle.get(0));
            if (ValueUtils.isBlank(minutesBodyTrEle)){
                throw new RuntimeException("未找到日历天小时minutesBodyTrEle");
            }
            List<WebElement> minutesBodyTdEle = WebElementUtils.getVisibleChildren(minutesBodyTrEle.get(0));
            if (ValueUtils.isBlank(minutesBodyTdEle)){
                throw new RuntimeException("未找到日历天小时MinutesBodyTdElement");
            }
            List<WebElement> minuteEles = WebElementUtils.getVisibleChildren(minutesBodyTdEle.get(0));
            if(ValueUtils.isBlank(minuteEles) || minuteEles.size() != 60){
                throw new RuntimeException("日历分钟数不对:" + dateTime.getMonth());
            }
            WebElementUtils.click(this.browser, minuteEles.get(dateTime.getMinute()));
        }
    }

    void selectedNow(WebElement yearMonthEle){
        List<WebElement> yearMonthTableEle = WebElementUtils.getChildrenByTag(yearMonthEle, "table");
        if(ValueUtils.isBlank(yearMonthTableEle)){
            throw new RuntimeException("未找到日历Table");
        }
        List<WebElement> yearMonthTableTFootEle = WebElementUtils.getChildrenByTag(yearMonthTableEle.get(0), "tfoot");
        if(ValueUtils.isBlank(yearMonthTableTFootEle)){
            throw new RuntimeException("未找到日历TableFoot");
        }
        WebElementUtils.click(this.browser, yearMonthTableTFootEle.get(0));
    }

    WebElement openAddBox(){
        try{
            Thread.sleep(1000);
        }catch (Exception e){}

        String errorMsg = null;
        int i = 300;
        while (i-- > 0){
            if(this.browser.isClosed()){
                throw new BusinessException("浏览器已关闭");
            }
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
}
