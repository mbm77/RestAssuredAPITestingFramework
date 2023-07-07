package com.testautomation.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;
import com.testautomation.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;

public class PostAPIRequestUsingPojos {
	private static final Logger logger = LogManager.getLogger(PostAPIRequestUsingPojos.class);
	@Test
	public void PostRequestUsingPojo() {
		logger.info("json schema validation started ...............");
		try {
			String jsonSchema = FileUtils.readFileToString(new File(FileNameConstants.EXPECTED_JSON_SCHEMA), "UTF-8");
		
		BookingDates bookingDates = new BookingDates();
		bookingDates.setCheckin("2023-07-06");
		bookingDates.setCheckout("2023-07-14");
		
		Booking booking = new Booking();
		booking.setFirstname("Bala");
		booking.setLastname("Mannepalli");
		booking.setTotalprice(1000);
		booking.setDepositpaid(true);
		booking.setAdditionalneeds("HP Laptop");
		booking.setBookingdates(bookingDates);
		
		ObjectMapper objectMapper = new ObjectMapper();
			String jsonBookingData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);
			System.out.println(jsonBookingData);
			
			Response response = RestAssured
				.given()
					.baseUri("https://restful-booker.herokuapp.com/booking")
					.contentType(ContentType.JSON)
					.body(jsonBookingData)
				.when()
					.post()
				.then()
					.assertThat()
					.statusCode(200)
				.extract()
					.response();
			int booking_id = response.path("bookingid");
			
			RestAssured
				.given()
					.baseUri("https://restful-booker.herokuapp.com")
					.pathParam("b_id", booking_id)
					.contentType(ContentType.JSON)
				.when()
					.get("/booking/{b_id}")
				.then()
					.assertThat()
					.statusCode(200)
					.body(JsonSchemaValidator.matchesJsonSchema(jsonSchema));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		logger.info("json schema validation ended...!");
		
	}

}
