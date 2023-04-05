package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CreatePermanentBadgeRequest extends BaseClass {
	
	public static String path="/facility-access/perm-badges";
	//public static String accpath="/facility-access/access-tickets";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String accessStartDate = tc.getCurrentDateAndTime();
	static String accessEndDate = tc.getCurrentDateTime();
	
	// create Visitor Access Ticket with all possible combinations
	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreatePermanentBadgeRequest(Map<String, String> data) throws IOException, InterruptedException {
		
		
		//logger = extent.createTest("verifyCreatePermanentBadgeRequest");
		logger = extent.createTest(data.get("TestCaseName"));
			
		String body = createPermanentBadgePayLoad(data);
				
		String apiURI = domain + path;
		System.out.println(body);
		//String body =tc.getRequestBody(data);
		//System.out.println(body);
		Response response = createPermanentBadgeRequest(Token,body);
		
		System.out.println("exam: "+response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	//Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		
		if (response.getStatusCode()==200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Permanent Badge request");
			logger.log(Status.PASS, "Created Permanent Badge request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "PermanentBadge_" + WONumber);
			//tc.retrieveFAGETResponseAndRequestParameters(WONumber, body, accpath);
			GetRemoteHandsTicketDetails obj = new GetRemoteHandsTicketDetails();
			/*
			 * String getALLAPI =
			 * domain+"/remotehands-tickets?requestType="+requestType+"&category="+category;
			 * System.out.println(getALLAPI);
			 * obj.verifyGetAllWithAllRequestTypesAndCategoryValues(WONumber,getALLAPI);
			 */
		}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Visitor access request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}
		 
		
	}
	
	//Create Permanent Badge Request For Invalid Token	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreatePermanentBadgeRequestForInvalidToken(Map<String, String> data) throws IOException, InterruptedException {
		
		
		logger = extent.createTest(data.get("TestCaseName"));
			
		String body = createPermanentBadgePayLoad(data);
				
		String apiURI = domain + path;
		System.out.println(body);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+"abcde")
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(body)
				.post(domain + path)
				.then().extract().response();
		log.info("Provided Invalid Token");
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		
		Assert.assertEquals(response.getStatusCode(), 401);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 401"));
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		log.info("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		tc.verifyErrorResponseMessage(response.asString(), "client", "Unauthorized. Access token is missing or invalid.");
		logger.pass("Not Allowed to Create a Permanent Badge Request With Invalid token");		 
		log.info("Not Allowed to Create a Permanent Badge Request With Invalid token");
		
	}
	
	//Create Permanent Badge Request For Invalid Legal Entity	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreatePermanentBadgeRequestForInvalidLegalentity(Map<String, String> data) throws IOException, InterruptedException {				
		logger = extent.createTest(data.get("TestCaseName"));
		String body = createPermanentBadgePayLoad(data);
				
		String apiURI = domain + path;
		System.out.println(body);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id",masterAccountID)
				.header("Account-Id", "abcde")
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(body)
				.post(domain + path)
				.then().extract().response();
		log.info("Provided Invalid Legal Entity");
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		
		Assert.assertEquals(response.getStatusCode(), 400);
		Assert.assertTrue(response.statusLine().contains("HTTP/1.1 400"));
		log.info("Response Status Code and Message Is  "+response.getStatusLine());
		logger.pass("Response Status Code and Message Is " + response.getStatusLine());
		logger.pass("Error Response Message Is" + response.asString());
		tc.verifyErrorResponseMessage(response.asString(), "client", "Bad request was submitted.");
		logger.pass("Not Allowed to Create a Permanent Badge Request With Invalid Legal Entity");		 
		log.info("Not Allowed to Create a Permanent Badge Request With Invalid Legal Entity"); 
		
	}


	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyUploadAttachmentToPermanentBadgeRequest(Map<String, String> data) throws IOException, InterruptedException {
		
		
		//logger = extent.createTest("verifyCreatePermanentBadgeRequest");
		logger = extent.createTest(data.get("TestCaseName"));			
		String body = createPermanentBadgePayLoad(data);		
		String apiURI = domain + path;
		System.out.println(body);
		//String body =tc.getRequestBody(data);
		//System.out.println(body);
		String WONumber=null;
		Response response = createPermanentBadgeRequest(Token,body);
		
		System.out.println("exam: "+response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		
		if (response.getStatusCode()==200) {
			WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Permanent Badge request");
			logger.log(Status.PASS, "Created Permanent Badge request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "PermanentBadge_" + WONumber);
			GetRemoteHandsTicketDetails obj = new GetRemoteHandsTicketDetails();
			}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Visitor access request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}
		 
		tc.addAttachmentToTheRequestAndVerify(WONumber,path,Token);
	}
	
		public String createPermanentBadgePayLoad(Map<String, String> data)
		{
			String body = "{\r\n"
					+ "\"firstName\":\""+data.get("visitorFirstName")+"\","
					+ "\r\n\"lastName\":\""+data.get("visitorLastName")+"\","
					+ "\r\n\"email\":\""+data.get("visitorEmail")+"\","
					+ "\r\n\r\n\"company\":\""+data.get("company")+"\","
					+ "\r\n\"badgeStartDate\": \""+date+"\","
					+ "\r\n\"phone\": \""+data.get("visitorPhone")+"\","
					+ "\r\n \"emailNotifications\": \""+data.get("emailNotifications")+"\","
					+ "\r\n\r\n\"sites\":[\r\n      {\r\n \"site\": \""+data.get("site")+"\","
					+ "\r\n        \"locations\": [ {\r\n \"location\": \""+data.get("locations")+"\","
					+ "\r\n  \"accessStartDate\": \""+accessStartDate+"\","
					+ "\r\n \"accessEndDate\": \""+accessEndDate+"\","
					+ "\r\n \"accessStartTime\": \"06:22:00\","
					+ "\r\n \"accessEndTime\": \"06:22:00\"\r\n }"
					+ "\r\n        ]      }    ]\r\n}";
			
			return body;
		}
	
	// Re-usable methods for Create FA Ticket
		// *************************************************************************************

		public static Response createPermanentBadgeRequest(String Token, String body) throws IOException {

			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Token)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID)
					.header("User-Email", "vigneswarareddy@digitalrealty.com")
					.body(body)
					.post(domain + path)
					.then().extract().response();
			
			return response;
		}

}
