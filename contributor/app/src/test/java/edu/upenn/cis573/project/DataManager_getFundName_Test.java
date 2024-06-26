package edu.upenn.cis573.project;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;

public class DataManager_getFundName_Test {

    @Test
    public void testSuccess() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":\"Snoopy\"}";
            }
        });

        String name = dm.getFundName("12345");
        assertNotNull(name);
        assertEquals("Snoopy", name);
    }

    @Test
    public void testFailure() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"failure\"}";
            }
        });

        String name = dm.getFundName("12345");
        assertNotNull(name);
        assertEquals("Unknown Fund", name);
    }

    @Test
    public void testException() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Exception");
            }
        });

        String name = dm.getFundName("12345");
        assertNull(name);
    }

    @Test
    public void testInvalidJson() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "invalid json";
            }
        });

        String name = dm.getFundName("12345");
        assertNull(name);
    }
}