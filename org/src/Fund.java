import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;

public class Fund {

	private String id;
	private String name;
	private String description;
	private long target;
	private List<Donation> donations;
	private List<ContributorAggregate> aggregatedDonations;  // To cache aggregated donations

	public Fund(String id, String name, String description, long target) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.target = target;
		donations = new LinkedList<>();
		aggregatedDonations = null;  // Initialize as null for lazy calculation
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public long getTarget() {
		return target;
	}

	public void setDonations(List<Donation> donations) {
		this.donations = donations;
		this.aggregatedDonations = null;  // Reset cache when donations are set
	}

	public List<Donation> getDonations() {
		return donations;
	}

	public List<ContributorAggregate> getAggregatedDonations() {
		if (aggregatedDonations == null) {
			Map<String, ContributorAggregate> aggregateMap = new HashMap<>();
			for (Donation donation : donations) {
				String contributorName = donation.getContributorName();
				ContributorAggregate aggregate = aggregateMap.getOrDefault(contributorName, new ContributorAggregate(contributorName));
				aggregate.addDonation(donation);
				aggregateMap.put(contributorName, aggregate);
			}
			aggregatedDonations = new LinkedList<>(aggregateMap.values());
			Collections.sort(aggregatedDonations, Comparator.comparingLong(ContributorAggregate::getTotalAmount).reversed());
		}
		return aggregatedDonations;
	}
}