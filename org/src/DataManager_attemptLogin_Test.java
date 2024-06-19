import static org.junit.Assert.*;

import java.util.Map;
import java.util.List;

import org.hamcrest.core.IsCollectionContaining;
import org.junit.Test;

public class DataManager_attemptLogin_Test {

	@Test
	public void testSuccessfulLogin() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"Test Org\",\"description\":\"Test Organization\",\"funds\":[]}}";
			}
		});
		
		Organization org = dm.attemptLogin("testLogin", "testPassword");
		
		assertNotNull(org);
		assertEquals("12345", org.getId());
		assertEquals("Test Org", org.getName());
		assertEquals("Test Organization", org.getDescription());
		assertTrue(org.getFunds().isEmpty());
	}
	
	@Test
	public void testLoginWithFunds() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"Test Org\",\"description\":\"Test Organization\",\"funds\":[{\"_id\":\"fund1\",\"name\":\"Fund 1\",\"description\":\"Description 1\",\"target\":1000,\"donations\":[]}]}}";
			}
		});
		
		Organization org = dm.attemptLogin("testLogin", "testPassword");
		
		assertNotNull(org);
		assertEquals("12345", org.getId());
		assertEquals("Test Org", org.getName());
		assertEquals("Test Organization", org.getDescription());
		
		List<Fund> funds = org.getFunds();
		assertEquals(1, funds.size());
		
		Fund fund = funds.get(0);
		assertEquals("fund1", fund.getId());
		assertEquals("Fund 1", fund.getName());
		assertEquals("Description 1", fund.getDescription());
		assertEquals(1000, fund.getTarget());
		assertTrue(fund.getDonations().isEmpty());
	}
	
	@Test
	public void testLoginWithFundsAndDonations() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"Test Org\",\"description\":\"Test Organization\",\"funds\":[{\"_id\":\"fund1\",\"name\":\"Fund 1\",\"description\":\"Description 1\",\"target\":1000,\"donations\":[{\"contributor\":\"contrib1\",\"amount\":500,\"date\":\"2023-06-01\"}]}]}}";
			}
		}) {
			@Override
			public String getContributorName(String id) {
				return "John Doe";
			}
		};
		
		Organization org = dm.attemptLogin("testLogin", "testPassword");
		
		assertNotNull(org);
		assertEquals("12345", org.getId());
		assertEquals("Test Org", org.getName());
		assertEquals("Test Organization", org.getDescription());
		
		List<Fund> funds = org.getFunds();
		assertEquals(1, funds.size());
		
		Fund fund = funds.get(0);
		assertEquals("fund1", fund.getId());
		assertEquals("Fund 1", fund.getName());
		assertEquals("Description 1", fund.getDescription());
		assertEquals(1000, fund.getTarget());
		
		List<Donation> donations = fund.getDonations();
		assertEquals(1, donations.size());
		
		Donation donation = donations.get(0);
		assertEquals("fund1", donation.getFundId());
		assertEquals("John Doe", donation.getContributorName());
		assertEquals(500, donation.getAmount());
		assertEquals("2023-06-01", donation.getDate());
	}
	
	@Test
	public void testInvalidLogin() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"message\":\"Invalid login\"}";
			}
		});
		
		Organization org = dm.attemptLogin("invalidLogin", "invalidPassword");
		
		assertNull(org);
	}

	@Test
	public void testServerError() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"message\":\"Server error\"}";
			}
		});
		
		Organization org = dm.attemptLogin("testLogin", "testPassword");
		
		assertNull(org);
	}

	@Test(expected = IllegalStateException.class)
	public void testNetworkFailure() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				throw new RuntimeException("Network failure");
			}
		});
		
		Organization org = dm.attemptLogin("testLogin", "testPassword");
		
		assertNull(org);
	}

	@Test(expected = IllegalStateException.class)
	public void testInvalidJsonResponse() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "Invalid JSON response";
			}
		});
		
		Organization org = dm.attemptLogin("testLogin", "testPassword");
		
		assertNull(org);
	}
}
