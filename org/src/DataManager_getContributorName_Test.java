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

	@Test(expected = IllegalStateException.class)
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

	@Test(expected = IllegalStateException.class)
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

	@Test(expected = IllegalStateException.class)
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

	@Test(expected = IllegalStateException.class)
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

	@Test
	public void testSuccessfulCaching() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			int cache = 0;

			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				if (cache == 0){
					cache += 1;
					return "{\"status\":\"success\",\"data\":\"John Doe\"}";
				}
				else
				{
					return "";
				}
			}
		});

		String name = dm.getContributorName("12345");
		assertEquals("John Doe", name);

		name = dm.getContributorName("12345");
		assertEquals("John Doe", name);
	}
}