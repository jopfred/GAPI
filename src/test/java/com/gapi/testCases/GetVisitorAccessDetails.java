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


public class GetVisitorAccessDetails extends BaseClass {

	String path = "/facility-access/visitors";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();
	static String visitorBadgeStartDate = tc.getCurrentDateAndTime();
	static String visitorBadgeEndDate = tc.getCurrentDateTime();


	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void GetVisitorAccessDetailsInfo(Map<String, String> data) throws InterruptedException, IOException {

		logger = extent.createTest("Get Visitor Access Details Info");
		String body="{\r\n    \"visitors\": [\r\n {\r\n\""
				+ "visitorFirstName\": \""+data.get("visitorFirstName")+"\",\r\n\""
				+ "visitorLastName\": \""+data.get("visitorLastName")+"\",\r\n\""
				+ "visitorEmail\": \""+data.get("visitorEmail")+"\"\r\n}\r\n    ],\r\n\""
				+ "visitorType\": \""+data.get("visitorType")+"\",\r\n    \""
				+ "escortFirstName\": \""+data.get("escortFirstName")+"\",\r\n    \""
				+ "escortLastName\": \""+data.get("escortLastName")+"\",\r\n    \""
				+ "visitorHostFirstName\": \""+data.get("visitorHostFirstName")+"\",\r\n    \""
				+ "visitorHostLastName\": \""+data.get("visitorHostFirstName")+"\",\r\n    \""
				+ "visitorHostCompany\": \""+data.get("visitorHostCompany")+"\",\r\n    \""
				+ "visitorBadgeStartDate\": \""+visitorBadgeStartDate+"\",\r\n    \""
				+ "visitorBadgeEndDate\": \""+visitorBadgeEndDate+"\",\r\n    \""
				+ "company\": \""+data.get("company")+"\",\r\n    \""
				+ "notes\": \""+data.get("notes")+" \",\r\n    \""
				+ "emailNotifications\": \""+data.get("emailNotifications")+"\",\r\n    \""
				+ "isExtendedVisitorBadge\": "+data.get("isExtendedVisitorBadge")+",\r\n    \""
				+ "standardVisitorBadgeStr\": \""+data.get("standardVisitorBadgeStr")+"\",\r\n    \""
				+ "extendedVisitorBadgeStr\": \""+data.get("extendedVisitorBadgeStr")+"\",\r\n    \""
				+ "addSites\": [\r\n        {\r\n            \""
				+ "site\": \""+data.get("site")+"\",\r\n            \""
				+ "locations\": [\r\n                {\r\n                   \""
				+ "location\": \""+data.get("locations")+"\",\r\n                    \""
				+ "accessStartDate\": \""+visitorBadgeStartDate+"\",\r\n                    \""
				+ "accessEndDate\": \""+visitorBadgeEndDate+"\",\r\n                    \""
				+ "accessStartTime\": \"06:22:00\",\r\n                    \""
				+ "accessEndTime\": \"06:22:00\"\r\n                }\r\n            ]\r\n        }\r\n    ]\r\n}";
		String apiURI = domain + path;
		System.out.println("body is :"+apiURI);
		System.out.println("body is :"+body);
		Response crResponse = tc.createFARequest(Token, body, apiURI);
		System.out.println("Response:"+crResponse.asString());
		Assert.assertEquals(crResponse.getStatusCode(), 200);
		String WONumber = tc.getWOIDFromResponse(crResponse);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.get(apiURI);
		System.out.println("Respo :"+response.asPrettyString());

		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		Assert.assertTrue(response.statusLine().contains("OK"));
		Assert.assertTrue(response.asString().contains(WONumber));
		Assert.assertTrue(response.asString().contains("Visitor Access"));
		log.info("Successfully extracted all Visito access Ticket Info ");
		logger.log(Status.INFO, "Successfully extracted all Visito access Ticket Info");
		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
	}
}
