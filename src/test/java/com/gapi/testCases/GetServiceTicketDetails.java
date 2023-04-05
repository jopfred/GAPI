package com.gapi.testCases;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetServiceTicketDetails extends BaseClass {

	String path = "/service-tickets";
	static TestUtilities tc = new TestUtilities();
	

public void getAllServiceTicketDetailsinfo() throws InterruptedException, IOException {
	
	logger = extent.createTest("verifygetgetAllServiceTicketDetailsinfo");

	String apiURI = domain + path;
	Response response = RestAssured.given().relaxedHTTPSValidation()
			.header("Authorization", "Bearer " + Token)
			.header("Content-Type", "application/json")
			.header("Master-Account-Id", masterAccountID)
			.header("Account-Id", accountID)
			.get(apiURI);

	logger.log(Status.INFO, response.statusLine());
	Assert.assertEquals(response.getStatusCode(), 200);
	Assert.assertTrue(response.statusLine().contains("OK"));
	log.info("Successfully extracted all Service Ticket Info ");
	logger.log(Status.INFO, "Successfully extracted all Service Ticket Info");
	logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());

}

//verify retrieve all service ticket request details using various filters

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyGetAllWithServiceTicketFilters(Map<String, String> data) throws InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		String filters = data.get("filters");
		String apiURI = domain + path +"?"+ filters;
		System.out.println("Request is: "+apiURI);
		//Thread.sleep(1000);
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "msirikonda@digitalrealty.com")
				.get(apiURI);

		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		//Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		String allResponsebody = response.asString();
		logger.log(Status.INFO, "API -" + apiURI);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.getStatusLine());
		logger.log(Status.PASS, allResponsebody);
		JSONObject object = new JSONObject(allResponsebody);
		JSONArray requestArray = object.getJSONArray("content");
		Assert.assertTrue(requestArray.length() >= 0);
		Map<String, String> objMap = new HashMap<String, String>();
		//filters=filters.replace("KVM Assistance", "KVM (keyboard, video, mouse) assistance");
		if (filters.contains("&")) {
			String[] values = filters.split("&");
			for (int j = 0; j < values.length; j++) {
				String[] keyPair = values[j].split("=");
				objMap.put(keyPair[0], keyPair[1]);
			}
		} else {
			String[] keyPair = filters.split("=");
			objMap.put(keyPair[0], keyPair[1]);
		}
		if (!filters.contains("size") && !filters.contains("page")) {
			for (int i = 0; i < requestArray.length(); i++) {
				JSONObject individualRequestInfo = requestArray.getJSONObject(i);
				for (Entry<String, String> m : objMap.entrySet()) {
					String actualValue = individualRequestInfo.getString(m.getKey());
					Assert.assertEquals(actualValue, m.getValue());
				}
				if(!(individualRequestInfo.getString("ticketType").contains("Shipping and Receiving")))
				{
				String actualRequestType = individualRequestInfo.getString("requestType").trim();
				Assert.assertTrue(actualRequestType.equals("Planned Work") || actualRequestType.equals("Urgent Work") || actualRequestType.equals("Account Management")|| actualRequestType.equals("Accounts Receivable")|| actualRequestType.equals("Facilities Request")|| actualRequestType.equals("Amenities")||actualRequestType.equals("Visitor Access")||actualRequestType.equals("Permanent Badge")||actualRequestType.equals("OSP Access"));
				String actualCategoryValue = individualRequestInfo.getString("category");
				verifyCategoryValue(actualRequestType, actualCategoryValue);
				logger.log(Status.PASS, "Response details Matched with the given filter criteria");
				}
			}
		} else if (filters.contains("size") || filters.contains("page")) {
			String keyName, keyValue;
			for (int i = 0; i < requestArray.length(); i++) {
				JSONObject individualRequestInfo = requestArray.getJSONObject(i);
				for (Entry<String, String> m : objMap.entrySet()) {
					keyName = m.getKey();
					keyValue = m.getValue();
					if (keyName.equals("size")) {
						int objectsLength = requestArray.length();
						Assert.assertEquals(String.valueOf(objectsLength), keyValue);
					} else if (keyName.equals("page")) {
						int objectsLength = requestArray.length();
						Assert.assertTrue(objectsLength>0 || objectsLength==25);

					} else {
						String actualValue = individualRequestInfo.getString(keyName);
						Assert.assertEquals(actualValue, keyValue);
					}
				}
				if(!(individualRequestInfo.getString("ticketType").contains("Shipping and Receiving")))
				{
				String actualRequestType = individualRequestInfo.getString("requestType");
				Assert.assertTrue(actualRequestType.equals("Planned Work") || actualRequestType.equals("Urgent Work") || actualRequestType.equals("Account Management")|| actualRequestType.equals("Accounts Receivable")|| actualRequestType.equals("Facilities Request")|| actualRequestType.equals("Amenities")||actualRequestType.equals("Visitor Access")||actualRequestType.equals("Permanent Badge")||actualRequestType.equals("OSP Access")||actualRequestType.equals("PoP/POE Room Access"));
				String actualCategoryValue = individualRequestInfo.getString("category");
				verifyCategoryValue(actualRequestType, actualCategoryValue);
				logger.log(Status.PASS, "Response details Matched with the given filter criteria");
				}

			}
		}
	}
	
	public void verifyCategoryValue(String actualRequestType, String actualCategoryValue) {

		if (actualRequestType.equals("Planned Work")) {
			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("Existing cross connect or connectivity testing")
					|| actualCategoryValue.equalsIgnoreCase("Customer premise cabling")
					|| actualCategoryValue.equalsIgnoreCase("Tape swaps")
					|| actualCategoryValue.equalsIgnoreCase("Equipment installation")
					|| actualCategoryValue.equalsIgnoreCase("Equipment de-installation")
					|| actualCategoryValue.equalsIgnoreCase("Dedicated escort")
					|| actualCategoryValue.equalsIgnoreCase("Auditing")
					|| actualCategoryValue.equalsIgnoreCase("Other"));
		} else if(actualRequestType.equals("Urgent Work")) {

			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("Existing cross connect or connectivity testing")
					|| actualCategoryValue.equalsIgnoreCase("Equipment troubleshoot or replacement")
					|| actualCategoryValue.equalsIgnoreCase("KVM (keyboard, video, mouse) assistance")
					|| actualCategoryValue.equalsIgnoreCase("Power cycle or reboot")
					|| actualCategoryValue.equalsIgnoreCase("Other"));
		}
		else if(actualRequestType.equals("Account Management")){
			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("User Account Inquiry"));
		}
		else if(actualRequestType.equals("Accounts Receivable")){
			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("General Billing Inquiry"));
		}
		else if(actualRequestType.equals("Facilities Request")){
			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("Cooling - Datacenter")
					|| actualCategoryValue.equalsIgnoreCase("Cooling - Office")
					|| actualCategoryValue.equalsIgnoreCase("Electrical")
					|| actualCategoryValue.equalsIgnoreCase("Fire/Safety")
					|| actualCategoryValue.equalsIgnoreCase("Planned Maintenance"));
		}
		else if(actualRequestType.equals("Visitor Access")) {

			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("New Visitor Access")
					|| actualCategoryValue.equalsIgnoreCase("Modify Visitor Access")
					|| actualCategoryValue.equalsIgnoreCase("KVM (keyboard, video, mouse) assistance")
					|| actualCategoryValue.equalsIgnoreCase("Terminate All Visitor Access"));
		}
		else if(actualRequestType.equals("Permanent Badge")) {

			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("New Badge")
					|| actualCategoryValue.equalsIgnoreCase("Terminate All Access")
					|| actualCategoryValue.equalsIgnoreCase("Modify Badge"));
		}
		else if(actualRequestType.equals("OSP Access")) {

			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase(""));
					 
		}
		else if(actualRequestType.equals("PoP/POE Room Access")) {

			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase(""));
					 
		}
		else
		{
			Assert.assertTrue(actualCategoryValue.equalsIgnoreCase("Elevator")
					|| actualCategoryValue.equalsIgnoreCase("Loading Dock")
					|| actualCategoryValue.equalsIgnoreCase("Parking")
					|| actualCategoryValue.equalsIgnoreCase("General Inquiry"));
		}
	}
	
}
