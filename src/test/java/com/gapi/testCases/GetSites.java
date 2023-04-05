package com.gapi.testCases;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.gapi.utilities.DataProviderUtility;
import com.gapi.utilities.TestUtilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class GetSites extends BaseClass {
	static TestUtilities tc = new TestUtilities();
	public static String path = "/sites/sites";

	@Test(dataProvider = "testCasesData", dataProviderClass = DataProviderUtility.class)
	public void getAllSiteDetailsinfo(Map<String, String> data) throws InterruptedException {

		logger = extent.createTest(data.get("TestCaseName"));
		String getSitesFilters = data.get("filters");
		System.out.println(getSitesFilters);
		String apiURI = idpdomain + path + getSitesFilters;

		System.out.println(apiURI);
		Response response = RestAssured.given().relaxedHTTPSValidation().header("Authorization", "Bearer " + Tokenidp)
				.header("Content-Type", "application/json").header("Master-Account-Id", masterAccountID)
				.header("Account-Id", accountID).header("User-Email", "phantom.aao.dlr@gmail.com")
				.get(apiURI);

		System.out.println(response.getBody().asPrettyString());
		logger.pass("Displayed Cities are" + response.getBody().asPrettyString());
		logger.log(Status.INFO, response.statusLine());
		Assert.assertEquals(response.getStatusCode(), 200);
		// Assert.assertTrue(response.statusLine().contains("OK"));
		Assert.assertTrue(response.asString().contains("sitecode"));
		logger.log(Status.INFO, "Successfully extracted Sites Info");
		logger.log(Status.PASS, "Status Code and Status Message is" + response.getStatusLine());
		HashMap<String, String> filtersMap = new HashMap<>();

		if (getSitesFilters.contains("&")) {
			String[] values = getSitesFilters.split("&");
			for (int j = 0; j < values.length; j++) {
				String[] keyPair = values[j].split("=");
				filtersMap.put(keyPair[0], keyPair[1]);
			}
		} else {
			String[] keyPair = getSitesFilters.split("=");
			filtersMap.put(keyPair[0], keyPair[1]);
		}
		System.out.println(response.getBody().asPrettyString());
		Assert.assertEquals(response.getStatusCode(), 200);
		JSONArray jsonArray = new JSONArray(response.asString());
		boolean status = false;

		if (getSitesFilters.contains("&")) {
			String[] values = getSitesFilters.split("&");
			for (int j = 0; j < values.length; j++) {
				String[] keyPair = values[j].split("=");
				filtersMap.put(keyPair[0], keyPair[1]);
			}

		} else {
			String[] keyPair = getSitesFilters.split("=");
			filtersMap.put(keyPair[0], keyPair[1]);
		}

		for (int index = 0; index < jsonArray.length(); index++) {
			if (getSitesFilters.contains("campus")) {
				Assert.assertEquals(((JSONObject) jsonArray.get(index)).optString("campus"), filtersMap.get("campus"));
				status = true;

			}
			if (getSitesFilters.contains("city")) {
				Assert.assertEquals(((JSONObject) jsonArray.get(index)).optString("city"), filtersMap.get("city"));
				status = true;
			}
			if (getSitesFilters.contains("market")) {
				Assert.assertEquals(((JSONObject) jsonArray.get(index)).optString("market"), filtersMap.get("market"));
				status = true;
			}
			if (getSitesFilters.contains("country")) {
				Assert.assertEquals(((JSONObject) jsonArray.get(index)).optString("country"), filtersMap.get("country"));
				status = true;
			}
			if (status == true) {
				log.info("Validated Sites Filters Successfully");
				logger.log(Status.PASS, "Validated Sites Filters Successfully ");
			} else {
				logger.log(Status.FAIL, "Validation is not Successfull with Sites Filters ");
				log.info("Validation is not Successfull with Sites Filters");
			}
		}
		

	}
}
