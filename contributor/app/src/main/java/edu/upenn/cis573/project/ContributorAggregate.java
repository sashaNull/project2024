package edu.upenn.cis573.project;

public class ContributorAggregate {
    private String fundName;
    private int donationCount;
    private long totalAmount;

    public ContributorAggregate(String fundName) {
        this.fundName = fundName;
        this.donationCount = 0;
        this.totalAmount = 0;
    }

    public void addDonation(Donation donation) {
        donationCount++;
        totalAmount += donation.getAmount();
    }

    public String getFundName() {
        return fundName;
    }

    public int getDonationCount() {
        return donationCount;
    }

    public long getTotalAmount() {
        return totalAmount;
    }
}
