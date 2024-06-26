import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DataManager {

    private final WebClient client;
    private Map<String, String> contributorNameCache;

    public DataManager(WebClient client) {
        if (client == null) {
            throw new IllegalStateException("WebClient cannot be null");
        }
        this.client = client;
        this.contributorNameCache = new HashMap<>();
    }

    /**
     * Attempt to log the user into an Organization account using the login and
     * password.
     * This method uses the /findOrgByLoginAndPassword endpoint in the API
     * 
     * @return an Organization object if successful; null if unsuccessful
     */
    public Organization attemptLogin(String login, String password) {
        if (login == null) {
            throw new IllegalArgumentException("Login cannot be null");
        }

        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("login", login);
            map.put("password", password);
            String response = client.makeRequest("/findOrgByLoginAndPassword", map);

            if (response == null) {
                throw new IllegalStateException("WebClient returned null");
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if ("success".equals(status)) {
                JSONObject data = (JSONObject) json.get("data");
                if (data == null)
                {
                    return null;
                }
                String fundId = (String) data.get("_id");
                String name = (String) data.get("name");
                String description = (String) data.get("description");
                Organization org = new Organization(fundId, name, login, description);

                JSONArray funds = (JSONArray) data.get("funds");
                Iterator<?> it = funds.iterator();
                while (it.hasNext()) {
                    JSONObject fund = (JSONObject) it.next();
                    fundId = (String) fund.get("_id");
                    name = (String) fund.get("name");
                    description = (String) fund.get("description");
                    Long targetLong = (Long) fund.get("target");
                    long target = targetLong != null ? targetLong : 0;

                    Fund newFund = new Fund(fundId, name, description, target);

                    JSONArray donations = (JSONArray) fund.get("donations");
                    List<Donation> donationList = new LinkedList<>();
                    Iterator<?> it2 = donations.iterator();
                    while (it2.hasNext()) {
                        JSONObject donation = (JSONObject) it2.next();
                        String contributorId = (String) donation.get("contributor");
                        String contributorName = this.getContributorName(contributorId);
                        Long amountLong = (Long) donation.get("amount");
                        long amount = amountLong != null ? amountLong : 0;
                        String date = (String) donation.get("date");
                        donationList.add(new Donation(fundId, contributorName, amount, date));
                    }

                    newFund.setDonations(donationList);
                    org.addFund(newFund);
                }

                return org;
            } else {
                throw new IllegalStateException("WebClient returned error status: " + status);
            }
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to parse JSON response", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }

    public List<Contributor> getAllContributors() {
        List<Contributor> contributors = new ArrayList<>();
        try {
            String response = client.makeRequest("/allContributors", Map.of());
            System.out.println("Response from /allContributors: " + response);

            if (response == null) {
                throw new IllegalStateException("WebClient returned null");
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if ("success".equals(status)) {
                JSONArray data = (JSONArray) json.get("data");
                for (Object obj : data) {
                    JSONObject contributorJson = (JSONObject) obj;
                    String id = (String) contributorJson.get("_id");
                    String name = (String) contributorJson.get("name");
                    contributors.add(new Contributor(id, name));
                }
            } else {
                throw new IllegalStateException("Error status returned from server");
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch contributors: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Error in communicating with server", e);
        }
        return contributors;
    }

    public boolean makeDonation(String fundId, String contributorId, long amount) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("fund", fundId);
            map.put("contributor", contributorId);
            map.put("amount", amount);
            String response = client.makeRequest("/makeDonation", map);
            System.out.println("Response from /makeDonation: " + response);

            if (response == null) {
                throw new IllegalStateException("WebClient returned null");
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if ("success".equals(status)) {
                return true;
            } else {
                throw new IllegalStateException("Error status returned from server");
            }
        } catch (Exception e) {
            System.err.println("Failed to make donation: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }

    public Fund getFundById(String fundId) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", fundId);
            String response = client.makeRequest("/findFundById", map);
            System.out.println("Response from /findFundById: " + response);

            if (response == null) {
                throw new IllegalStateException("WebClient returned null");
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if ("success".equals(status)) {
                JSONObject data = (JSONObject) json.get("data");
                String id = (String) data.get("_id");
                String name = (String) data.get("name");
                String description = (String) data.get("description");

                // Handle null target
                Long targetLong = (Long) data.get("target");
                long target = targetLong != null ? targetLong : 0;

                Fund fund = new Fund(id, name, description, target);

                JSONArray donations = (JSONArray) data.get("donations");
                List<Donation> donationList = new ArrayList<>();
                for (Object obj : donations) {
                    JSONObject donationJson = (JSONObject) obj;
                    String contributorId = (String) donationJson.get("contributor");
                    String contributorName = this.getContributorName(contributorId);
                    long amount = (Long) donationJson.get("amount");
                    String date = (String) donationJson.get("date");

                    Donation donation = new Donation(id, contributorName, amount, date);
                    donationList.add(donation);
                }
                fund.setDonations(donationList);
                return fund;
            } else {
                throw new IllegalStateException("Error status returned from server");
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch fund: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }

    public String getContributorName(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Contributor ID cannot be null");
        }

        // Cache to avoid redundant network calls
        if (contributorNameCache.containsKey(id)) {
            return contributorNameCache.get(id);
        }

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            String response = client.makeRequest("/findContributorNameById", map);

            if (response == null) {
                throw new IllegalStateException("WebClient returned null");
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if ("success".equals(status)) {
                String name = (String) json.get("data");
                contributorNameCache.put(id, name); // Add to cache
                return name;
            } else {
                throw new IllegalStateException("Error status returned from server");
            }
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to parse JSON response", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }
    
    /**
     * This method creates a new fund in the database using the /createFund endpoint
     * in the API
     * 
     * @return a new Fund object if successful; null if unsuccessful
     */
    public Fund createFund(String orgId, String name, String description, long target) {
        if (orgId == null) {
            throw new IllegalArgumentException("Organization ID cannot be null");
        }
        if (name == null) {
            throw new IllegalArgumentException("Fund name cannot be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("Fund description cannot be null");
        }

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("orgId", orgId);
            map.put("name", name);
            map.put("description", description);
            map.put("target", target);
            String response = client.makeRequest("/createFund", map);

            if (response == null) {
                throw new IllegalStateException("WebClient returned null");
            }

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if ("success".equals(status)) {
                JSONObject fund = (JSONObject) json.get("data");
                String fundId = (String) fund.get("_id");
                return new Fund(fundId, name, description, target);
            } else {
                throw new IllegalStateException("WebClient returned error status: " + status);
            }
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to parse JSON response", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }
    
    public boolean isUsernameTaken(String login) {
        if (login == null) {
            throw new IllegalArgumentException("Login cannot be null");
        }

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("login", login);
            String response = client.makeRequest("/checkUsername", map);

            if (response == null) {
                throw new IllegalStateException("WebClient returned null");
            }
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");

            if ("taken".equals(status)) {
                return true;
            } 
            else if ("available".equals(status)) {
                return false;
            }
            else {
                throw new IllegalStateException("WebClient returned error status: " + status);
            }
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to parse JSON response", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }

    /**
     * Create a new organization with the provided details.
     * This method uses the /createOrg endpoint in the API.
     *
     * @return an Organization object if the creation is successful;
     */    
    public Organization createOrganization(String login, String password, String name, String description)
    {
        if (login == null || password == null || name == null || description == null) {
            throw new IllegalArgumentException("Login/ password/ name/ description cannot be null");
        }
        
        try{
           Map<String, Object> map = new HashMap<>(); 
           map.put("login", login);
           map.put("password", password);
           map.put("name", name);
           map.put("description", description);
           String response = client.makeRequest("/createOrg", map);
           
           if (response == null) {
                throw new IllegalStateException("WebClient returned null");
            }
        
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");
            if ("success".equals(status)) {
                JSONObject data = (JSONObject) json.get("data");
                String orgId = (String) data.get("_id");
                return new Organization(orgId, name, login, description);
            } else {
                throw new IllegalStateException("WebClient returned error status: " + status);
            }
            
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to parse JSON response", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }

    }
    
    public boolean changePassword (String orgId, String newPassword)
    {
        if (orgId == null || newPassword == null) {
            throw new IllegalArgumentException("OrgID/ New Password cannot be null");
        }
        
        try{
           Map<String, Object> map = new HashMap<>(); 
           map.put("id", orgId);
           map.put("password", newPassword);
           String response = client.makeRequest("/changePassword", map);
           
           if (response == null) {
                throw new IllegalStateException("WebClient returned null");
            }
        
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");
            if ("success".equals(status)) {
                return true;
            } 
            else if ("change failed".equals(status)) {
                return false;
            }
            else {
                throw new IllegalStateException("WebClient returned error status: " + status);
            }
            
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to parse JSON response", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }

    }
    
    public boolean editOrganizationInfo(String orgId, String newName, String newDescription) {
        if (orgId == null) {
            throw new IllegalArgumentException("OrgID cannot be null");
        }
    
        // Prepare update data
        Map<String, Object> map = new HashMap<>();
        map.put("id", orgId);
        if (newName != null && !newName.isEmpty()) {
            map.put("name", newName);
        }
        if (newDescription != null && !newDescription.isEmpty()) {
            map.put("description", newDescription);
        }
    
        try {
            String response = client.makeRequest("/editOrgInfo", map);
            if (response == null) {
                throw new IllegalStateException("WebClient returned null");
            }
    
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(response);
            String status = (String) json.get("status");
            if ("success".equals(status)) {
                return true;
            } else if ("update failed".equals(status)) {
                return false;
            } else {
                throw new IllegalStateException("WebClient returned error status: " + status);
            }
        } catch (ParseException e) {
            throw new IllegalStateException("Failed to parse JSON response", e);
        } catch (Exception e) {
            throw new IllegalStateException("Error in communicating with server", e);
        }
    }
    
    
}