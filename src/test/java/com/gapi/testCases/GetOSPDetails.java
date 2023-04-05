package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;
import com.google.gson.JsonObject;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetOSPDetails extends BaseClass {
	
	String path = "/facility-access/osps/access-requests";
	static TestUtilities tc = new TestUtilities();
	
		@Test()
		public void getOSPDetailsInfo() throws InterruptedException {
			
	    	logger = extent.createTest("getOSPDetailsInfo");
			String apiURI = domain+ path;
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Token)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID) 	
					.header("User-Email", "gpuat22-uat3@yahoo.com")
	 				.get(apiURI);      		     	

			System.out.println("===>"+response.asString());
			
			logger.log(Status.INFO, response.statusLine());
			Assert.assertEquals(response.getStatusCode(), 200);
			Assert.assertTrue(response.statusLine().contains("OK"));
	 		
	 		log.info("Successfully extracted all get OSP Details Info ");
	 		logger.log(Status.INFO, "Successfully extracted get OSP Details Info" );
	 		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	 		
		
		}
	    
		  
				@Test
				public void getOSPDetailsInfoWithInvalidToken() throws InterruptedException {
					
			    	logger = extent.createTest("getOSPDetailsInfoWithInvalidToken");
					String apiURI = domain+ path;
					Response response = RestAssured.given().relaxedHTTPSValidation()
							.header("Authorization", "Bearer "+"abcde")
							.header("Content-Type", "application/json")
							.header("Master-Account-Id", masterAccountID)
							.header("Account-Id", accountID) 	
							.header("User-Email", "gpuat22-uat3@yahoo.com")
			 				.get(apiURI);      		     	

					System.out.println("===>"+response.asString());
					
					logger.log(Status.INFO, response.statusLine());	
					Assert.assertEquals(response.getStatusCode(), 401);
					Assert.assertTrue(response.statusLine().contains("HTTP/1.1 401"));
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					tc.verifyErrorResponseMessage(response.asString(), "client", "Unauthorized. Access token is missing or invalid.");
					logger.pass("Not Allowed to extracted OSP Details Info With Invalid token");
					
				
				}
				
				@Test
				public void getOSPDetailsInfoWithInvalidGlobalUltimate() throws InterruptedException {

					logger = extent.createTest("getOSPDetailsInfoWithInvalidGlobalUltimate");
					String apiURI = domain + path;
					Response response = RestAssured.given().relaxedHTTPSValidation()
							.header("Authorization", "Bearer " + Token)
							.header("Content-Type", "application/json")
							.header("Master-Account-Id", "abcde")
							.header("Account-Id", accountID)
							.header("User-Email", "gpuat22-uat3@yahoo.com")
							.get(apiURI);

					System.out.println("===>" + response.asString());

					logger.log(Status.INFO, response.statusLine());
					Assert.assertEquals(response.getStatusCode(), 400);
					Assert.assertTrue(response.statusLine().contains("HTTP/1.1 400"));
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					tc.verifyErrorResponseMessage(response.asString(), "client", "Bad request was submitted.");
					logger.pass("Not Allowed to extracted OSP Details Info With Invalid Global Ultimate");
				}

			
		
	

}
