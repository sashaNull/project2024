import java.util.LinkedList;
import java.util.List;

public class Fund {

	private String id;
	private String name;
	private String description;
	private long target;
	private List<Donation> donations;
	
	public Fund(String id, String name, String description, long target) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.target = target;
		donations = new LinkedList<>();
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
	}
	
	public List<Donation> getDonations() {
		return donations;
	}
	
	
	
}
