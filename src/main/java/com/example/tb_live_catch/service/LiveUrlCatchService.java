package com.example.tb_live_catch.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.tb_live_catch.util.BeetlUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class LiveUrlCatchService {

    public void liveCatch(String uri) throws IOException {

        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.connect();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            return;
        }

        BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("C:\\Users\\Zzz\\Desktop\\test.flv"));
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = bis.read(bytes)) != -1) {
            bos.write(bytes, 0, len);
        }
        bos.flush();
        bos.close();
        bis.close();

    }

    public String getLiveUri(String liveId) throws IOException {
        CookieStore cookieStore = new BasicCookieStore();
        HttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", "0");
        map.put("sign", "0");
        map.put("liveId", liveId);
        String url1 = BeetlUtil.render(GET_LIVE_URL, map);
        HttpGet get = new HttpGet(url1);
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0");
        client.execute(get);
        List<Cookie> cookies = cookieStore.getCookies();

        Optional<String> token = cookies.stream().filter(o -> "_m_h5_tk".equals(o.getName())).findAny().map(Cookie::getValue);
        String sign = "";
        Long timestamp = 0L;
        if (token.isPresent()) {
            String tokenStr = token.get();
            tokenStr = tokenStr.substring(0, tokenStr.indexOf("_"));
            timestamp = (new Date()).getTime();
            sign = DigestUtils.md5Hex(tokenStr + "&" + timestamp + "&" + APP_KEY + "&{\"liveId\":\""+liveId+"\"}");
        }
        map.put("timestamp", timestamp);
        map.put("sign", sign);
        String url2 = BeetlUtil.render(GET_LIVE_URL, map);
        HttpGet get1 = new HttpGet(url2);
        get1.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0");
        HttpResponse response = client.execute(get1);
        HttpEntity httpEntity = response.getEntity();
        String responseStr = EntityUtils.toString(httpEntity);

        String jsonStr = responseStr.substring("mtopjsonp4(".length() + 1, responseStr.length()-1);
        JSONObject responseJson = JSONObject.parseObject(jsonStr);
        System.out.println(responseJson.toJSONString());

        if ("SUCCESS::调用成功".equals(responseJson.getJSONArray("ret").get(0))) {
            JSONObject data = responseJson.getJSONObject("data");
            JSONArray urlList = data.getJSONArray("liveUrlList");
            for (int i = 0; i < urlList.size(); i++) {
                JSONObject item = urlList.getJSONObject(i);
                if ("0".equals(item.getString("codeLevel"))) {
                    return item.toJSONString();
                }
            }
        }
    return "";
    }

    private static final String APP_KEY = "12574478";

    private static String GET_LIVE_URL =
            "http://h5api.m.taobao.com/h5/mtop.mediaplatform.live.livedetail/4.0/" +
            "?jsv=2.4.0" +
            "&appKey=12574478" +
            "&t=${timestamp}" +
            "&sign=${sign}" +
            "&AntiCreep=true" +
            "&api=mtop.mediaplatform.live.livedetail" +
            "&v=4.0" +
            "&type=jsonp" +
            "&dataType=jsonp" +
            "&timeout=20000" +
            "&callback=mtopjsonp4" +
            "&data=%7b%22liveId%22%3a%22${liveId}%22%7d";

    public static void main(String[] args) {
        String sign = DigestUtils.md5Hex("00b8bde2b912d462da6c6913e9652a38&1577359735090" + "&" + APP_KEY + "&{\"liveId\":\"248941792164\"}");
        System.out.println(sign);
    }

}
