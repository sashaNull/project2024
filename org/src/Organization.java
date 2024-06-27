import java.util.LinkedList;
import java.util.List;

public class Organization {
	
	private String id;
	private String name;
	private String login;
	private String description;
	
	private List<Fund> funds;
	
	public Organization(String id, String name, String login, String description) {
		this.id = id;
		this.name = name;
		this.login = login;
		this.description = description;
		funds = new LinkedList<>();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getLogin() {
		return login;
	}

	public String getDescription() {
		return description;
	}

	public List<Fund> getFunds() {
		return funds;
	}
	
	public void addFund(Fund fund) {
		funds.add(fund);
	}
	

}
