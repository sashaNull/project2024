import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManagerRobustnessTest {
	
	
	private DataManager dm;

	/*
	 * Tests for attemptLogin
	 */
	

	@Test(expected=IllegalStateException.class)
	public void testAttemptLogin_WebClientIsNull() {

		dm = new DataManager(null);
		dm.attemptLogin("login", "password");
		fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient is null");
		
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAttemptLogin_LoginIsNull() {

		dm = new DataManager(new WebClient("localhost", 3001));
		dm.attemptLogin(null, "password");
		fail("DataManager.attemptLogin does not throw IllegalArgumentxception when login is null");
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAttemptLogin_PasswordIsNull() {

		dm = new DataManager(new WebClient("localhost", 3001));
		dm.attemptLogin("login", null);
		fail("DataManager.attemptLogin does not throw IllegalArgumentxception when password is null");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testAttemptLogin_WebClientCannotConnectToServer() {

		// this assumes no server is running on port 3002
		dm = new DataManager(new WebClient("localhost", 3002));
		dm.attemptLogin("login", "password");
		fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient cannot connect to server");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testAttemptLogin_WebClientReturnsNull() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}
		});
		dm.attemptLogin("login", "password");
		fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient returns null");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testAttemptLogin_WebClientReturnsError() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}";
			}
		});
		dm.attemptLogin("login", "password");
		fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient returns error");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testAttemptLogin_WebClientReturnsMalformedJSON() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "I AM NOT JSON!";
			}
		});
		dm.attemptLogin("login", "password");
		fail("DataManager.attemptLogin does not throw IllegalStateException when WebClient returns malformed JSON");
		
	}
	
	/*
	 * Tests for getContributorName
	 */
	
	
	@Test(expected=IllegalStateException.class)
	public void testGetContributorName_WebClientIsNull() {

		dm = new DataManager(null);
		dm.getContributorName("id");
		fail("DataManager.getContributorName does not throw IllegalStateException when WebClient is null");
		
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetContributorName_IdIsNull() {

		dm = new DataManager(new WebClient("localhost", 3001));
		dm.getContributorName(null);
		fail("DataManager.getContributorName does not throw IllegalArgumentxception when id is null");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testGetContributorName_WebClientCannotConnectToServer() {

		// this assumes no server is running on port 3002
		dm = new DataManager(new WebClient("localhost", 3002));
		dm.getContributorName("id");
		fail("DataManager.getContributorName does not throw IllegalStateException when WebClient cannot connect to server");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testGetContributorName_WebClientReturnsNull() {

		dm = new DataManager(new WebClient("locahost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}
		});
		dm.getContributorName("id");
		fail("DataManager.getContributorName does not throw IllegalStateException when WebClient returns null");
		
	}
	
	
	@Test(expected=IllegalStateException.class)
	public void testGetContributorName_WebClientReturnsError() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}";
			}
		});
		dm.getContributorName("id");
		fail("DataManager.getContributorName does not throw IllegalStateException when WebClient returns error");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testGetContributorName_WebClientReturnsMalformedJSON() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "I AM NOT JSON!";
			}
		});
		dm.getContributorName("id");
		fail("DataManager.getContributorName does not throw IllegalStateException when WebClient returns malformed JSON");
		
	}

	
	/*
	 * Tests for createFund
	 */
	

	@Test(expected=IllegalStateException.class)
	public void testCreateFund_WebClientIsNull() {

		dm = new DataManager(null);
		dm.createFund("orgId", "name", "description", 100);
		fail("DataManager.createFund does not throw IllegalStateException when WebClient is null");
		
	}

	@Test(expected=IllegalArgumentException.class)
	public void testCreateFund_OrgIdIsNull() {

		dm = new DataManager(new WebClient("localhost", 3001));
		dm.createFund(null, "name", "description", 100);
		fail("DataManager.createFund does not throw IllegalArgumentxception when orgId is null");
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateFund_NameIsNull() {

		dm = new DataManager(new WebClient("localhost", 3001));
		dm.createFund("orgId", null, "description", 100);
		fail("DataManager.createFund does not throw IllegalArgumentxception when name is null");
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreateFund_DescriptionIsNull() {

		dm = new DataManager(new WebClient("localhost", 3001));
		dm.createFund("orgId", "name", null, 100);
		fail("DataManager.createFund does not throw IllegalArgumentxception when description is null");
		
	}

	@Test(expected=IllegalStateException.class)
	public void testCreateFund_WebClientCannotConnectToServer() {

		// this assumes no server is running on port 3002
		dm = new DataManager(new WebClient("localhost", 3002));
		dm.createFund("orgId", "name", "description", 100);
		fail("DataManager.createFund does not throw IllegalStateException when WebClient cannot connect to server");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testCreateFund_WebClientReturnsNull() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;
			}
		});
		dm.createFund("orgId", "name", "description", 100);
		fail("DataManager.createFund does not throw IllegalStateException when WebClient returns null");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testCreateFund_WebClientReturnsError() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"error\",\"error\":\"An unexpected database error occurred\"}";
			}
		});
		dm.createFund("orgId", "name", "description", 100);
		fail("DataManager.createFund does not throw IllegalStateException when WebClient returns error");
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testCreateFund_WebClientReturnsMalformedJSON() {

		dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "I AM NOT JSON!";
			}
		});
		dm.createFund("orgId", "name", "description", 100);
		fail("DataManager.createFund does not throw IllegalStateException when WebClient returns malformed JSON");
		
	}

}
