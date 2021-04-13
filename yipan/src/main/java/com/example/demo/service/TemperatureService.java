package com.example.demo.service;

import com.example.demo.util.Msg;
import com.example.demo.util.TemperatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TemperatureService {
    private static final Logger logger = LoggerFactory.getLogger(TemperatureService.class);

    @Autowired
    TemperatureAPIService temperatureAPIService;

    private static Map<String, String> provinceCodeMapping = new HashMap<>();
    private static Map<String, String> cityCodeMapping = new HashMap<>();
    private static Map<String, String> countyCodeMapping = new HashMap<>();

    private String getProvinceCode(Parameter parameter) throws IOException {
        if (provinceCodeMapping.isEmpty()) {
            synchronized (provinceCodeMapping) {
                if (provinceCodeMapping.isEmpty()) {
                    try {
                        Map<String, String> map = temperatureAPIService.getProvinceCode();
                        for(Map.Entry<String, String> entry : map.entrySet()) {
                            provinceCodeMapping.put(entry.getValue(), entry.getKey());
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        throw new IOException("GET ProvinceCode API error");
                    }
                }
            }
        }
        return provinceCodeMapping.get(parameter.province);
    }

    private String getCityCode(Parameter parameter) throws IOException {
        String key = parameter.province + ";" + parameter.city;
        if (!cityCodeMapping.containsKey(key)) {
            synchronized (cityCodeMapping) {
                if (!cityCodeMapping.containsKey(key)) {
                    try {
                        Map<String, String> map = temperatureAPIService.getCityCode(parameter.provinceCode);
                        for(Map.Entry<String, String> entry : map.entrySet()) {
                            cityCodeMapping.put(parameter.province + ";" + entry.getValue(), parameter.provinceCode + entry.getKey());
                        }
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                        throw new IOException("GET ProvinceCode API error");
                    }
                }
            }
        }
        return cityCodeMapping.get(key);
    }

    private String getCountyCode(Parameter parameter) throws IOException {
        String key = parameter.province + ";" + parameter.city + ";" + parameter.county;
        if (!countyCodeMapping.containsKey(key)) {
            synchronized (countyCodeMapping) {
                if (!countyCodeMapping.containsKey(key)) {
                    try {
                        Map<String, String> map = temperatureAPIService.getCountyCode(parameter.cityCode);
                        for(Map.Entry<String, String> entry : map.entrySet()) {
                            countyCodeMapping.put(parameter.province + ";" + parameter.city + ";" + entry.getValue(), parameter.cityCode + entry.getKey());
                        }
                    } catch (IOException e) {
                        throw new IOException("GET ProvinceCode API error");
                    }
                }
            }
        }
        return countyCodeMapping.get(key);
    }

    public Optional<Integer> getTemperatureWithException(String province, String city, String county) throws Exception {
        Parameter parameter = new Parameter(province, city, county);
        String provinceCode = getProvinceCode(parameter);
        if (null != provinceCode) {
            parameter.provinceCode = provinceCode;
        } else {
            logger.error(Msg.NO_PROVINCE.getMsgContent());
            throw new TemperatureException(Msg.NO_PROVINCE);
        }
        String cityCode = getCityCode(parameter);
        if (null != cityCode) {
            parameter.cityCode = cityCode;
        } else {
            logger.error(Msg.NO_PROVINCE.getMsgContent());
            throw new TemperatureException(Msg.NO_CITY);
        }
        String countyCode = getCountyCode(parameter);
        if (null != countyCode) {
            parameter.countyCode = countyCode;
        } else {
            logger.error(Msg.NO_PROVINCE.getMsgContent());
            throw new TemperatureException(Msg.NO_COUNTY);
        }

        return temperatureAPIService.getCountyTemperature(countyCode);

//        Optional<String> provinceCode = temperatureAPIService.getProvinceCode().entrySet().stream().filter(x -> x.getValue() == province).map(x -> x.getKey()).findFirst().get();

    }

    private static class Parameter {
        public final String province;
        public final String city;
        public final String county;
        public String provinceCode;
        public String cityCode;
        public String countyCode;

        public Parameter(String province, String city, String county) {
            this.province = province;
            this.city = city;
            this.county = county;
        }
    }

}
