import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

public class DataManager_createOrganization_Test {

    @Test(expected = IllegalArgumentException.class)
    public void testLoginNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization(null, "a", "b", "c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPasswordNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("a", null, "b", "c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("b", "a", null, "c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDescriptionNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("d", "a", "b", null);
    }

    @Test(expected = IllegalStateException.class)
    public void testNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.createOrganization("upenn", "123456", "University Of Pennsylvania", "An Ivy League school.");
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }

        });
        dm.createOrganization("upenn", "123456", "University Of Pennsylvania", "An Ivy League school.");
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidJsonResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "Invalid JSON response";
            }
        });

        dm.createOrganization("upenn", "123456", "University Of Pennsylvania", "An Ivy League school.");
    }

    @Test
    public void testSuccessfulCreation() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001){
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\": \"success\", \"data\": {\"_id\": \"667cb7b72b45b3544ce5f950\", \"login\": \"upenn\", \"password\": \"123456\", \"name\": \"University Of Pennsylvania\", \"description\": \"An Ivy League school.\", \"funds\": [], \"__v\": 0}}";
            }
        });
        
        Organization newOrg = dm.createOrganization("upenn", "123456", "University Of Pennsylvania", "An Ivy League school.");
        assertEquals("667cb7b72b45b3544ce5f950", newOrg.getId());
        assertEquals("University Of Pennsylvania", newOrg.getName());
        assertEquals("An Ivy League school.", newOrg.getDescription());
        assertEquals(Collections.EMPTY_LIST, newOrg.getFunds());

    }

}
