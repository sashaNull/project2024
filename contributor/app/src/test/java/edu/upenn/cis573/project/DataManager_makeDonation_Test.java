package edu.upenn.cis573.project;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;

public class DataManager_makeDonation_Test {

    @Test
    public void testMakeDonationSuccess() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\"}";
            }
        });

        boolean result = dm.makeDonation("contrib1", "fund1", "100");
        assertTrue(result);
    }

    @Test
    public void testMakeDonationFailure() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"failure\"}";
            }
        });

        boolean result = dm.makeDonation("contrib1", "fund1", "100");
        assertFalse(result);
    }

    @Test
    public void testMakeDonationException() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Exception");
            }
        });

        boolean result = dm.makeDonation("contrib1", "fund1", "100");
        assertFalse(result);
    }

    @Test
    public void testMakeDonationInvalidJson() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "invalid json";
            }
        });

        boolean result = dm.makeDonation("contrib1", "fund1", "100");
        assertFalse(result);
    }
}
