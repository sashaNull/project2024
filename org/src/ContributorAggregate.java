public class ContributorAggregate {

	private String contributorName;
	private int donationCount;
	private long totalAmount;

	public ContributorAggregate(String contributorName) {
		this.contributorName = contributorName;
		this.donationCount = 0;
		this.totalAmount = 0;
	}

	public void addDonation(Donation donation) {
		this.donationCount++;
		this.totalAmount += donation.getAmount();
	}

	public String getContributorName() {
		return contributorName;
	}

	public int getDonationCount() {
		return donationCount;
	}

	public long getTotalAmount() {
		return totalAmount;
	}
}