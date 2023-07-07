package com.testautomation.apitesting.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;
import com.testautomation.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class DataDrivenTestingUsingExcel {
	
	@Test(dataProvider="excelFile")
	public void dataDrivenTestingUsingExcel(Map<String,String> testData) {
		BookingDates bookingDates = new BookingDates();
		bookingDates.setCheckin("2023-07-07");
		bookingDates.setCheckout("2023-07-15");
		
		Booking booking = new Booking();
		booking.setFirstname(testData.get("FirstName"));
		booking.setLastname(testData.get("LastName"));
		booking.setTotalprice(Integer.parseInt(testData.get("TotalPrice")));
		booking.setAdditionalneeds("HP Laptop");
		booking.setDepositpaid(true);
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
	
	@DataProvider(name="excelFile")
	public Object[][] dataDrivenTest() {
		
		String query = "select * from sheet1 where Run='Yes'";
		
		Object[][] objArray = null;
		Map<String,String> map = null;
		List<Map<String,String>> list = null;
		
		Fillo fillo = new Fillo();
		Connection connection;
		Recordset recordset;
		try {
			connection = fillo.getConnection(FileNameConstants.EXCEL_TEST_DATA);
			recordset = connection.executeQuery(query);
			list = new ArrayList<Map<String,String>>();
			while(recordset.next()) {
				map = new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);
				for(String field:recordset.getFieldNames()) {
					map.put(field, recordset.getField(field));
				}
				list.add(map);
			}
			objArray = new Object[list.size()][1];
			for(int i=0;i<list.size();i++) {
				objArray[i][0] = list.get(i);
			}
		} catch (FilloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return objArray;	
	}

}
