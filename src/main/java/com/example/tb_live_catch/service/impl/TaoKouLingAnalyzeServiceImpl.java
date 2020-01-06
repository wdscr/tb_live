package com.example.tb_live_catch.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.tb_live_catch.service.LiveUrlCatchService;
import com.example.tb_live_catch.service.TaoKouLingAnalyzeService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TaoKouLingAnalyzeServiceImpl implements TaoKouLingAnalyzeService {


    @Value("${taokouling.appkey}")
    private String appkey;

    @Value("${taokouling.api}")
    private String api;

    @Autowired
    LiveUrlCatchService liveUrlCatchService;

    @Override
    public String getSourceUrl(String tkl) {

        try {
            CloseableHttpClient client = HttpClients.createDefault();

            HttpPost post = new HttpPost(api);

            JSONObject param = new JSONObject();
            param.put("apikey", appkey);
            param.put("tkl", tkl);
            String paramStr = param.toJSONString();
            StringEntity postingString = new StringEntity(paramStr, "utf-8");

            post.setEntity(postingString);

            HttpResponse response = client.execute(post);
            HttpEntity responseEntity = response.getEntity();
            String responseJsonStr = EntityUtils.toString(responseEntity);

            JSONObject responseJson = JSONObject.parseObject(responseJsonStr);
            if (responseJson.getInteger("code") != null && responseJson.getInteger("code") == 1) {
                String url = responseJson.getString("url");
                if (url.indexOf("live.html") > 0) {
                    int sidx = url.indexOf("id=") + 3;
                    int eidx = sidx;
                    while (eidx < url.length()) {
                        if (url.charAt(eidx) == '&') {
                            break;
                        }
                        eidx ++;
                    }
                    String id = url.substring(sidx, eidx);
                    return liveUrlCatchService.getLiveUri(id);
                } else {
                    int sidx = url.indexOf("videoUrl=") + 9;
                    int eidx = sidx;
                    while (eidx < url.length()) {
                        if (url.charAt(eidx) == '&') {
                            break;
                        }
                        eidx ++;
                    }
                    return  url.substring(sidx, eidx);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
