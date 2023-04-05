package com.gapi.testCases;

import java.io.IOException;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;
import com.github.javafaker.Faker;

import io.restassured.RestAssured;
import io.restassured.response.Response;


/* Terminate Visitor Access */


public class TerminatePermanentBadgeRequest extends BaseClass {
	
	public static String path="/facility-access/perm-badges/terminate";
	//public static String accpath="/facility-access/access-tickets";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String date1 = tc.getCurrentDateTime();
	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyTerminatePermanentBadgeRequest(Map<String, String> data) throws IOException, InterruptedException {
		
		
		
//		logger = extent.createTest("verifyTerminatePermanentBadgeRequest");
		logger = extent.createTest(data.get("TestCaseName"));
		
		String body = 
				
				"{\r\n\"firstName\":\""+data.get("visitorFirstName")+"\","
				+ "\r\n\"lastName\":\""+data.get("visitorLastName")+"\","
				+ "\r\n\"email\":\""+data.get("visitorEmail")+"\","
				+ "\r\n\r\n\"badgeExpirationDate\": \""+date1+"\","
				+ "\r\n\r\n\"company\":\""+data.get("company")+"\"\r\n}";
		
		
		System.out.println(body);
        Response response = TerminateCreatedPermBadgeTicket(Token,body);
		System.out.println("exam: "+response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		
		
		if (response.getStatusCode()==200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Terminated Perm Badge request");
			logger.log(Status.PASS, "Terminated Permanent badge request Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "TerminatePermbadge_" + WONumber);
		}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to Terminate Perm Badge request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}
		 
		
	}
	
	
	
	
	//Re-usable methods for Terminate FA Perm Badge Ticket
			// *************************************************************************************

			public static Response TerminateCreatedPermBadgeTicket(String Token, String body) throws IOException {

				Response response = RestAssured.given().relaxedHTTPSValidation()
						.header("Authorization", "Bearer "+Token)
						.header("Content-Type", "application/json")
						.header("Master-Account-Id", masterAccountID)
						.header("Account-Id",accountID)
						.header("User-Email", "vigneswarareddy@digitalrealty.com")
						.body(body)
						.post(domain + path)
						.then().extract().response();
				
				return response;
			}

	}
		
		
		
		
		
		
		
		
		
		
		
		
		
