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

public class ModifyFAPermanentBadge extends BaseClass {
	public static String path="/facility-access/perm-badges/modify";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String accessStartDate = tc.getCurrentDateAndTime();
	static String accessEndDate = tc.getCurrentDateTime();
	// MOdify Visitor Access Ticket with all possible combinations	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyModifyPermanentBadgeRequest(Map<String, String> data) throws IOException, InterruptedException {
		
		logger = extent.createTest(data.get("TestCaseName"));
		
		String body="{\r\n    \"firstName\": \""+data.get("visitorFirstName")+"\","
				+ "\r\n  \"lastName\": \""+data.get("visitorLastName")+"\","
				+ "\r\n  \"email\": \""+data.get("visitorEmail")+"\","
				+ "\r\n  \"badgeStartDate\": \""+accessStartDate+"\","
				+ "\r\n  \"badgeExpirationDate\": \""+accessEndDate+"\","
				+ "\r\n  \"emailNotifications\": \""+data.get("emailNotifications")+"\","
				+ "\r\n  \"phone\": \""+data.get("visitorPhone")+"\","
				+ "\r\n  \"company\": \""+data.get("company")+"\","
				+ "\r\n  \"addSites\": [\r\n  {\r\n \"site\": \""+data.get("site")+"\","
				+ "\r\n  \"locations\": [\r\n  {\r\n  \"location\": \""+data.get("locations")+"\","
				+ "\r\n  \"accessStartDate\": \""+accessStartDate+"\","
				+ "\r\n  \"accessEndDate\": \""+accessEndDate+"\","
				+ "\r\n  \"accessStartTime\": \"06:22:00\","
				+ "\r\n  \"accessEndTime\": \"06:22:00\"\r\n }\r\n  ]\r\n   }\r\n    ]\r\n}";

		
		System.out.println("Body is : " + body);
				
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(body).put(domain+path)
				.then().extract().response();
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());

		if (response.getStatusCode() == 200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Modified Permanent Badge request");
			logger.log(Status.PASS, "Modified Permanent Badge request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "Modify_Permanentbadge_" + WONumber);
		} else {
			//logger.fail("Error Response message is " + response.asString());
			logger.info("Error Response message is " + response.asString());
			//logger.log(Status.FAIL, "Not allowed to Permanent Badge request");
			logger.log(Status.PASS, "Not allowed to Permanent Badge request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
		}
	}

}