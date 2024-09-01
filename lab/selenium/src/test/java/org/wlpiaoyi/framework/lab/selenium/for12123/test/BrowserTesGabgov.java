package org.wlpiaoyi.framework.lab.selenium.for12123.test;

import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;

import java.util.*;

public class BrowserTesGabgov {



    @Before
    public void setUp() throws Exception {
    }


    @SneakyThrows
    @Test
    public void test() {
        BrowserCabgov bc = new BrowserCabgov();
        bc.start();
    }


    public WebElement getNearBy(WebElement ce, Integer[] ui){
        int maxV = Integer.MAX_VALUE;
        List<WebElement> elements = ((RemoteWebElement) ce).findElementsByXPath("./*");
        if(elements == null){
            return null;
        }
        WebElement element = null;
        for (WebElement e : elements){
            WebElement targetE = getNearBy(e, ui);
            if(targetE == null){
                targetE = e;
            }
            int iuu = Math.abs(targetE.getRect().getX() - ui[0]) + Math.abs(targetE.getRect().getY() - ui[1]);
            if(iuu >= maxV){
                continue;
            }
            element = targetE;
            maxV = iuu;
        }
        return element;
    }

    @After
    public void tearDown() throws Exception {

    }
}
