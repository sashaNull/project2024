package edu.upenn.cis573.project;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;

public class DataManager_attemptLogin_Test {

    @Test
    public void testAttemptLoginSuccess() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"123\",\"name\":\"John Doe\",\"email\":\"john@upenn.edu\",\"creditCardNumber\":\"1234-5678-9012-3456\",\"creditCardCVV\":\"123\",\"creditCardExpiryMonth\":12,\"creditCardExpiryYear\":2025,\"creditCardPostCode\":\"12345\",\"donations\":[]}}";
            }
        });

        Contributor contributor = dm.attemptLogin("john", "password");
        assertNotNull(contributor);
        assertEquals("John Doe", contributor.getName());
        assertEquals("john@upenn.edu", contributor.getEmail());
    }

    @Test
    public void testAttemptLoginFailure() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"failure\"}";
            }
        });

        Contributor contributor = dm.attemptLogin("john", "password");
        assertNull(contributor);
    }

    @Test(expected = RuntimeException.class)
    public void testAttemptLoginException() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Exception");
            }
        });

        dm.attemptLogin("john", "password");
    }

    @Test
    public void testAttemptLoginInvalidJson() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "invalid json";
            }
        });

        Contributor contributor = dm.attemptLogin("john", "password");
        assertNull(contributor);
    }

    @Test
    public void testAttemptLoginWithDonations() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":{\"_id\":\"123\",\"name\":\"John Doe\",\"email\":\"john@upenn.edu\",\"creditCardNumber\":\"1234-5678-9012-3456\",\"creditCardCVV\":\"123\",\"creditCardExpiryMonth\":12,\"creditCardExpiryYear\":2025,\"creditCardPostCode\":\"12345\",\"donations\":[{\"fund\":\"Fund A\",\"date\":\"2024-01-01\",\"amount\":100},{\"fund\":\"Fund B\",\"date\":\"2024-02-01\",\"amount\":200}]}}";
            }
        }) {
            @Override
            public String getFundName(String id) {
                if (id.equals("Fund A")) {
                    return "Fund A";
                } else if (id.equals("Fund B")) {
                    return "Fund B";
                } else {
                    return "Unknown Fund";
                }
            }
        };

        Contributor contributor = dm.attemptLogin("john", "password");
        assertNotNull(contributor);
        assertEquals("John Doe", contributor.getName());
        assertEquals("john@upenn.edu", contributor.getEmail());
        assertNotNull(contributor.getDonations());
        assertEquals(2, contributor.getDonations().size());

        Donation donation1 = contributor.getDonations().get(0);
        assertEquals("Fund A", donation1.getFundName());
        assertEquals("John Doe", donation1.getContributorName());
        assertEquals(100, donation1.getAmount());
        assertEquals("2024-01-01", donation1.getDate());

        Donation donation2 = contributor.getDonations().get(1);
        assertEquals("Fund B", donation2.getFundName());
        assertEquals("John Doe", donation2.getContributorName());
        assertEquals(200, donation2.getAmount());
        assertEquals("2024-02-01", donation2.getDate());
    }
}
