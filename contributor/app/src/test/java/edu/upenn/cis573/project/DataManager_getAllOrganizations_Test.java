package edu.upenn.cis573.project;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import java.util.Map;

public class DataManager_getAllOrganizations_Test {

    @Test
    public void testGetAllOrganizationsSuccess() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"success\",\"data\":[{\"_id\":\"org1\",\"name\":\"Org 1\",\"funds\":[{\"_id\":\"fund1\",\"name\":\"Fund 1\",\"target\":1000,\"totalDonations\":500},{\"_id\":\"fund2\",\"name\":\"Fund 2\",\"target\":2000,\"totalDonations\":1500}]}]}";
            }
        });

        List<Organization> organizations = dm.getAllOrganizations();
        assertNotNull(organizations);
        assertEquals(1, organizations.size());

        Organization org = organizations.get(0);
        assertEquals("org1", org.getId());
        assertEquals("Org 1", org.getName());

        List<Fund> funds = org.getFunds();
        assertNotNull(funds);
        assertEquals(2, funds.size());

        Fund fund1 = funds.get(0);
        assertEquals("fund1", fund1.getId());
        assertEquals("Fund 1", fund1.getName());
        assertEquals(1000, fund1.getTarget());
        assertEquals(500, fund1.getTotalDonations());

        Fund fund2 = funds.get(1);
        assertEquals("fund2", fund2.getId());
        assertEquals("Fund 2", fund2.getName());
        assertEquals(2000, fund2.getTarget());
        assertEquals(1500, fund2.getTotalDonations());
    }

    @Test
    public void testGetAllOrganizationsFailure() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"failure\"}";
            }
        });

        List<Organization> organizations = dm.getAllOrganizations();
        assertNull(organizations);
    }

    @Test
    public void testGetAllOrganizationsException() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                throw new RuntimeException("Exception");
            }
        });

        List<Organization> organizations = dm.getAllOrganizations();
        assertNull(organizations);
    }

    @Test
    public void testGetAllOrganizationsInvalidJson() {
        DataManager dm = new DataManager(new WebClient(null, 0) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "invalid json";
            }
        });

        List<Organization> organizations = dm.getAllOrganizations();
        assertNull(organizations);
    }
}