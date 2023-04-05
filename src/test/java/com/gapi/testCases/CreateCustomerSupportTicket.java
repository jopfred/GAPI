package com.gapi.testCases;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;

import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class CreateCustomerSupportTicket extends BaseClass {

	public static String path = "/customer-support-tickets";
	static TestUtilities tc = new TestUtilities();
	static String date = tc.getCurrentDateAndTime();

	//Create customer support tickets with all possible combinations	
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void verifyCreateCustomerSupportTicket(Map<String, String> data) throws IOException, InterruptedException {
		
		String body =tc.getRequestBody(data);
		System.out.println(data.get("ScenarioName")+ "\n");
		System.out.println("Request payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createCustomerSupportTicket(Token,body);
		logger.log(Status.INFO, response.asString());
		String expectedStatusCode = data.get("expectedStatusCode");
		String requestType = data.get("requestType");
		String category = data.get("category");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	    //Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		if (response.getStatusCode()==201) {
			String WONumber = tc.getWONumberFromResponse(response);
			log.info("The WO Number Created is " + WONumber);
			logger.log(Status.PASS, "Successfully Created Customer Support Ticket");
			logger.log(Status.PASS, "Created Customer Support Ticket Id Is " + WONumber);
			tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "CustomerSupportId_" + WONumber);
			tc.retrieveGETCallResponseAndCompareWithRequestParameters(WONumber, body, path);
			
			
		}
		else {
			logger.log(Status.PASS, "Not allowed to create Customer Support Ticket");
			String expectedErrorMessageType = data.get("errorMessageType");
			String expectedErrorMessage = data.get("errorMessage");
			tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
		}
		
	}
	
	// verify customer support end to end flow
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void createCustomerSupportTicketAndVerifyEndToEndFlow(Map<String, String> data) throws IOException, InterruptedException{
		
		String body =tc.getRequestBody(data);
		System.out.println(data.get("TestCaseName")+ "\n");
		System.out.println("Request Payload is \n" + body);
		logger = extent.createTest(data.get("TestCaseName"));
		Response response = createCustomerSupportTicket(Token,body);
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode  = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
	    //Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		log.info("Created Customer Support Ticket");
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
		String WONumber = tc.getWONumberFromResponse(response);
		log.info("The WO Number Created is " + WONumber);
		logger.log(Status.PASS, "Created Customer Support Ticket Id Is  " + WONumber);
		tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "CustomerSupportId_" + WONumber);
		tc.retrieveGETCallResponseAndCompareWithRequestParameters(WONumber, body, path);
		tc.addCommentToTheRequestAndVerify(WONumber,path);
		tc.addAttachmentToTheRequestAndVerify(WONumber,path);
		
	}
	//Create CS Ticket with Invalid user RBAC
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void verifyCreateCSTicketForInvalidUser(Map<String, String> data) throws IOException, InterruptedException {	
			
			logger = extent.createTest(data.get("TestCaseName"));
			JSONObject customerSupportTicketdetails = new JSONObject();
			customerSupportTicketdetails.put("site", data.get("site"));
			customerSupportTicketdetails.put("location", data.get("location"));
			customerSupportTicketdetails.put("requestType", data.get("requestType"));
			customerSupportTicketdetails.put("category", data.get("category"));
			customerSupportTicketdetails.put("description", data.get("description"));
			customerSupportTicketdetails.put("title", data.get("title"));
			
			String apiURI = domain + path;
			System.out.println(apiURI);
			String jsonbody = customerSupportTicketdetails.toString();
			Response response = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer " + Token)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID)
					.header("User-Email", "vigneswarareddy@digitalrealty.com")
					.body(jsonbody).post(domain + path).then().extract().response();
			
			System.out.println("Response is : " + response.asString());
			System.out.println("Status Code is : " + response.getStatusCode());
			Assert.assertEquals(404, response.getStatusCode());
			logger.log(Status.PASS, "Response Status Code and Status Message is " + response.statusLine());
			
			if (response.getStatusCode()==200) {
				String WONumber = tc.getWOIDFromResponse(response);
				log.info("The WO Number Created is " + WONumber);
				logger.log(Status.PASS, "Successfully Created cs Ticket");
				logger.log(Status.PASS, "Created cs Ticket  " + WONumber);
				tc.saveRequestIdToExcelSheet(excelPath, "RequestId", 0, 1, date, "cs_" + WONumber);
				//tc.retrieveFAGETResponseAndRequestParameters(WONumber, jsonbody, "service-tickets?id=");			
			}
			else {
				logger.pass("Error Response message is " + response.asString());
				logger.log(Status.PASS, "Not allowed to create cs Ticket");
				String expectedErrorMessageType = data.get("errorMessageType");
				String expectedErrorMessage = data.get("errorMessage");

	}
	}
		
		//Retrieve attachments using attachmentID for CS ticket --pavan 08-03-2023
		  static String path2 = "/attachments";
		    static String path3 = "/attachments/";
			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void retrieveCustomerSupportTicketAttachmentusingRequestId(Map<String, String> data) throws IOException, InterruptedException{
				
				String body =tc.getRequestBody(data);
				System.out.println(data.get("TestCaseName")+ "\n");
				System.out.println("Request Payload is \n" + body);
				logger = extent.createTest(data.get("TestCaseName"));
				Response response = createCustomerSupportTicket(Token,body);
				String expectedStatusCode = data.get("expectedStatusCode");
				int actualStatusCode  = response.getStatusCode();
				String expectedStatusMessage = data.get("ExpectedStatusMessage");
				Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
				String WONumber = tc.getWONumberFromResponse(response);
				String attachmentfilter = data.get("filters");
				String apiURI = domain+ path2 +attachmentfilter+WONumber;
				System.out.println("API URL by ID: " + apiURI);
				File testUploadFile = new File(System.getProperty ("user.dir") + "\\src\\test\\java\\com\\gapi\\testData\\Sample image.png");
				Response responsepst = tc.postAttachmentRequest (Token,masterAccountID,accountID,apiURI,testUploadFile);
				log.info("Attached file to the customer support ticket");
				logger.log(Status.PASS, "Successfully attached attachment to the customer support ticket");
				logger.log(Status.PASS,	"Response Status Code and Status Message is after editing the ticket" + responsepst.statusLine());
				System.out.println("Response is : " + responsepst.asString());
				String apiURL = domain+ path3+WONumber;
				System.out.println("API URL by ID: " + apiURL);
				Response responseatt = tc.getRequest(Token,masterAccountID,accountID,apiURL);
				log.info("Retrieved Attached file to the customer support ticket");
				logger.log(Status.PASS, "Successfully retrieved attachment to the Customer Support ticket");
				logger.log(Status.PASS,	"Response Status Code and Status Message is after editing the ticket" + responseatt.statusLine());
				System.out.println("Responseatt is : " + responseatt.asString());
				
			}

	
	// Re-usable method to create Customer Support Ticket

	public static Response createCustomerSupportTicket(String Token, String body) throws IOException {

		Response response = RestAssured.given().relaxedHTTPSValidation()
				.header("Authorization", "Bearer "+Token)
				.header("Content-Type", "application/json")
				.header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID)
				.header("User-Email", "admin@digitalrealty.com")
				.body(body)
				.post(domain + path)
				.then().extract().response();

		return response;
	}

}
