package edu.upenn.cis573.project;

import java.io.Serializable;
import java.util.*;

public class Contributor implements Serializable {

    private String id;
    private String name;
    private String email;
    private String creditCardNumber;
    private String creditCardCVV;
    private String creditCardExpiryMonth;
    private String creditCardExpiryYear;
    private String creditCardPostCode;
    private List<Donation> donations;
    private Map<String, ContributorAggregate> aggregatedDonationsCache;

    public Contributor(String id, String name, String email, String creditCardNumber, String creditCardCVV, String creditCardExpiryMonth, String creditCardExpiryYear, String creditCardPostCode) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.creditCardNumber = creditCardNumber;
        this.creditCardCVV = creditCardCVV;
        this.creditCardExpiryMonth = creditCardExpiryMonth;
        this.creditCardExpiryYear = creditCardExpiryYear;
        this.creditCardPostCode = creditCardPostCode;
        donations = new LinkedList<>();
        aggregatedDonationsCache = null; // Initialize the cache as null
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public String getCreditCardCVV() {
        return creditCardCVV;
    }

    public String getCreditCardExpiryMonth() {
        return creditCardExpiryMonth;
    }

    public String getCreditCardExpiryYear() {
        return creditCardExpiryYear;
    }

    public String getCreditCardPostCode() {
        return creditCardPostCode;
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
        aggregatedDonationsCache = null; // Invalidate the cache when donations are set
    }

    public List<ContributorAggregate> getAggregatedDonations() {
        if (aggregatedDonationsCache == null) {
            aggregatedDonationsCache = new HashMap<>();
            for (Donation donation : donations) {
                String fundName = donation.getFundName();
                ContributorAggregate aggregate = aggregatedDonationsCache.get(fundName);
                if (aggregate == null) {
                    aggregate = new ContributorAggregate(fundName);
                    aggregatedDonationsCache.put(fundName, aggregate);
                }
                aggregate.addDonation(donation);
            }
        }
        List<ContributorAggregate> aggregatedList = new ArrayList<>(aggregatedDonationsCache.values());
        aggregatedList.sort(Comparator.comparingLong(ContributorAggregate::getTotalAmount).reversed());
        return aggregatedList;
    }
}
