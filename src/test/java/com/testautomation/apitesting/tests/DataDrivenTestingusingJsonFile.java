package com.testautomation.apitesting.tests;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;
import com.testautomation.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.minidev.json.JSONArray;

public class DataDrivenTestingusingJsonFile {
	
	@Test(dataProvider="jsonGenerator")
	public void dataDrivenTestingUsingJsonFile(LinkedHashMap<String,String> testData) {
		BookingDates bookingDates = new BookingDates();
		bookingDates.setCheckin("2023-07-06");
		bookingDates.setCheckout("2023-07-15");
		
		Booking booking = new Booking();
		booking.setFirstname("Bala");
		booking.setLastname("Mannepalli");
		booking.setTotalprice(2000);
		booking.setDepositpaid(true);
		booking.setAdditionalneeds("Camera");
		booking.setBookingdates(bookingDates);
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);
			RestAssured
				.given()
					.baseUri("https://restful-booker.herokuapp.com/booking")
					.contentType(ContentType.JSON)
					.body(jsonData)
				.when()
					.post()
				.then()
					.assertThat()
					.statusCode(200)
				.extract()
					.response();
					
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	@DataProvider(name="jsonGenerator")
	public Object[] jsonDataProvider() {
		Object[] obj = null;
		try {
			String jsonTestData = FileUtils.readFileToString(new File(FileNameConstants.JSON_TEST_DATA),"UTF-8");
			JSONArray objArray = JsonPath.read(jsonTestData,"$");
			int size = objArray.size();
			obj = new Object[size];
			
			for(int i=0;i<size;i++) {
				obj[i] = objArray.get(i);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return obj;
		
	}
}
