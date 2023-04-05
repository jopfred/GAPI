
package com.gapi.testCases;

import org.json.JSONObject;

/** CreateRemoteHandRequest class is used to create REMOTE HANDS TICKET with the various sets of input data,
 * Validate the create REMOTE HANDS TICKET API response by using GET call 
 * and
 * verify the created REMOTE HANDS request information in service now application 
 *  @author rkasi
 *  @version 17
 *  @since 2022
 **/

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CreateRemoteHandsTicket extends BaseClass {

	public static String path = "/remotehands-tickets";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();

	// create Remote Hands Ticket with all possible combinations
	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateRemoteHandsTicket(Map<String, String> data) throws IOException, InterruptedException {
		
		String body =tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName")+ "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createRemoteHandsRequest(Token,body);
		System.out.println("exam: "+response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		String requestType = data.get("requestType");
		String category = data.get("category");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	    //Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		if (response.getStatusCode()==201) {
			String WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Remote Hands Ticket");
			logger.log(Status.PASS, "Created Remote Hands Ticket Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "RemoteHandsId_" + WONumber);
			Thread.sleep(5000);
			tc.retrieveGETCallResponseAndCompareWithRequestParameters(WONumber, body, path);
			GetRemoteHandsTicketDetails obj = new GetRemoteHandsTicketDetails();
			String getALLAPI = domain+"/remotehands-tickets?requestType="+requestType+"&category="+category;
			System.out.println(getALLAPI);
			obj.verifyGetAllWithAllRequestTypesAndCategoryValues(WONumber,getALLAPI);
		}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Remote Hands request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}
		
	}
	
	// verify remote hands end to end flow
	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void createRemoteHandsTicketAndVerifyEndToEndFlow(Map<String, String> data) throws IOException, InterruptedException{
		
		String body =tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName")+ "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createRemoteHandsRequest(Token,body);
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	    //Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		log.info("Created Remote Hands Ticket");
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String WONumber = tc.getWONumberFromResponse(response);
		log.info("The WO Number Created is " + WONumber);
		logger.log(Status.PASS, "Created Remote Hands request Id  " + WONumber);
		tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "RemoteHandsId_" + WONumber);
		tc.retrieveGETCallResponseAndCompareWithRequestParameters(WONumber, body, path);
		tc.addCommentToTheRequestAndVerify(WONumber,path);
		tc.addAttachmentToTheRequestAndVerify(WONumber,path);
		
	}
	
	//Create RH Ticket with Invalid user RBAC
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateRHTicketForInvalidUser(Map<String, String> data) throws IOException, InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		JSONObject remoteHandsdetails = new JSONObject();
		remoteHandsdetails.put("site", data.get("site"));
		remoteHandsdetails.put("location", data.get("locations"));
		remoteHandsdetails.put("title", data.get("title"));
		remoteHandsdetails.put("requestType", data.get("requestType"));
		remoteHandsdetails.put("category", data.get("category"));
		
		System.out.println("Body is------ :");
		System.out.println(remoteHandsdetails.toString());
		
		String apiURI = domain + path;
		System.out.println(apiURI);
		String jsonbody = remoteHandsdetails.toString();
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer " + "abcde")
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "vigneswarareddy@digitalrealty.com")
				.body(jsonbody).post(domain + path).then().extract().response();
		
		System.out.println("Response is : " + response.asString());
		System.out.println("Status Code is : " + response.getStatusCode());
		Assert.assertEquals(401, response.getStatusCode());
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		
		if (response.getStatusCode()==200) {
			String WONumber = tc.getWOIDFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created RH Ticket");
			logger.log(Status.PASS, "Created RH Ticket  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "rh_" + WONumber);
			//tc.retrieveFAGETResponseAndRequestParameters(WONumber, jsonbody, "service-tickets?id=");			
		}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create RH Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
		
}
}
	

	// pavan on 08-03-2023 create RH ticket with Invalid and Multiple notification Recipients 

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void createRemoteHandsTicketwithNotificationRecipient(Map<String, String> data) throws IOException, InterruptedException {
		
		String body =tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName")+ "\n");
		System.out.println("Request Payload data is  \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createRemoteHandsRequest(Token,body);
		System.out.println("exam: "+response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		String requestType = data.get("requestType");
		String category = data.get("category");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		System.out.println("Statusmessage & status code is:"+response.statusCode()+response.statusLine());
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	    //Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		if (response.getStatusCode()==201) {
			String WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Remote Hands Ticket");
			logger.log(Status.PASS, "Created Remote Hands Ticket Id  " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "RemoteHandsId_" + WONumber);
			tc.retrieveGETCallResponseAndCompareWithRequestParameters(WONumber, body, path);
			GetRemoteHandsTicketDetails obj = new GetRemoteHandsTicketDetails();
			String getALLAPI = domain+"/remotehands-tickets?requestType="+requestType+"&category="+category;
			System.out.println(getALLAPI);
			obj.verifyGetAllWithAllRequestTypesAndCategoryValues(WONumber,getALLAPI);
		}
		else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to create Remote Hands request");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}
	}

	

	
	// Re-usable methods for Create Remote Hands Ticket
	// *************************************************************************************

	public static Response createRemoteHandsRequest(String Token, String body) throws IOException {
		System.out.println("Body is : " + body);
		
		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "msirikonda@digitalrealty.com")
				.body(body)
				.post(domain + path)
				.then().extract().response();
		
		return response;
	}

}
