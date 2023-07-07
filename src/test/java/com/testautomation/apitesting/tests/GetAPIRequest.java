package com.testautomation.apitesting.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class GetAPIRequest {
	
	@Test
	public void getAllBookings() {
		
		Response response = RestAssured
				.given()
					.baseUri("https://restful-booker.herokuapp.com/booking")
					.contentType(ContentType.JSON)
				.when()
					.get()
				.then()
					.log().all()
					.assertThat()
					.statusCode(200)
					.statusLine("HTTP/1.1 200 OK")
					.header("Content-Type","application/json; charset=utf-8")
				.extract()
					.response();
		boolean isTrue = response.getBody().asString().contains("bookingid");
		Assert.assertTrue(isTrue);
	
	}

}
