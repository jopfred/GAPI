package com.gapi.testCases;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

public class GetCrossConnectDetails extends BaseClass {
	
	String authToken;
	String path="/cross-connect-inventory?";
	//String audience="https://api-uat.digitalrealty.com";
	static TestUtilities tc = new TestUtilities();
	
	
	// verify retrieve Cross Connect Details using Get Calls
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class,groups= {"All"})
		public void verifyCrossConnectDetailsUsingDifferentFilters(Map<String, String> data) {
			
			logger = extent.createTest(data.get("TestCaseName"));
			String crossconnectfilters = data.get("filters");
			System.out.println("CC: "+crossconnectfilters);
			String apiURI = CCdomain + path + crossconnectfilters;
			System.out.println("URI is: "+apiURI);
			Response response = RestAssured.given()
					.header("Authorization", "Bearer "+TokenCC) 
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", cmasterAccountID)
					.header("Account-Id", caccountID)
					.header("User-Email", "phantom.aao.dlr@gmail.com")
					.get(apiURI);
			
			System.out.println("Response is: "+response.prettyPrint());
			//String allResponsebody = response.asString();
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			String expectedStatusMessage = data.get("ExpectedStatusMessage");
			logger.log(Status.PASS, "API URI :"+apiURI);
			logger.log(Status.PASS, "Response :"+response.statusLine());
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
			String allResponsebody = response.asString();
			boolean status = false;
			if (actualStatusCode == 200) {
			JSONObject object = new JSONObject(allResponsebody);
			JSONArray requestArray = object.getJSONArray("content");
			Assert.assertTrue(requestArray.length() >= 0);
			Map<String, String> objMap = new HashMap<String, String>();
			if (crossconnectfilters.contains("&")) {
				String[] values = crossconnectfilters.split("&");
				for (int j = 0; j < values.length; j++) {
					String[] keyPair = values[j].split("=");
					objMap.put(keyPair[0], keyPair[1]);
				}
			} else {
				String[] keyPair = crossconnectfilters.split("=");
				objMap.put(keyPair[0], keyPair[1]);
			}
			if (!crossconnectfilters.contains("limit") && !crossconnectfilters.contains("offset")) {
				for (int i = 0; i < requestArray.length(); i++) {
					JSONObject individualRequestInfo = requestArray.getJSONObject(i);
					for (int index = 0; index < requestArray.length(); index++) {
						/*
						 * for(String index : requestArray) { System.out.println("Book: " + book); }
						 */
						if (crossconnectfilters.contains("accountId")) {
							Assert.assertEquals(response.getBody().jsonPath().getString("content[" + i + "].accountId"),
									objMap.get("accountId"));
							status=true;
						 
						if (crossconnectfilters.contains("interconnectionPanel")) {
							Assert.assertEquals(
									response.getBody().jsonPath().getString("content[" + i + "].interconnectionPanel"),
									objMap.get("interconnectionPanel"));
							status =true;

						}
						if (crossconnectfilters.contains("interconnectionPortStatus")) {
							Assert.assertEquals(
									response.getBody().jsonPath().getString("content[" + i + "].interconnectionPortStatus"),
									objMap.get("interconnectionPortStatus"));
							status =true;

						}
				}
				}
				}
				
				if (status == true) {
					log.info("Response details Matched with the given filter criteria");
					logger.log(Status.PASS, "Response details Matched with the given filter criteria");
					Assert.assertTrue(true);
					
				} else {
					logger.log(Status.FAIL, "Response details were not Matched with the given filter criteria");
					log.info("Response details were not Matched with the given filter criteria");
					Assert.assertTrue(false);
				}
			
			}
			 else if (crossconnectfilters.contains("limit") || crossconnectfilters.contains("offset")) {
				String keyName, keyValue;
				for (int i = 0; i < requestArray.length(); i++) {
					JSONObject individualRequestInfo = requestArray.getJSONObject(i);
					for (Entry<String, String> m : objMap.entrySet()) {
						keyName = m.getKey();
						keyValue = m.getValue();
						if (keyName.equals("limit")) {
							int objectsLength = requestArray.length();
							Assert.assertEquals(String.valueOf(objectsLength), keyValue);
						} else if (keyName.equals("offset")) {
							int objectsLength = requestArray.length();
							Assert.assertTrue(objectsLength>0 || objectsLength==25);

						} /*
							 * else { String actualValue = individualRequestInfo.getString(keyName);
							 * Assert.assertEquals(actualValue, keyValue); }
							 */
					}
					String actualbillnumber = individualRequestInfo.getString("accountId");
					
					

				}
				logger.log(Status.PASS, "Response details Matched with the given filter criteria");
			}
			else {
				logger.pass("Error Response message is " + response.asString());
				logger.log(Status.PASS, "Not allowed to create Remote Hands request");
				String expectedErrorMessageType = data.get("errorMessageType");
				String expectedErrorMessage = data.get("errorMessage");
				tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
			}
			}
		}
			
		
		
		      // verify retrieve cross connect details by valid id
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class,groups= {"All"})
				public void verifyGetCrossConnectDetailsByValidId(Map<String, String> data) {

					logger = extent.createTest("verifyGetCrossConnectDetailsByValidId");
					String apiURI = CCdomain + "/cross-connect-inventory/" + "X17XY33OWFJKJ0";
					System.out.println("URI :" + apiURI);
					Response response = RestAssured.given()
							.header("Authorization", "Bearer " + TokenCC)
							.header("Content-Type", "application/json")
							.header("Master-Account-Id", cmasterAccountID)
							.header("Account-Id", caccountID)
							.header("User-Email", "phantom.aao.dlr@gmail.com")
							.get(apiURI);
					System.err.println("Response is: " + response.asString());

					String expectedStatusCode = data.get("expectedStatusCode");
					int actualStatusCode = response.getStatusCode();
					String actualmessage=response.getStatusLine();
					System.out.println("code: "+actualStatusCode);
					System.out.println("message: "+actualmessage);
					String expectedStatusMessage = data.get("ExpectedStatusMessage");
					Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
					Assert.assertEquals(response.statusLine().trim(), "HTTP/1.1 200 OK");
					
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Response Message Is" + response.asString());
					logger.pass("Allowed to Retrieve Request Details With valid Id");

				}
			
		// verify retrieve cross connect details by invalid id
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyGetCrossConnectDetailsByInValidId(Map<String, String> data) {

			logger = extent.createTest("verifyGetCrossConnectDetailsByInValidId");
			String apiURI = CCdomain + "/cross-connect-inventory/" + "ZYJWVGVBAYE";
			System.out.println("URI :"+apiURI);
			Response response = RestAssured.given()
					.header("Authorization", "Bearer "+TokenCC) 
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", "0012E00002S4aOtQAJ")
					.header("Account-Id", "0012E00002dzucpQAA")
					.header("User-Email", "phantom.aao.dlr@gmail.com")
					.get(apiURI);
			System.err.println("Response is: "+response.asString());
						
			  String expectedErrorMessageType = data.get("errorMessageType"); 
			  String expectedErrorMessage = data.get("errorMessage");
			  System.out.println("Message:"+expectedErrorMessageType);
			  System.out.println("message1 :"+expectedErrorMessage);		  
			  tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
			logger.pass("Response Status Code and Message Is " + response.getStatusLine());
			logger.pass("Error Response Message Is" + response.asString());
			logger.pass("Not Allowed to Retrieve Request Details With Invalid Id");

		}
		
		        // verify retrieve cross connect details by Empty Billing Account Number
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyGetCrossConnectDetailsByInvalidBillingAccountNumber(Map<String, String> data) throws IOException {

					logger = extent.createTest(data.get("TestCaseName"));
					String crossconnectfilters = data.get("filters");
					String apiURI = CCdomain + path + crossconnectfilters;
					System.out.println("URI :"+apiURI);
					Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
					System.err.println("Response is: "+response.asString());								
					 
					   
					Assert.assertEquals(response.getStatusCode(), 200);
					Assert.assertTrue(response.asString().contains(" \"content\": []"));
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					logger.pass("Not Allowed to Retrieve Request Details With Empty Billing Account Number");

				}
				
				// verify retrieve cross connect details by  In-valid  Interconnection PortStatus
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyGetCrossConnectDetailsByInvalidInterconnectionPortStatus(Map<String, String> data) throws IOException {

					logger = extent.createTest(data.get("TestCaseName"));
					String apiURI = CCdomain + "/cross-connect-inventory?"+data.get("filters");
					System.out.println("URI :"+apiURI);
					Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
					System.err.println("Response is: "+response.asString());								
					String expectedErrorMessageType = "client"; 
					  String expectedErrorMessage = "Query parameter interconnectionPortStatus can only have one of the following values: 'Pre-Wired', 'In-Service', 'Available'";
					  System.out.println("Message:"+expectedErrorMessageType);
					  System.out.println("message1 :"+expectedErrorMessage);	
					 Assert.assertEquals(response.getStatusCode(), 400);
					tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					logger.pass("Not Allowed to Retrieve Request Details With In-valid  Interconnection PortStatus");

				}
		
				// verify retrieve cross connect details by valid Inter connection  Port Status & Invalid inter connect Panel
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyGetCrossConnectDetailsByValidInterconnectionPortStatusAndInvalidInterConnectPanel(Map<String, String> data) throws IOException {

					logger = extent.createTest(data.get("TestCaseName"));
					String apiURI = CCdomain + "/cross-connect-inventory?" + data.get("filters");
					System.out.println("URI :" + apiURI);
					Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
					System.err.println("Response is: " + response.asString());
					Assert.assertEquals(response.getStatusCode(), 200);
					Assert.assertTrue(response.asString().contains("\"content\": []"));
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					logger.pass("Not Allowed to Retrieve Request Details With valid Inter connection  Port Status & Invalid inter connect Panel");

				}
		
				// Retrieve the Cross connect details by Invalid Bearer Token
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyGetCrossConnectDetailsByInvalidBearerToken(Map<String, String> data) throws IOException {

					logger = extent.createTest(data.get("TestCaseName"));
					String apiURI = CCdomain + "/cross-connect-inventory?"+data.get("filters");
					System.out.println("URI :"+apiURI);
					String invalidToken="123444";
					Response response = tc.getRequest(invalidToken, cmasterAccountID, caccountID, apiURI);
					System.err.println("Response is: "+response.asString());								
					String expectedErrorMessageType = "client"; 
					  String expectedErrorMessage = "Unauthorized. Access token is missing or invalid.";
					  System.out.println("Message:"+expectedErrorMessageType);
					  System.out.println("message1 :"+expectedErrorMessage);	
					 Assert.assertEquals(response.getStatusCode(), 401);
					tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					logger.pass("Not Allowed to Retrieve Request Details With In-valid  Interconnection PortStatus");

				}
				
				// Retrieve the Cross connect details by Invalid Limit value(limit=0)
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyGetCrossConnectDetailsByInvalidLimitValue(Map<String, String> data) throws IOException {

					logger = extent.createTest(data.get("TestCaseName"));
					String apiURI = CCdomain + "/cross-connect-inventory?"+data.get("filters");
					System.out.println("URI :"+apiURI);
					Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
					System.err.println("Response is: "+response.asString());								
					String expectedErrorMessageType = "client"; 
					  String expectedErrorMessage = "Query parameter 'limit' value has to be between '1' and '1000'.";
					  System.out.println("Message:"+expectedErrorMessageType);
					  System.out.println("message1 :"+expectedErrorMessage);	
					 Assert.assertEquals(response.getStatusCode(), 400);
					tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					logger.pass("Not Allowed to Retrieve Request Details With Invalid Limit value");

				}
				
				//Retrieve the cross connect details by providing the Negative Offset values
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyGetCrossConnectDetailsWithNegativeOffset(Map<String, String> data) throws IOException {

					logger = extent.createTest(data.get("TestCaseName"));
					String apiURI = CCdomain + path+data.get("filters");
					System.out.println("URI :"+apiURI);
					Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
					System.err.println("Response is: "+response.asString());								
					String expectedErrorMessageType = "client"; 
					  String expectedErrorMessage = "Query parameter 'offset' can't be a negative number.";
					  System.out.println("Message:"+expectedErrorMessageType);
					  System.out.println("message1 :"+expectedErrorMessage);	
					 Assert.assertEquals(response.getStatusCode(), 400);
					tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					logger.pass("Not Allowed to Retrieve Request Details With Negative Offset values");

				}
				
				//Retreive the cross connect details by  providing the Non-Interger Offset values
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyGetCrossConnectDetailsWithNonIntergerOffset(Map<String, String> data) throws IOException {

					logger = extent.createTest(data.get("TestCaseName"));
					String apiURI = CCdomain + path+data.get("filters");
					System.out.println("URI :"+apiURI);
					Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
					System.err.println("Response is: "+response.asString());								
					String expectedErrorMessageType = "client"; 
					  String expectedErrorMessage = "Query parameter 'offset' has to be formatted as 'integer'.";
					  
					 Assert.assertEquals(response.getStatusCode(), 400);
					tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					logger.pass("Not Allowed to Retrieve Request Details With Non-Interger Offset values");

				}
				
				//Retrieve the cross connect details by  providing the Non-Integer Limit values
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyGetCrossConnectDetailsWithNonIntegerLimitValues(Map<String, String> data) throws IOException {

					logger = extent.createTest(data.get("TestCaseName"));
					String apiURI = CCdomain + path+data.get("filters");
					System.out.println("URI :"+apiURI);
					Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
					System.err.println("Response is: "+response.asString());								
					String expectedErrorMessageType = "client"; 
					  String expectedErrorMessage = "Query parameter 'limit' has to be formatted as 'integer'.";
					  
					 Assert.assertEquals(response.getStatusCode(), 400);
					tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					logger.pass("Not Allowed to Retrieve Request Details With Non-Integer Limit values");

				}
				
				//Retrieve the cross connect details by  providing the Negative Limit values
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyGetCrossConnectDetailsWithNegativeLimitValues(Map<String, String> data) throws IOException {

					logger = extent.createTest(data.get("TestCaseName"));
					String apiURI = CCdomain + path+data.get("filters");
					System.out.println("URI :"+apiURI);
					Response response = tc.getRequest(TokenCC, cmasterAccountID, caccountID, apiURI);
					System.err.println("Response is: "+response.asString());								
					String expectedErrorMessageType = "client"; 
					  String expectedErrorMessage = "Query parameter 'limit' has to be formatted as 'integer'.";
					  
					 Assert.assertEquals(response.getStatusCode(), 400);
					tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					logger.pass("Not Allowed to Retrieve Request Details With Negative Limit values");

				}
				// Retrieve the Cross connect details using id by Invalid Bearer Token
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void verifyGetCrossConnectDetailsusingIDByInvalidBearerToken(Map<String, String> data) throws IOException {

					logger = extent.createTest(data.get("TestCaseName"));
					String apiURI = CCdomain + "/cross-connect-inventory/"+ "ZYJWVGVBAYE";
					System.out.println("URI :"+apiURI);
					String invalidToken="123444";
					Response response = tc.getRequest(invalidToken, cmasterAccountID, caccountID, apiURI);
					System.err.println("Response is: "+response.asString());
					String expectedStatusCode = data.get("expectedStatusCode");
					int actualStatusCode = response.getStatusCode();
					//String expectedStatusMessage = data.get("ExpectedStatusMessage");
					String expectedErrorMessageType = data.get("errorMessageType");
					  String expectedErrorMessage = data.get("errorMessage");
					  System.out.println("Message:"+expectedErrorMessageType);
					  System.out.println("message1 :"+expectedErrorMessage);	
					  Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
					tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
					logger.pass("Response Status Code and Message Is " + response.getStatusLine());
					logger.pass("Error Response Message Is" + response.asString());
					logger.pass("Not Allowed to Retrieve Request Details With In-valid bearer token");

				}
}
