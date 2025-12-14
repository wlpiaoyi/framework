package org.wlpiaoyi.framework.lab.selenium.utils;

import org.openqa.selenium.*;
import org.wlpiaoyi.framework.lab.selenium.Browser;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>    元素操控</p>
 * <p><b>{@code @date:}</b>           2024/7/14 10:24</p>
 * <p><b>{@code @version:}</b>        1.0</p>
 */

public class WebElementUtils {


    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; border: 1px solid #e74c3c; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 获取可见的子元素
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>parentElement</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/8 15:15</p>
     * <p><b>{@code @return:}</b>{@link List<WebElement>}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static List<WebElement> getVisibleChildren(WebElement parentElement) {
        List<WebElement> allChildren = parentElement.findElements(By.xpath("./*"));
        return allChildren.stream()
                .filter(WebElement::isDisplayed)
                .collect(Collectors.toList());
    }

    /**
     * <p><b>{@code @description:}</b>
     * <div style='border-radius: 12px; border: 1px solid #e74c3c; padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 获取可点击的子元素
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>parentElement</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/8 15:16</p>
     * <p><b>{@code @return:}</b>{@link List< WebElement>}</p>
     * <p><b>{@code @author:}</b>wlpia</p>
     */
    public static List<WebElement> getClickableChildren(WebElement parentElement) {
        List<WebElement> allChildren = parentElement.findElements(By.xpath("./*"));
        return allChildren.stream()
                .filter(WebElement::isDisplayed)
                .filter(WebElement::isEnabled)
                .collect(Collectors.toList());
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取所有直接子元素
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>parentElement</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/7 16:40</p>
     * <p><b>{@code @return:}</b>{@link List<WebElement>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static List<WebElement> getDirectChildren(WebElement parentElement) {
        return parentElement.findElements(By.xpath("./*"));
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取特定标签的直接子元素
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>parentElement</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>tagName</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/7 16:43</p>
     * <p><b>{@code @return:}</b>{@link List<WebElement>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static List<WebElement> getChildrenByTag(WebElement parentElement, String tagName) {
        return parentElement.findElements(By.xpath("./" + tagName));
    }
    /**
     * <p><b>{@code @description:}</b>
     * 获取具有特定class的子元素
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>parentElement</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>className</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/7 16:44</p>
     * <p><b>{@code @return:}</b>{@link List<WebElement>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static List<WebElement> getChildrenByClass(WebElement parentElement, String className) {
        return parentElement.findElements(By.cssSelector(":scope > ." + className));
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取具有特定属性的子元素
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>parentElement</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>attribute</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>value</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/7 16:44</p>
     * <p><b>{@code @return:}</b>{@link List<WebElement>}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static List<WebElement> getChildrenByAttribute(WebElement parentElement,
                                                          String attribute, String value) {
        String xpath = String.format("./*[@%s='%s']", attribute, value);
        return parentElement.findElements(By.xpath(xpath));
    }

    /**
     * <p><b>{@code @description:}</b>
     * 检查元素是否有父元素
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>childElement</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/7 16:39</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static boolean hasParent(WebElement childElement) {
        try {
            childElement.findElement(By.xpath("parent::*"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取上级节点
     * </p>
     *
     * <p><b>{@code @param:}</b> <b>ele</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/7 16:37</p>
     * <p><b>{@code @return:}</b>{@link WebElement}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static WebElement getParentSafely(WebElement childElement) {
        try {
            return childElement.findElement(By.xpath("parent::*"));
        } catch (NoSuchElementException e) {
            System.out.println("父元素不存在");
            return null;
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * 出发click事件
     * </p>
     *
     * <p><b>@param</b> <b>browser</b>
     * {@link Browser}
     * </p>
     *
     * <p><b>@param</b> <b>ele</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/7/14 10:23</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static void click(Browser browser, WebElement ele){
        try{
            ele.click();
        }catch (Exception e){
            ((JavascriptExecutor)browser.getDriver()).executeScript("arguments[0].click();", ele);
        }
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取元素Value
     * </p>
     *
     * <p><b>@param</b> <b>element</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/7/14 10:23</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String getValue(WebElement element){
        String value = element.getText();
        if(ValueUtils.isBlank(value)) value = element.getAttribute("value");
        return value;
    }


    /**
     * <p><b>{@code @description:}</b>
     * 给元素设置value
     * </p>
     *
     * <p><b>@param</b> <b>element</b>
     * {@link WebElement}
     * </p>
     *
     * <p><b>@param</b> <b>value</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2024/7/14 10:22</p>
     * <p><b>{@code @return:}</b>{@link boolean}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static boolean setValue(WebElement element, String value) {

        int length = 0;
        if(element.getAttribute("value") != null){
            length = element.getAttribute("value").length();
        }

        if(length > 0){
            element.clear();
            for (int i = 0; i < 20 ; i++) {
                try { Thread.sleep(100);} catch (InterruptedException e) {}
                if(element.getAttribute("value") == null || element.getAttribute("value").length() == 0)
                    break;
            }

            if(element.getAttribute("value") != null){
                length = element.getAttribute("value").length();
            }

            for (int i = length; i > 0  ; i--) {
                element.sendKeys(Keys.BACK_SPACE);
                try { Thread.sleep(50);} catch (InterruptedException e) {}
            }
            length = element.getAttribute("value").length();
        }

        element.sendKeys(value);

        return length == 0;
    }
    public interface Runnable {
        /**
         * Runs this operation.
         */
        boolean run();
    }


    /**
     * <p><b>{@code @description:}</b>
     * <div style=' padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
     * 循环执行
     * </div>
     * </p>
     *
     * <p><b>{@code @param}</b> <b>runnable</b>
     * {@link Runnable}
     * </p>
     *
     * <p><b>{@code @param}</b> <b>times</b>
     * {@link int}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/11/29 12:43</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     * <hr/>
     */
    public static void whileDo(Runnable runnable, int times) {
        Random random = new Random();
        int i = times;
        while (i-- > 0){
            try {
                int sleep = random.nextInt(400) + 100;
                Thread.sleep(sleep);
                if(runnable.run()) break;
                sleep = random.nextInt(200) + 500;
                Thread.sleep(sleep);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
