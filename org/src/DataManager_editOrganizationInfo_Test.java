import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_editOrganizationInfo_Test {

    @Test(expected = IllegalArgumentException.class)
    public void testOrgIdNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.editOrganizationInfo(null, "newName", "newDescription");
    }

    @Test(expected = IllegalStateException.class)
    public void testNewNameEmpty() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.editOrganizationInfo("orgId", "newName", "newDescription");
    }


    @Test(expected = IllegalStateException.class)
    public void testNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.editOrganizationInfo("orgId", "newName", "newDescription");
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }
        });

        dm.editOrganizationInfo("orgId", "newName", "newDescription");
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidJsonResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "Invalid JSON response";
            }
        });

        dm.editOrganizationInfo("orgId", "newName", "newDescription");
    }

    @Test
    public void testUpdateFailed() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"update failed\"}";
            }
        });

        boolean result = dm.editOrganizationInfo("orgId", "newName", "newDescription");
        assertFalse(result);
    }

    @Test
    public void testSuccessfulUpdate() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\": \"success\", \"data\": {\"_id\": \"667cb7b72b45b3544ce5f950\", \"name\": \"newName\", \"description\": \"newDescription\"}}";
            }
        });

        boolean result = dm.editOrganizationInfo("orgId", "newName", "newDescription");
        assertTrue(result);
    }
}
