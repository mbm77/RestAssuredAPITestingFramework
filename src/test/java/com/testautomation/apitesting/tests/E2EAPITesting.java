package com.testautomation.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.listener.RestAssuredListener;
import com.testautomation.apitesting.utils.BaseTest;
import com.testautomation.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public class E2EAPITesting extends BaseTest{
	
	private static final Logger logger = LogManager.getLogger(E2EAPITesting.class);	
	@Test
	public void postAPIRequest() { 
		logger.info("e2eAPITesting execution started.......");
		try {
			String postAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.POST_API_REQUEST_BODY),"UTF-8");
			String tokenAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.TOKEN_API_REQUEST_BODY),"UTF-8");
			String putAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PUT_API_REQUEST_BODY),"UTF-8");
			String patchAPIRequestBody = FileUtils.readFileToString(new File(FileNameConstants.PATCH_API_REQUEST_BODY),"UTF-8");
			
			//post request
			Response response = RestAssured
				.given()
				.filter(new RestAssuredListener())
					.baseUri("https://restful-booker.herokuapp.com/booking")
					.contentType(ContentType.JSON)
					.body(postAPIRequestBody)
				.when()
					.post()
				.then()
					.statusCode(201)
				.extract()
					.response();
			
			String firstName = JsonPath.read(response.getBody().asString(),"$.booking.firstname");
			Assert.assertEquals(firstName, "api testing");
			
			String lastName = JsonPath.read(response.getBody().asString(),"$.booking.lastname");
			Assert.assertEquals(lastName,"tutorials");
			
			int totalPrice = JsonPath.read(response.getBody().asString(), "$.booking.totalprice");
			Assert.assertEquals(totalPrice, 1000);
			
			String checkIn = JsonPath.read(response.getBody().asString(),"$.booking.bookingdates.checkin");
			Assert.assertEquals(checkIn, "2018-01-01");
			
			String checkOut = JsonPath.read(response.getBody().asString(),"$.booking.bookingdates.checkout");
			Assert.assertEquals(checkOut, "2019-01-01");
			
			int booking_id = JsonPath.read(response.getBody().asString(),"$.bookingid");
			
			//base64 encoding class-name parallel="tests" 
			//thread-count="4" dependesOnMethods={} alwaysRun=true ignoreMissingDependencies=true 
			//get api call
			RestAssured
				.given()
					.baseUri("https://restful-booker.herokuapp.com")
					.pathParam("b_id", booking_id)
					.contentType(ContentType.JSON)
				.when()
					.get("/booking/{b_id}")
				.then()
					.assertThat()
					.statusCode(200);
			
			//token generation
			
			Response tokenResponse = RestAssured
				.given()
					.baseUri("https://restful-booker.herokuapp.com/auth")
					.contentType(ContentType.JSON)
					.body(tokenAPIRequestBody)
				.when()
					.post()
				.then()
					.assertThat()
					.statusCode(200)
				.extract()
					.response();
			String token = JsonPath.read(tokenResponse.getBody().asString(),"$.token");
			
			//put request
			Response putResponse = RestAssured
				.given()
					.baseUri("https://restful-booker.herokuapp.com")
					.pathParam("b_id", booking_id)
					.header("Cookie", "token="+token)
					.contentType(ContentType.JSON)
					.body(putAPIRequestBody)
				.when()
					.put("/booking/{b_id}")
				.then()
					.assertThat()
					.statusCode(200)
					.body("firstname", Matchers.equalTo("Specflow"))
					.body("lastname", Matchers.equalTo("Selenium C#"))
				.extract()
					.response();
			
			//patch request
			RestAssured
				.given()
					.baseUri("https://restful-booker.herokuapp.com")
					.pathParam("b_id", booking_id)
					.header("Cookie","token="+token)
					.contentType(ContentType.JSON)
					.body(patchAPIRequestBody)
				.when()
					.patch("/booking/{b_id}")
				.then()
					.assertThat()
					.statusCode(200)
					.body("firstname", Matchers.equalTo("Testers Talk"));
			
			RestAssured
				.given()
					.contentType(ContentType.JSON)
					.header("Cookie","token="+token)
					.baseUri("https://restful-booker.herokuapp.com")
					.pathParam("b_id",booking_id)
				.when()
					.delete("/booking/{b_id}")
				.then()
					.assertThat()
					.statusCode(200);
					
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		logger.info("e2eApiTesting execution ended..............");
		
	}

}
