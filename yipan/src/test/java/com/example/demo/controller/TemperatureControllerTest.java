package com.example.demo.controller;

import com.example.demo.util.TemperatureException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TemperatureControllerTest {

	@Autowired
	TemperatureController temperatureController;

	@Test
	void testTemperatureController() throws IOException {
		Optional<Integer> temperatureNormal = null;
		try {
			temperatureNormal = temperatureController.getTemperature("江苏", "苏州", "苏州");
		} catch (Exception e) {
			e.printStackTrace();
		}
		assert temperatureNormal.get() == 23;


		Exception exception = assertThrows(TemperatureException.class, () -> {
			temperatureController.getTemperature("江南", "苏州", "苏州");
		});
		String exceptMsgContent = "province does not exist";
		String actualMsgContent = ((TemperatureException) exception).msgContent;

		assertTrue(exceptMsgContent.equals(actualMsgContent));
	}

}
