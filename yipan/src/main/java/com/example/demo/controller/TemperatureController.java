package com.example.demo.controller;

import com.example.demo.service.RateLimiter;
import com.example.demo.service.TemperatureService;
import com.example.demo.util.Msg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Optional;

@Controller
public class TemperatureController {
    private static final Logger logger = LoggerFactory.getLogger(TemperatureController.class);

    @Autowired
    TemperatureService temperatureService;

    @RateLimiter(value = 100)
    public Optional<Integer> getTemperature(String province, String city, String county) throws Exception {
        Optional<Integer> temp = temperatureService.getTemperatureWithException(province, city, county);
        return temp;
    }
}
