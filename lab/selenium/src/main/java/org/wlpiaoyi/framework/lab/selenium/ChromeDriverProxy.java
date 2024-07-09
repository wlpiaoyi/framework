//package org.wlpiaoyi.framework.lab.selenium;
//import com.google.gson.Gson;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClientBuilder;
//import org.apache.http.util.EntityUtils;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeDriverService;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.logging.LogEntries;
//import org.openqa.selenium.logging.LogEntry;
//import org.openqa.selenium.logging.LogType;
//import org.openqa.selenium.logging.Logs;
//import org.wlpiaoyi.framework.utils.gson.GsonBuilder;
//
//import java.io.IOException;
//import java.util.*;
//
//
//public class ChromeDriverProxy extends ChromeDriver {
//
//    /**
//     * method --> Network.responseReceived
//     */
//    public static final String NETWORK_RESPONSE_RECEIVED = "Network.responseReceived";
//
//    /**
//     * post 请求超时时间
//     */
//    private static final int COMMAND_TIMEOUT = 5000;
//
//    /**
//     * 必须固定端口，因为ChromeDriver没有实时获取端口的接口；
//     */
//    private static final int CHROME_DRIVER_PORT = 9102;
//
//    private static ChromeDriverService driverService = new ChromeDriverService.Builder().usingPort(CHROME_DRIVER_PORT).build();
//
//    public ChromeDriverProxy(ChromeOptions options) {
//        super(driverService, options);
//    }
//
//
//    // 根据请求ID获取返回内容
//    public String getResponseBody(String requestId) {
//        try {
//            // CHROME_DRIVER_PORT chromeDriver提供的端口
//            String url = String.format("http://localhost:%s/session/%s/goog/cdp/execute",
//                    CHROME_DRIVER_PORT, getSessionId());
//            HttpPost httpPost = new HttpPost(url);
//            Map object = new HashMap();
//            Map params = new HashMap();
//            params.put("requestId", requestId);
//            object.put("cmd", "Network.getResponseBody");
//            object.put("params", params);
//            httpPost.setEntity(new StringEntity(GsonBuilder.gsonDefault().toJson(object)));
//
//            RequestConfig requestConfig = RequestConfig
//                    .custom()
//                    .setSocketTimeout(COMMAND_TIMEOUT)
//                    .setConnectTimeout(COMMAND_TIMEOUT).build();
//
//            CloseableHttpClient httpClient = HttpClientBuilder.create()
//                    .setDefaultRequestConfig(requestConfig).build();
//
//            HttpResponse response = httpClient.execute(httpPost);
//            String s = EntityUtils.toString(response.getEntity());
//            return  this.getResponseValue(s);
//        } catch (IOException e) {
//            //logger.error("getResponseBody failed!", e);
//        }
//        return null;
//    }
//
//
//
//    // 根据请求ID获取返回内容
//    // https://github.com/bayandin/chromedriver/blob/master/server/http_handler.cc
//    public Map getCookies(String requestId) {
//        try {
//            // CHROME_DRIVER_PORT chromeDriver提供的端口
//            String url = String.format("http://localhost:%s/session/%s/cookie",
//                    CHROME_DRIVER_PORT, getSessionId());
//            String cookieStr = Hclient.doGet(url, null);
//            return JSONArray.parseArray(getResponseValue(cookieStr));
//        } catch (Exception e) {
//            //logger.error("getResponseBody failed!", e);
//        }
//        return null;
//    }
//    /**
//     * 获取响应结果{"sessionId": "","status": 0,"value": ""}的value
//     *
//     * @param data
//     * @return
//     */
//    private String getResponseValue(String data){
//        JSONObject json = JSONObject.parseObject(data);
//        ResponseBodyVo responseBodyVo = JSONObject.toJavaObject(json,ResponseBodyVo.class);
//        if(0 == responseBodyVo.getStatus()){
//            return responseBodyVo.getValue();
//        }else{
//            System.out.println("status error:" + data);
//            return "";
//        }
//
//    }
//
//    public static void saveHttpTransferDataIfNecessary(ChromeDriverProxy driver) {
//        Logs logs = driver.manage().logs();
//        Set<String> availableLogTypes = logs.getAvailableLogTypes();
//
//        if(availableLogTypes.contains(LogType.PERFORMANCE)) {
//            LogEntries logEntries = logs.get(LogType.PERFORMANCE);
//            List<ResponseReceivedEvent> responseReceivedEvents = new ArrayList<>();
//
//            for(LogEntry entry : logEntries) {
//                JSONObject jsonObj = JSON.parseObject(entry.getMessage()).getJSONObject("message");
//                String method = jsonObj.getString("method");
//                String params = jsonObj.getString("params");
//
//                if (method.equals(NETWORK_RESPONSE_RECEIVED)) {
//                    ResponseReceivedEvent response = JSON.parseObject(params, ResponseReceivedEvent.class);
//                    responseReceivedEvents.add(response);
//                }
//            }
//            doSaveHttpTransferDataIfNecessary(driver, responseReceivedEvents);
//        }
//
//    }
//
//    // 保存网络请求
//    private static void doSaveHttpTransferDataIfNecessary(ChromeDriverProxy driver, List<ResponseReceivedEvent> responses) {
//        for(ResponseReceivedEvent responseReceivedEvent : responses) {
//            String url = JSONObject.parseObject(responseReceivedEvent.getResponse()).getString("url");
//            boolean staticFiles = url.endsWith(".png")
//                    || url.endsWith(".jpg")
//                    || url.endsWith(".css")
//                    || url.endsWith(".ico")
//                    || url.endsWith(".js")
//                    || url.endsWith(".gif");
//
//            if(!staticFiles && url.startsWith("http")) {
//                // 使用上面开发的接口获取返回数据
//                String  bodyx= driver.getResponseBody(responseReceivedEvent.getRequestId());
//
//                System.out.println("url:"+url+" ,body-->"+bodyx);
//                JSONArray cookies = driver.getCookies(responseReceivedEvent.getRequestId());
//                System.out.println("url:"+url+" ,cookies-->"+cookies);
//            }
//        }
//    }
//
//
//}
