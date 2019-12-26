package com.example.tb_live_catch.service;

import com.example.tb_live_catch.util.BeetlUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.beetl.core.BeetlKit;
import org.beetl.core.parser.BeetlLexer;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.CookieManager;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TBWordAnalysisService {

    private static final String PLAY_BACK_REGEX = "^videoUrl=.*\\.mp4$";

    public String analysis(String tbWork) {

        return "a original url";
    }

    public String playBackUrl(String originalUrl) {

        Pattern pattern = Pattern.compile(PLAY_BACK_REGEX);

        Matcher matcher = pattern.matcher(originalUrl);

        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    public String tbSign() throws IOException {
        HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

        CookieStore cookieStore = new BasicCookieStore();
        HttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).setRoutePlanner(routePlanner).build();
        Map<String, Object> map = new HashMap<>();
        map.put("timestamp", "0");
        map.put("sign", "0");
        map.put("liveId", "249188383631");
        String url1 = BeetlUtil.render(URL, map);
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
            sign = DigestUtils.md5Hex(tokenStr + "&" + timestamp + "&" + APP_KEY + "&{\"liveId\":\"249188383631\"}");
        }
        map.put("timestamp", timestamp);
        map.put("sign", sign);
        String url2 = BeetlUtil.render(URL, map);
        HttpGet get1 = new HttpGet(url2);
        get1.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0");
        HttpResponse response = client.execute(get1);
        cookies = cookieStore.getCookies();
        System.out.println(cookies);
        HttpEntity httpEntity = response.getEntity();
        String responseStr = EntityUtils.toString(httpEntity);
        System.out.println(responseStr);
        
        return "";
    }

    private static final String APP_KEY = "12574478";

    private static String URL =
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
            "&data=%7B%22liveId%22%3A%22249188383631%22%7D";

    public static void main(String[] args) {
        String sign = DigestUtils.md5Hex("00b8bde2b912d462da6c6913e9652a38&1577359735090" + "&" + APP_KEY + "&{\"liveId\":\"249188383631\"}");
//        String sign = DigestUtils.md5Hex("1577340458410");

        System.out.println(sign);

    }



}
