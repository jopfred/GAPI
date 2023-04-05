package com.gapi.testCases;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
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

public class GetAllAssets extends BaseClass {
	static TestUtilities tc = new TestUtilities();
	public String path = "/assets/all";

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getAllAssetsInformation(Map<String, String> data) throws InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		String apiURI = idpdomain + path;
		System.out.println(apiURI);
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "phantom.aao.dlr@gmail.com")
				.get(apiURI);

		logger.pass("All Assets Information::" + response.getBody().asPrettyString());
		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		// String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		// Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));

		System.out.println(response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		// Assert.assertTrue(response.statusLine().contains("OK"));
		logger.log(Status.INFO, "Successfully extracted All Assets Information");
		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());

	}

	// Retrieve All Assets Information based on different filters
	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getAllAssetsInformationUsingDifferentFilters(Map<String, String> data) throws InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		String allAssetsFilters = data.get("filters");
		// System.out.println(citiesFilters);
		String apiURI = idpdomain + path + allAssetsFilters;
		System.out.println(apiURI);
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "phantom.aao.dlr@gmail.com")
				.get(apiURI);

		String expectedStatusCode = data.get("expectedStatusCode");
		int actualStatusCode = response.getStatusCode();
		String expectedStatusMessage = data.get("ExpectedStatusMessage");
		Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
		// Assert.assertTrue(response.statusLine().contains(expectedStatusMessage));
		String allResponsebody = response.asString();
		logger.log(Status.INFO, "API -" + apiURI);
		logger.log(Status.PASS, "Response Status Code and Status Message is " + response.getStatusLine());
		logger.log(Status.PASS, allResponsebody);
		if (actualStatusCode == 200) {
			JSONObject object = new JSONObject(allResponsebody);
			JSONArray requestArray = object.getJSONArray("content");
			Assert.assertTrue(requestArray.length() >= 0);
			Map<String, String> objMap = new HashMap<String, String>();
			if (allAssetsFilters.contains("&")) {
				String[] values = allAssetsFilters.split("&");
				for (int j = 0; j < values.length; j++) {
					String[] keyPair = values[j].split("=");
					objMap.put(keyPair[0], keyPair[1]);
				}
			} else {
				String[] keyPair = allAssetsFilters.split("=");
				objMap.put(keyPair[0], keyPair[1]);
			}
			if (!allAssetsFilters.contains("size") && !allAssetsFilters.contains("page")) {
				for (int i = 0; i < requestArray.length(); i++) {
					JSONObject individualRequestInfo = requestArray.getJSONObject(i);
					/*
					 * for (Entry<String, String> m : objMap.entrySet()) { String actualValue =
					 * individualRequestInfo.getString(m.getKey()); Assert.assertEquals(actualValue,
					 * m.getValue()); }
					 */

				}
			} else if (allAssetsFilters.contains("size") || allAssetsFilters.contains("page")) {
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
							Assert.assertTrue(objectsLength > 0 || objectsLength == 25);

						} /*
							 * else { String actualValue = individualRequestInfo.getString(keyName);
							 * Assert.assertEquals(actualValue, keyValue);
							 */
					}
				}

			}
		} else {
			logger.pass("Error Response message is " + response.asString());
			logger.log(Status.PASS, "Not allowed to Retrive the All Assets info");
			
			  String expectedErrorMessageType = data.get("errorMessageType"); 
			  String expectedErrorMessage = data.get("errorMessage");
			    System.out.println("Response :"+response.asString());		  
			 // tc.verifyErrorResponseMessage(response.asString(),expectedErrorMessageType,expectedErrorMessage);
			    tc.verifyErrorMessagefromResponse(response.asString(),expectedErrorMessageType,expectedErrorMessage);
			
		}
	}
	// Validate the customer assets using Valid accountId & ParentID
		@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
		public void vaidateTheCustomerAssetsUsingValidAccountIdAndParentID(Map<String, String> data) {
			logger = extent.createTest(data.get("TestCaseName"));
			String apiURI = idpdomain + "/assets" + data.get("filters");
			System.out.println(apiURI);
			Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
					.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID).header("User-Email", "phantom.aao.dlr@gmail.com")
					.get(apiURI);

			logger.pass("All Assets Information::" + response.getBody().asPrettyString());
			String expectedStatusCode = data.get("expectedStatusCode");
			int actualStatusCode = response.getStatusCode();
			Assert.assertEquals(String.valueOf(actualStatusCode), expectedStatusCode);
			System.out.println(response.asString());
			Assert.assertEquals(response.getStatusCode(), 200);
			JSONArray jsonArray = new JSONArray(response.asString());
			String id = ((JSONObject) jsonArray.get(0)).optString("id");
			String sitecode = ((JSONObject) jsonArray.get(0)).optString("sitecode");

			/*
			 * logger.log(Status.INFO, "Successfully extracted All Assets Information" );
			 * logger.log(Status.PASS, "Status Code and Status Message is" +
			 * response.getStatusLine())
			 */;
			String siteAPIURI = idpdomain + "/sites/sites?site=" + sitecode;
			Response siteResponse = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer " + Tokenidp).header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID).header("Account-Id", accountID)
					.header("User-Email", "phantom.aao.dlr@gmail.com").get(siteAPIURI);
			String city = null;
			String assetId=id;
			String region=null;
			String country=null;
			 
			JSONArray siteJsonArray = new JSONArray(siteResponse.asString());
			for (int index = 0; index < siteJsonArray.length(); index++) {
				if (sitecode.contains(((JSONObject) siteJsonArray.get(index)).optString("sitecode"))) {
					city=((JSONObject) siteJsonArray.get(index)).optString("city");
					region=((JSONObject) siteJsonArray.get(index)).optString("region");
					country=((JSONObject) siteJsonArray.get(index)).optString("country");

				}
			}
			String assetsValidatePayLoad= "[  {\r\n        \"accountId\": \"0012E00002dmMWaQAM\",\r\n        "
					+ "\"siteRequests\":[\r\n            {\r\n                "
					+ "\"assetId\": \""+assetId+"\",\r\n                "
					+ "\"sitecode\": \""+sitecode+"\",\r\n                "
					+ "\"region\": \""+region+"\",\r\n               "
					+ "\"country\": \""+country+"\",\r\n                "
					+ "\"city\": \""+city+"\"\r\n            }\r\n        ]\r\n}\r\n\r\n]";
			
			String validateAPI=idpdomain + "/assets/validate";
			Response validateResponse = RestAssured.given().relaxedHTTPSValidation()
					.header("Authorization", "Bearer "+Tokenidp)
					.header("Content-Type", "application/json")
					.header("Master-Account-Id", masterAccountID)
					.header("Account-Id", accountID)
					.header("User-Email", "phantom.aao.dlr@gmail.com")
					.body(assetsValidatePayLoad)
					.post(validateAPI)
					.then().extract().response();
			 
			Assert.assertEquals(validateResponse.getStatusCode(), 200);
			Assert.assertEquals(validateResponse.statusLine().trim(), "HTTP/1.1 200");
			if (validateResponse.getStatusCode() == 200) {
				log.info("Validate the customer assets using Valid accountId & ParentID Successfully");
				logger.log(Status.PASS, "Validate the customer assets using Valid accountId & ParentID Successfully");
				log.info("Validate the customer assets using Valid accountId & ParentID Successfully");
				logger.log(Status.PASS,"Status Line "+validateResponse.statusLine());
			} else {
				logger.log(Status.FAIL, "Validate the customer assets using Valid accountId & ParentID was not Successfull");
				log.info("Validate the customer assets using Valid accountId & ParentID was not Successfull");
				logger.log(Status.PASS, "Status Line is "+validateResponse.statusLine());
			}
			
		}
		
		// Validate the customer assets using accountId & Asset ID - Negative Testing
				@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
				public void vaidateTheCustomerAssetsUsingAccountIdAndAssetID(Map<String, String> data) {
					logger = extent.createTest(data.get("TestCaseName"));
					
					String assetsValidatePayLoad = "[\r\n  {\r\n    \"accountId\": \"0012E00002dmMWeQAM\",\r\n    \"assetIds\": [\r\n      \"0040012e1326b200a501b7a66144b06a\"\r\n    ]\r\n  }\r\n]";
					String validateAPI = idpdomain + "/assets/validate";
					Response validateResponse = RestAssured.given().relaxedHTTPSValidation()
							.header("Authorization", "Bearer " + Tokenidp).header("Content-Type", "application/json")
							.header("Master-Account-Id", masterAccountID).header("Account-Id", accountID)
							.header("User-Email", "phantom.aao.dlr@gmail.com").body(assetsValidatePayLoad).post(validateAPI).then()
							.extract().response();

					Assert.assertEquals(validateResponse.getStatusCode(), 400);
					Assert.assertEquals(validateResponse.statusLine().trim(), "HTTP/1.1 400");
					Assert.assertTrue(validateResponse.asString().contains("<Description>Payload is incorrect</Description>"));	
					System.out.println(validateResponse.asString());
					if (validateResponse.getStatusCode() == 400) {
						log.info("Validate the customer assets using accountId & Asset ID was not Successful");
						logger.log(Status.PASS, "Validate the customer assets using accountId & Asset ID was not Successful");
						logger.log(Status.PASS, "Status Line " + validateResponse.statusLine());
					} else {
						logger.log(Status.FAIL,
								"Validate the customer assets using accountId & Asset ID was Successful");
						log.info("Validate the customer assets using accountId & Asset ID was Successful");
						logger.log(Status.PASS, "Status Line is " + validateResponse.statusLine());
					}

				}
			
		// Retrieve the asset names using the Valid asset ID
			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void verifyRetrievalofAssetNamesUsingAssetID(Map<String, String> data) {
				logger = extent.createTest(data.get("TestCaseName"));
				String apiURI = idpdomain + "/assets" + data.get("filters");
				System.out.println(apiURI);
				Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
						.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
						.header("Account-Id", accountID).header("User-Email", "phantom.aao.dlr@gmail.com")
						.get(apiURI);

				System.out.println(response.asString());

				JSONArray jsonArray = new JSONArray(response.asString());
				String id = ((JSONObject) jsonArray.get(0)).optString("id");
				String assetNameActual = ((JSONObject) jsonArray.get(0)).optString("assetName");
				String namesAPIURI = idpdomain + "/assets/names";
				String namesPayLoad = "{\r\n\"assetIds\": [\r\n\"" + id + "\"\r\n]\r\n}";
				System.out.println(namesPayLoad);
				Response validateResponse = assetsPostRequest(namesPayLoad, namesAPIURI);

				Assert.assertEquals(validateResponse.getStatusCode(), 200);
				Assert.assertEquals(validateResponse.statusLine().trim(), "HTTP/1.1 200");

				JSONArray jsonArrayNames = new JSONArray(response.asString());
				String assetNameExpected = ((JSONObject) jsonArrayNames.get(0)).optString("assetName");
				Assert.assertEquals(assetNameActual, assetNameExpected);
				System.out.println(validateResponse.asPrettyString());
				if (validateResponse.getStatusCode() == 200) {
					log.info("Retrieved the asset names using the asset ID Successfully");
					logger.log(Status.PASS, "Retrieved the asset names using the asset ID Successfully");
					log.info("Validate the customer assets using Valid accountId & ParentID Successfully");
					logger.log(Status.PASS, "Status Line " + validateResponse.statusLine());
				} else {
					logger.log(Status.FAIL, "Not Able to Retrieve the asset names using the asset ID");
					log.info("Not Able to Retrieve the asset names using the asset ID");
					logger.log(Status.FAIL, "Status Line is " + validateResponse.statusLine());
				}

			}

		//Retrieve the asset names using the Invalid asset ID
			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void verifyRetrievalofAssetNamesUsingInvalidAssetID(Map<String, String> data) {
				logger = extent.createTest(data.get("TestCaseName"));
				String id = "12345";
				String namesPayLoad = "{\r\n\"assetIds\": [\r\n\"" + id + "\"\r\n]\r\n}";
				System.out.println(namesPayLoad);
				String namesAPIURI = idpdomain + "/assets/names";
				Response validateResponse = assetsPostRequest(namesPayLoad, namesAPIURI);
				Assert.assertEquals(validateResponse.getStatusCode(), 200);
				Assert.assertEquals(validateResponse.statusLine().trim(), "HTTP/1.1 200");
				Assert.assertEquals(validateResponse.asString(), "[]");
				System.out.println("Response is " + validateResponse.asPrettyString());
				if (validateResponse.getStatusCode() == 200) {
					log.info("Retrieval of the asset names using invalid asset ID was Successful");
					logger.log(Status.PASS, "Retrieval of the asset names using invalid asset ID was Successful");
					log.info("Retrieval of the asset names using invalid asset ID was Successful");
					logger.log(Status.PASS, "Status Line " + validateResponse.statusLine());
				} else {
					logger.log(Status.FAIL, "Retrieval of the asset names using invalid asset ID was not Successful");
					log.info("Retrieval of the asset names using invalid asset ID was not Successful");
					logger.log(Status.FAIL, "Status Line is " + validateResponse.statusLine());
				}

			}
			
			//Validate the customer assets using InValid accountId & InValid Asset ID - Negative Testing
			@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
			public void vaidateTheCustomerAssetsUsingInValidAccountIdAndInValidAssetID(Map<String, String> data) {
				logger = extent.createTest(data.get("TestCaseName"));
				
				String assetsValidatePayLoad = "[\r\n  {\r\n    \"accountId\": \"abcde\",\r\n    \"assetIds\": [\r\n      \"abcdes\"\r\n    ]\r\n  }\r\n]";
				String validateAPI = idpdomain + "/assets/validate";
				Response validateResponse = RestAssured.given().relaxedHTTPSValidation()
						.header("Authorization", "Bearer " + Tokenidp).header("Content-Type", "application/json")
						.header("Master-Account-Id", masterAccountID).header("Account-Id", accountID)
						.header("User-Email", "phantom.aao.dlr@gmail.com").body(assetsValidatePayLoad).post(validateAPI).then()
						.extract().response();

				Assert.assertEquals(validateResponse.getStatusCode(), 400);
				Assert.assertEquals(validateResponse.statusLine().trim(), "HTTP/1.1 400");
				Assert.assertTrue(validateResponse.asString().contains("<Description>Payload is incorrect</Description>"));			
				System.out.println(validateResponse.asString());
				if (validateResponse.getStatusCode() == 400) {
					log.info("Validate the customer assets using InValid accountId & InValid Asset ID was not Successful");
					logger.log(Status.PASS, "Validate the customer assets using InValid accountId & InValid Asset ID was not Successful");
					logger.log(Status.PASS, "Status Line " + validateResponse.statusLine());
				} else {
					logger.log(Status.FAIL,
							"Validate the customer assets using InValid accountId & InValid Asset ID was Successful");
					log.info("Validate the customer assets using InValid accountId & InValid Asset ID was Successful");
					logger.log(Status.PASS, "Status Line is " + validateResponse.statusLine());
				}

			}


		// Reusable Method to create Assets post request
			public Response assetsPostRequest(String payLoad, String apiURI) {
				Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
						.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
						.header("Account-Id", accountID).header("User-Email", "phantom.aao.dlr@gmail.com")
						.body(payLoad).post(apiURI).then().extract().response();
				return response;
			}
}
