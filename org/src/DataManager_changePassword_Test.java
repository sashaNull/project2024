import static org.junit.Assert.*;

import java.util.Map;
import org.junit.Test;

public class DataManager_changePassword_Test {

    @Test(expected = IllegalArgumentException.class)
    public void testOrgIdNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.changePassword(null, "newPassword");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewPasswordNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.changePassword("orgId", null);
    }

    @Test(expected = IllegalStateException.class)
    public void testNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.changePassword("orgId", "newPassword");
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }
        });

        dm.changePassword("orgId", "newPassword");
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidJsonResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "Invalid JSON response";
            }
        });

        dm.changePassword("orgId", "newPassword");
    }

    @Test
    public void testChangeFailed() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"change failed\"}";
            }
        });

        boolean result = dm.changePassword("orgId", "newPassword");
        assertFalse(result);
    }

    @Test
    public void testSuccessfulChange() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\": \"success\", \"data\": {\"_id\": \"667cb7b72b45b3544ce5f950\", \"password\": \"newPassword\"}}";
            }
        });

        boolean result = dm.changePassword("orgId", "newPassword");
        assertTrue(result);
    }
}
