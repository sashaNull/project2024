import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class DataManagerTest {

    @Test(expected = IllegalArgumentException.class)
    public void testGetContributorNameNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.getContributorName(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContributorNameNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.getContributorName("contributor1");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContributorNameErrorStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }
        });

        dm.getContributorName("contributor1");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetContributorNameInvalidJsonResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "Invalid JSON response";
            }
        });

        dm.getContributorName("contributor1");
    }

    @Test
    public void testGetContributorNameSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":\"Contributor1\"}";
            }
        });

        String contributorName = dm.getContributorName("contributor1");
        assertEquals("Contributor1", contributorName);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAllContributorsNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.getAllContributors();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAllContributorsErrorStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }

        });

        dm.getAllContributors();
    }

    @Test(expected = IllegalStateException.class)
    public void testGetAllContributorsInvalidJsonResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "Invalid JSON response";
            }
        });

        dm.getAllContributors();
    }

    @Test
    public void testGetAllContributorsSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":[{\"_id\":\"1\",\"name\":\"Contributor1\"},{\"_id\":\"2\",\"name\":\"Contributor2\"}]}";
            }
        });

        List<Contributor> contributors = dm.getAllContributors();

        assertNotNull(contributors);
        assertEquals(2, contributors.size());
        assertEquals("Contributor1", contributors.get(0).getName());
        assertEquals("Contributor2", contributors.get(1).getName());
    }

    @Test(expected = IllegalStateException.class)
    public void testMakeDonationNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.makeDonation("fund1", "contributor1", 100);
    }

    @Test(expected = IllegalStateException.class)
    public void testMakeDonationErrorStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }

        });

        dm.makeDonation("fund1", "contributor1", 100);
    }

    @Test(expected = IllegalStateException.class)
    public void testMakeDonationInvalidJsonResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "Invalid JSON response";
            }
        });

        dm.makeDonation("fund1", "contributor1", 100);
    }

    @Test
    public void testMakeDonationSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\"}";
            }
        });

        boolean result = dm.makeDonation("fund1", "contributor1", 100);
        assertTrue(result);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetFundByIdNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.getFundById("fund1");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetFundByIdErrorStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }

        });

        dm.getFundById("fund1");
    }

    @Test(expected = IllegalStateException.class)
    public void testGetFundByIdInvalidJsonResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "Invalid JSON response";
            }
        });

        dm.getFundById("fund1");
    }

    @Test
    public void testGetFundByIdSuccess() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/findFundById")) {
                    return "{\"status\":\"success\",\"data\":{\"_id\":\"fund1\",\"name\":\"Fund1\",\"description\":\"Description1\",\"target\":1000,\"donations\":[{\"_id\":\"donation1\",\"contributor\":\"contributor1\",\"fund\":\"fund1\",\"date\":\"2024-06-28T01:13:45.698Z\",\"amount\":100}]}}";
                } else if (resource.equals("/findContributorNameById")) {
                    return "{\"status\":\"success\",\"data\":\"Contributor1\"}";
                }
                return null;
            }
        });

        Fund fund = dm.getFundById("fund1");

        assertNotNull(fund);
        assertEquals("Fund1", fund.getName());
        assertEquals(1000, fund.getTarget());
        assertEquals(1, fund.getDonations().size());
        assertEquals("Contributor1", fund.getDonations().get(0).getContributorName());
    }

    @Test
    public void testGetFundByIdTargetNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                if (resource.equals("/findFundById")) {
                    return "{\"status\":\"success\",\"data\":{\"_id\":\"fund1\",\"name\":\"Fund1\",\"description\":\"Description1\",\"target\":null,\"donations\":[{\"_id\":\"donation1\",\"contributor\":\"contributor1\",\"fund\":\"fund1\",\"date\":\"2024-06-28T01:13:45.698Z\",\"amount\":100}]}}";
                } else if (resource.equals("/findContributorNameById")) {
                    return "{\"status\":\"success\",\"data\":\"Contributor1\"}";
                }
                return null;
            }
        });

        Fund fund = dm.getFundById("fund1");

        assertNotNull(fund);
        assertEquals("Fund1", fund.getName());
        assertEquals(0, fund.getTarget()); // Check that target is set to 0 when null
        assertEquals(1, fund.getDonations().size());
        assertEquals("Contributor1", fund.getDonations().get(0).getContributorName());
    }
}
