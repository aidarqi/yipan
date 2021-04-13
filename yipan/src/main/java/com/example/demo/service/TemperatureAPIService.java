package com.example.demo.service;

import com.example.demo.util.JsonUtil;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
public class TemperatureAPIService {

    public Map<String, String> getProvinceCode() throws IOException  {
        String url = "http://www.weather.com.cn/data/city3jdata/china.html";
        return doGet(url);
    }

    public Map<String, String> getCityCode(String provinceCode) throws IOException {
        String url = String.format("http://www.weather.com.cn/data/city3jdata/provshi/%s.html", provinceCode);
        return doGet(url);
    }

    public Map<String, String> getCountyCode(String cityCode) throws IOException {
        String url = String.format("http://www.weather.com.cn/data/city3jdata/station/%s.html", cityCode);
        return doGet(url);
    }

    public Optional<Integer> getCountyTemperature(String countyCode) throws IOException {
        String url = String.format("http://www.weather.com.cn/data/sk/%s.html", countyCode);
        Map<String, String> map = doGet(url);
        String weatherInfo= String.valueOf(map.get("weatherinfo"));
        String tmp = new JsonUtil().toMap(weatherInfo).get("temp");
        return Optional.ofNullable((int)Float.parseFloat(tmp));
    }


    @Retryable(value = { IOException.class }, maxAttemptsExpression = "${retry.maxAttempts:10}",
            backoff = @Backoff(delayExpression = "${retry.backoff:1000}"))
    private Map<String, String> doGet(String url) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                return new JsonUtil().toMap(content);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw e;
        }
        return null;
    }
}
