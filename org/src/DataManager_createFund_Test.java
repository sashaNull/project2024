import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_createFund_Test {
	
	@Test
	public void testSuccessfulCreation() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"12345\",\"name\":\"new fund\",\"description\":\"this is the new fund\",\"target\":10000,\"org\":\"5678\",\"donations\":[],\"__v\":0}}";
			}
		});
		
		Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);
		
		assertNotNull(f);
		assertEquals("12345", f.getId());
		assertEquals("new fund", f.getName());
		assertEquals("this is the new fund", f.getDescription());
		assertEquals(10000, f.getTarget());
	}
	
	@Test
	public void testCreationWithInvalidData() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"message\":\"Invalid data\"}";
			}
		});
		
		Fund f = dm.createFund("", "new fund", "this is the new fund", 10000);
		
		assertNull(f);
	}

	@Test
	public void testServerError() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"message\":\"Server error\"}";
			}
		});
		
		Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);
		
		assertNull(f);
	}

	@Test
	public void testNetworkFailure() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				throw new RuntimeException("Network failure");
			}
		});
		
		Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);
		
		assertNull(f);
	}

	@Test
	public void testEmptyFundName() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"message\":\"Fund name cannot be empty\"}";
			}
		});
		
		Fund f = dm.createFund("12345", "", "this is the new fund", 10000);
		
		assertNull(f);
	}

	@Test
	public void testNegativeTargetAmount() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"message\":\"Target amount must be positive\"}";
			}
		});
		
		Fund f = dm.createFund("12345", "new fund", "this is the new fund", -100);
		
		assertNull(f);
	}
	
	@Test
	public void testNullResponse() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}
		});
		
		Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);
		
		assertNull(f);
	}
	
	@Test
	public void testInvalidJsonResponse() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "Invalid JSON response";
			}
		});
		
		Fund f = dm.createFund("12345", "new fund", "this is the new fund", 10000);
		
		assertNull(f);
	}
}
