import static org.junit.Assert.*;

import java.util.Map;
import org.junit.Test;

public class DataManager_getContributorName_Test {

	@Test
	public void testSuccessfulContributorLookup() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":\"John Doe\"}";
			}
		});
		
		String name = dm.getContributorName("12345");
		
		assertNotNull(name);
		assertEquals("John Doe", name);
	}
	
	@Test
	public void testContributorNotFound() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"message\":\"Contributor not found\"}";
			}
		});
		
		String name = dm.getContributorName("invalidId");
		
		assertNull(name);
	}

	@Test
	public void testServerError() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"message\":\"Server error\"}";
			}
		});
		
		String name = dm.getContributorName("12345");
		
		assertNull(name);
	}

	@Test
	public void testNetworkFailure() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				throw new RuntimeException("Network failure");
			}
		});
		
		String name = dm.getContributorName("12345");
		
		assertNull(name);
	}

	@Test
	public void testInvalidJsonResponse() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "Invalid JSON response";
			}
		});
		
		String name = dm.getContributorName("12345");
		
		assertNull(name);
	}
}