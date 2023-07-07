package com.testautomation.apitesting.tests;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.testautomation.apitesting.listener.RestAssuredListener;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;
import com.testautomation.apitesting.utils.FileNameConstants;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class DataDrivenTestingUsingCSVFile {
	
	@Test(dataProvider="csvFile")
	public void dataDrivenTestingUsingCSVFile(Map<String,String> testData) {
		BookingDates bookingDates = new BookingDates();
		bookingDates.setCheckin("2023-07-07");
		bookingDates.setCheckout("2023-07-16");
		
		Booking booking = new Booking();
		booking.setFirstname(testData.get("firstname"));
		booking.setLastname(testData.get("lastname"));
		booking.setTotalprice(Integer.parseInt(testData.get("totalprice")));
		booking.setDepositpaid(true);
		booking.setAdditionalneeds("HP Laptop");
		booking.setBookingdates(bookingDates);
		
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);
			RestAssured
				.given()
					.filter(new RestAssuredListener())
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
	
	@DataProvider(name="csvFile")
	public Object[][] getDataFromCSV() {
		Object[][] objArray = null;
		Map<String,String> map = null;
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		try {
			CSVReader csvReader = new CSVReader(new FileReader(FileNameConstants.CSV_TEST_DATA));
			
			String[] line;
				int count = 0;
				while((line = csvReader.readNext())!=null) {
					if(count == 0) {
						count++;
						continue;
					}
					
					map = new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER);
					map.put("firstname", line[0]);
					map.put("lastname", line[1]);
					map.put("totalprice", line[2]);
					
					list.add(map);
				}
				
				objArray = new Object[list.size()][1];
				
				for(int i=0;i<list.size();i++) {
					objArray[i][0] = list.get(i);
				}
				
		}catch (CsvValidationException e) {
				e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return objArray;
	}

}
