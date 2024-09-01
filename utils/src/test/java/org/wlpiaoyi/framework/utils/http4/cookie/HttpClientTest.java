package org.wlpiaoyi.framework.utils.http.cookie;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.BufferedHttpEntity;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.http.HttpClient;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class HttpClientTest {

    private String think_language = "zh-cn";
    private String PHPSESSID = "u3l75up4pdr836rdpml3bre7c4";
    private String uuid = "B3AFBA99-1DD9-4C15-ACBC-2989B744E000";
    private String token = "lDZq1/4ge6fGACF2AgX3n0j+SiZ7R6Jj09j85wC2YpEH5Vcpy0vuAef1a0QzHgUeilWr8GE0eoCZhauMNSjjD7dUX8k99QXzhgPGKJ6akXKs2Lq8YnVFRuQELaI8mj6/hVftmsOm6EZqFUz93j9IWr6z8jpKVzNcj8fHrOY65hD9stwhl5CzKEvRNIcSqq7d";

    @Before
    public void setUp() throws Exception {}

    @Test
    public void test() throws IOException, InterruptedException {
//        Response response = this.emartet();
//        System.out.println();
//        String url = "http://www.100csc.com/shop/goods/1604545423286/100000352.html";
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
                                .setHeader("Cookie","_qrcode_digest=25c82b9baf0886a02f07a9b6f5b8b634351d5af0f9bc120e1d521cf46b874ed1; _122_gt=WGT-173947-plGwGkucjnhxfh33o12lkHZdFAS6sSGwouC; _122_gt_tag=1; JSESSIONID-L=fae904c1-5ca0-4b93-8d3f-c4959d5bbe20; JSESSIONID=00A7335EB29F807B4FC5259095496A7F; accessToken=6DcN65VqJor33DVso1QwXdb713eb7VIEqfkt8ir54JVXPtsubliHUOVwnkCyt2KAF1Rfq24+lm+lA3XMzLMu7MubTr2xIiJyfzlKK0DamMKkrStOSL844QFw4RWKaZK3lQ3etE7FdlwXh3C5oI/xB+0vf2OCrJSNdArM7gdTl4/eFsay7Z9vTs+douwj+qUH; c_yhlx_=2; tmri_csfr_token=D9F5B9AB1B43265C9ED8DC32BF7A446E")
                                .setMethod(Request.Method.Post)
                                .setParam("startDate","20200105")
                                .setParam("endDate","20240709")
                                .setParam("hpzl","52")
                                .setParam("hphm","川ADD3933")
                                .setParam("page","1")
                                .setParam("type","0")
                )
                .setRpClazz(Map.class)
                .response();
        Gson gson = GsonBuilder.gsonDefault();
        if(response.getStatusCode() != 200){
            throw new BusinessException("列表请求错误：" + gson.toJson(response.getBody()));
        }
        Integer totalPages = MapUtils.getValueByKeyPath(response.getBody(),"data.totalPages", -1, Integer.class);
        List<Map> content = MapUtils.getValueByKeyPath(response.getBody(),"data.content", null, List.class);
        System.out.println("<======" + response.getStatusCode());

    }



    public Response<String> emartet() {
        String url = "https://api.smsbao.com/sms?u=yes_boy&p=9d7fb24aa17944029d87d506b044c9c2&m=" +
                18228088049L
                + "&c=" + "【短信宝】您的验证码是3572,十分钟内有效";

        Response<String> response =  HttpClient.instance(
                Request.initJson(url).setMethod(Request.Method.Get)
        )
                .setRpClazz(String.class)
                .response();

        return response;
    }


    public Response<String> index(String classId) throws IOException {

        Response<String> response =  HttpClient.instance(
                Request.initJson("https://appapi.qukoudai.cn/index.php")
                        .setCookie("PHPSESSID", this.PHPSESSID)
                        .setCookie("think_language", this.think_language)
                        .setHeader("x-version-release", "1.5.1")
                        .setHeader("user-agent","1.5.1 rv:51 (iPhone; iOS 13.7; zh_CN)")
                        .setParam("mo", "v2")
                        .setParam("a", "getPurchaseList" )
                        .setParam("uuid", this.uuid)
                        .setParam("class_id", classId)
                        .setParam("pageSiz", "20")
                        .setParam("page", "1")
                        .setParam("token", this.token)
                        .setProxy("127.0.0.1", 8888)
        )
                .setRpClazz(String.class)
                .response();

        return response;
    }

    @After
    public void tearDown() throws Exception {

    }
}
