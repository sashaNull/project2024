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
                String fundId = (String) data.get("_id");
                String name = (String) data.get("name");
                String description = (String) data.get("description");
                Organization org = new Organization(fundId, name, description);

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

    /**
     * Look up the name of the contributor with the specified ID.
     * This method uses the /findContributorNameById endpoint in the API.
     * 
     * @return the name of the contributor on success; null if no contributor is
     *         found
     */
    public String getContributorName(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Contributor ID cannot be null");
        }

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
                contributorNameCache.put(id, name);
                return name;
            } else {
                throw new IllegalStateException("WebClient returned error status: " + status);
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
}
