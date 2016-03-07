package com.spring.company.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.company.CompanyApplication;
import com.spring.company.model.Company;
import com.spring.company.repository.CompanyRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CompanyApplication.class)
@WebIntegrationTest
public class CompanyControllerTest {

	// Required to Generate JSON content from Java objects
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	// Required to delete the data added for tests.
	// Directly invoke the APIs interacting with the DB
	@Autowired
	private CompanyRepository companyRepository;

	// Test RestTemplate to invoke the APIs.
	private RestTemplate restTemplate = new TestRestTemplate();

	@Test
	public void testCreateompanyApi() throws JsonProcessingException {

		// Building the Request body data
		Map<String, Object> requestBody = new HashMap<String, Object>();
		String companyName = "Hemadri Software";
		requestBody.put("name", companyName);
		String addresss = "KRPuram";
		requestBody.put("adress", addresss);
		String city = "Bangalore";
		requestBody.put("city", city);
		requestBody.put("country", "India");
		requestBody.put("phoneNumber", "9880440671");
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		HttpEntity<String> httpEntity = new HttpEntity<String>(
				OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);

		// Invoking the API
		@SuppressWarnings("unchecked")
		Map<String, Object> apiResponse = restTemplate.postForObject(
				"http://localhost:8888/companies/add/", httpEntity, Map.class,
				Collections.EMPTY_MAP);

		assertNotNull(apiResponse);

		// Asserting the response of the API.
		// String message = apiResponse.get("message").toString();
		assertEquals(city, apiResponse.get("city"));
		Integer companyId = (Integer) apiResponse.get("companyId");

		assertNotNull(companyId);

		// Fetching the company details directly from the DB to verify the API
		// succeeded
		Company companyFromDb = companyRepository.findOne(companyId);
		assertEquals(companyName, companyFromDb.getName());
		assertEquals(addresss, companyFromDb.getAdress());
		assertEquals(city, companyFromDb.getCity());

		// Delete the data added for testing
		companyRepository.delete(companyId);

	}

	@Test
	public void testGetcompanyDetailsApi() {
		// Create a new company using the companyRepository API
		Company company = new Company("DataSolutions", "Marthahalli",
				"Bangalore", "India", "9880440671");
		companyRepository.save(company);

		Integer companyId = company.getCompanyId();

		// Now make a call to the API to get details of the company
		Company apiResponse = restTemplate.getForObject(
				"http://localhost:8888/companies/" + companyId, Company.class);

		// Verify that the data from the API and data saved in the DB are same
		assertNotNull(apiResponse);
		assertEquals(company.getName(), apiResponse.getName());
		assertEquals(company.getCompanyId(), apiResponse.getCompanyId());
		assertEquals(company.getAdress(), apiResponse.getAdress());
		assertEquals(company.getCountry(), apiResponse.getCountry());

		// Delete the Test data created
		companyRepository.delete(apiResponse.getCompanyId());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdatecompanyDetails() throws JsonProcessingException {
		// Create a new company using the companyRepository API
		Company company = new Company("DataSolutions", "Marthahalli",
				"Bangalore", "India", "9880440671");
		company = companyRepository.save(company);

		Integer companyId = company.getCompanyId();

		// Now create Request body with the updated company Data.
		Map<String, Object> requestBody = new HashMap<String, Object>();
		requestBody.put("name", "DataSolutions");
		requestBody.put("adress", "JCRoad");
		requestBody.put("city", "Chennai");
		requestBody.put("country", "India");
		requestBody.put("phoneNumber", "9456788999");
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		// Creating http entity object with request body and headers
		HttpEntity<String> httpEntity = new HttpEntity<String>(
				OBJECT_MAPPER.writeValueAsString(requestBody), requestHeaders);

		// Invoking the API
		Map<String, Object> apiResponse = (Map<String, Object>) restTemplate
				.exchange(
						"http://localhost:8888/companies/update/" + companyId,
						HttpMethod.PUT, httpEntity, Map.class,
						Collections.EMPTY_MAP).getBody();

		assertNotNull(apiResponse);
		assertTrue(!apiResponse.isEmpty());

		// Asserting the response of the API.
		String message = apiResponse.get("adress").toString();
		assertEquals("JCRoad", message);

		// Fetching the Company details directly from the DB to verify the API
		// succeeded in updating the company details
		Company companyFromDb = companyRepository.findOne(companyId);
		assertEquals(requestBody.get("name"), companyFromDb.getName());
		assertEquals(requestBody.get("adress"), "JCRoad");
		assertEquals(requestBody.get("city"), "Chennai");

		// Delete the data added for testing
		companyRepository.delete(companyFromDb.getCompanyId());

	}

	@Test
	public void testDeleteCompanyApi() {
		// Create a new company using the companyRepository API
		Company company = new Company("DataSolutionsPvtLtd",
				"Marthahalli road", "Bangalore", "India", "9880440671");
		companyRepository.save(company);

		Integer companyId = company.getCompanyId();

		// Now Invoke the API to delete the Company
		restTemplate.delete("http://localhost:8888/companies/delete/"
				+ companyId);

		// Try to fetch from the DB directly
		Company companyFromDb = companyRepository.findOne(companyId);
		// and assert that there is no data found
		assertNull(companyFromDb);
	}

	@Test
	public void testGetAllCompaniesApi() {
		// Add some test data for the API
		Company company = new Company("DataSolutions1", "Marthahalli1",
				"Bangalore", "India", "9880440671");
		company = companyRepository.save(company);

		Company company1 = new Company("DataSolutions2", "Marthahalli2",
				"Chennai", "India", "9882440671");
		company1 = companyRepository.save(company1);

		// Invoke the API
		Company[] apiResponse = restTemplate.getForObject(
				"http://localhost:8888/companies/", Company[].class);

		assertEquals(2, apiResponse.length);

		// Delete the test data created
		companyRepository.delete(company.getCompanyId());
		companyRepository.delete(company1.getCompanyId());
	}

}
