package edu.upenn.cis573.project;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class DataManager {

    private WebClient client;

    public DataManager(WebClient client) {
        this.client = client;
    }


    /**
     * Attempt to log in to the Contributor account using the specified login and password.
     * This method uses the /findContributorByLoginAndPassword endpoint in the API
     * @return the Contributor object if successfully logged in, null otherwise
     */
    public Contributor attemptLogin(String login, String password) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("login", login);
            map.put("password", password);
            String response = client.makeRequest("/findContributorByLoginAndPassword", map);

            JSONObject json = new JSONObject(response);
            String status = json.getString("status");

            if (status.equals("success")) {
                JSONObject data = json.getJSONObject("data");
                String id = data.getString("_id");
                String name = data.getString("name");
                String email = data.getString("email");
                String creditCardNumber = data.optString("creditCardNumber", null);
                String creditCardCVV = data.optString("creditCardCVV", null);
                String creditCardExpiryMonth = String.valueOf(data.optInt("creditCardExpiryMonth", 0));
                String creditCardExpiryYear = String.valueOf(data.optInt("creditCardExpiryYear", 0));
                String creditCardPostCode = data.optString("creditCardPostCode", null);

                Contributor contributor = new Contributor(id, name, email, creditCardNumber, creditCardCVV, creditCardExpiryYear, creditCardExpiryMonth, creditCardPostCode);

                List<Donation> donationList = new LinkedList<>();
                JSONArray donations = data.optJSONArray("donations");
                if (donations != null) {
                    for (int i = 0; i < donations.length(); i++) {
                        JSONObject jsonDonation = donations.getJSONObject(i);
                        String fund = getFundName(jsonDonation.getString("fund"));
                        String date = jsonDonation.getString("date");
                        long amount = jsonDonation.optLong("amount", 0);

                        Donation donation = new Donation(fund, name, amount, date);
                        donationList.add(donation);
                    }
                }
                contributor.setDonations(donationList);
                return contributor;
            }
            return null;
        } catch (JSONException e) {
            Log.e("DataManager", "Invalid JSON response", e);
            return null;
        } catch (Exception e) {
            Log.e("DataManager", "Exception during login attempt", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the name of the fund with the specified ID using the /findFundNameById endpoint
     * @return the name of the fund if found, "Unknown fund" if not found, null if an error occurs
     */
    public String getFundName(String id) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            String response = client.makeRequest("/findFundNameById", map);

            // Added check to ensure the response is a valid JSON object
            if (!isValidJsonObject(response)) {
                Log.e("DataManager", "Invalid JSON response: " + response);
                return null;
            }

            JSONObject json = new JSONObject(response);
            String status = json.getString("status");

            if (status.equals("success")) {
                return json.getString("data");
            } else {
                return "Unknown Fund";
            }
        } catch (Exception e) {
            Log.e("DataManager", "Exception during getFundName", e);
            return null;
        }
    }

    /**
     * Get information about all of the organizations and their funds.
     * This method uses the /allOrgs endpoint in the API
     * @return a List of Organization objects if successful, null otherwise
     */
    public List<Organization> getAllOrganizations() {
        try {
            Map<String, Object> map = new HashMap<>();
            String response = client.makeRequest("/allOrgs", map);

            if (!isValidJsonObject(response)) {
                Log.e("DataManager", "Invalid JSON response: " + response);
                return null;
            }

            JSONObject json = new JSONObject(response);
            String status = json.getString("status");

            if (status.equals("success")) {
                List<Organization> organizations = new LinkedList<>();
                JSONArray data = json.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    String id = obj.getString("_id");
                    String name = obj.getString("name");

                    Organization org = new Organization(id, name);
                    List<Fund> fundList = new LinkedList<>();

                    JSONArray array = obj.getJSONArray("funds");
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject fundObj = array.getJSONObject(j);
                        id = fundObj.getString("_id");
                        name = fundObj.getString("name");
                        long target = fundObj.getLong("target");
                        long totalDonations = fundObj.getLong("totalDonations");

                        Fund fund = new Fund(id, name, target, totalDonations);
                        fundList.add(fund);
                    }
                    org.setFunds(fundList);
                    organizations.add(org);
                }
                return organizations;
            }
            return null;
        } catch (Exception e) {
            Log.e("DataManager", "Exception during getAllOrganizations", e);
            return null;
        }
    }
    private boolean isValidJsonObject(String response) {
        try {
            new JSONObject(response);
            return true;
        } catch (JSONException e) {
            return false;
        }
    }

    /**
     * Make a donation to the specified fund for the specified amount.
     * This method uses the /makeDonation endpoint in the API
     * @return true if successful, false otherwise
     */
    public boolean makeDonation(String contributorId, String fundId, String amount) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("contributor", contributorId);
            map.put("fund", fundId);
            map.put("amount", amount);
            String response = client.makeRequest("/makeDonation", map);
            // added check to ensure the response is a valid JSON object
            if (!isValidJsonObject(response)) {
                Log.e("DataManager", "Invalid JSON response: " + response);
                return false;
            }
            JSONObject json = new JSONObject(response);
            String status = json.getString("status");
            return status.equals("success");
        } catch (Exception e) {
            Log.e("DataManager", "Exception during makeDonation", e);
            return false;
        }
    }
}
