package com.example.demo.util;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

    public Map<String, String> toMap(String json) {
        Map<String, String> map = new HashMap<>();
        try {
            map = (Map<String, String>) JSON.parse(json);
        } catch (Exception e) {
            logger.error("JSON convert map error", e);
        }
        return map;
    }
}
