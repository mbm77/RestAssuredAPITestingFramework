package com.testautomation.apitesting.tests;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import com.testautomation.apitesting.utils.BaseTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONObject;

public class PostAPIRequest extends BaseTest{
	
	@Test
	public void createBooking() {

		JSONObject booking = new JSONObject();
		JSONObject bookingDates = new JSONObject();
		
		booking.put("firstname", "Bala");
		booking.put("lastname", "Mannepalli");
		booking.put("totalprice", 1000);
		booking.put("depositpaid", true);
		booking.put("additionalneeds", "super bowls");
		
		bookingDates.put("checkin","2023-07-04");
		bookingDates.put("checkout", "2024-05-09");
		
		booking.put("bookingdates", bookingDates);
		
		
		
		Response response = RestAssured 
				.given()
					.baseUri("https://restful-booker.herokuapp.com/booking")
					.header("Content-Type","application/json")
					.body(booking.toString())
					//.log().all()
				.when()
					.post()
				.then()
					.assertThat()
					.log().all()
					.statusCode(200)
					.body("booking.firstname", Matchers.equalTo("Bala"))
					.body("booking.bookingdates.checkout", Matchers.equalTo("2024-05-09"))
				.extract()
					.response();
				
		int booking_id = response.path("bookingid");
		
				RestAssured
				.given()
					.contentType(ContentType.JSON)
					.pathParam("b_id", booking_id)
					.baseUri("https://restful-booker.herokuapp.com/booking")
					
				.when()
					.get("/{b_id}")
				.then()
					.assertThat()
					.statusCode(200)
					.contentType(ContentType.JSON)
					.body("firstname", Matchers.equalTo("Bala"))
					.body("lastname", Matchers.equalTo("Mannepalli"))
					.log().all();
		
	}
	
	
}
