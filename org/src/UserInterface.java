import java.util.List;
import java.util.Scanner;

public class UserInterface {
	
	
	private DataManager dataManager;
	private Organization org;
	private Scanner in = new Scanner(System.in);
	
	public UserInterface(DataManager dataManager, Organization org) {
		this.dataManager = dataManager;
		this.org = org;
	}
	
	public void start() {
		while (true) {
			System.out.println("\n\n");
			if (org.getFunds().size() > 0) {
				System.out.println("There are " + org.getFunds().size() + " funds in this organization:");
			
				int count = 1;
				for (Fund f : org.getFunds()) {
					System.out.println(count + ": " + f.getName());
					count++;
				}
				System.out.println("Enter the fund number to see more information.");
			}
			System.out.println("Enter 0 to create a new fund");
			System.out.println("Enter 'q' or 'quit' to exit the program.");
	
			String input = in.nextLine().trim();
	
			if (input.equals("q") || input.equals("quit")) {
				System.out.println("Good bye!");
				break;
			}
	
			try {
				int option = Integer.parseInt(input);
				if (option == 0) {
					createFund();
				} else if (option > 0 && option <= org.getFunds().size()) {
					displayFund(option);
				} else {
					System.out.println("Invalid option. Please enter a valid fund number, 0 to create a new fund, or 'q'/'quit' to exit.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a number, 0 to create a new fund, or 'q'/'quit' to exit.");
			}
		}
	}
	
	public void createFund() {
		String name;
		String description;
		long target = -1;
	
		// Prompt for fund name and ensure it's not blank
		while (true) {
			System.out.print("Enter the fund name: ");
			name = in.nextLine().trim();
			if (!name.isEmpty()) {
				break;
			}
			System.out.println("Fund name cannot be blank. Please enter a valid fund name.");
		}
	
		// Prompt for fund description and ensure it's not blank
		while (true) {
			System.out.print("Enter the fund description: ");
			description = in.nextLine().trim();
			if (!description.isEmpty()) {
				break;
			}
			System.out.println("Fund description cannot be blank. Please enter a valid fund description.");
		}
	
		// Prompt for fund target and ensure it's a positive number
		while (true) {
			System.out.print("Enter the fund target: ");
			String targetInput = in.nextLine().trim();
			try {
				target = Long.parseLong(targetInput);
				if (target > 0) {
					break;
				}
				System.out.println("Fund target must be a positive number. Please enter a valid fund target.");
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a numeric value for the fund target.");
			}
		}
	
		Fund fund = dataManager.createFund(org.getId(), name, description, target);
		org.getFunds().add(fund);
	}
	
	public void displayFund(int fundNumber) {
		
		Fund fund = org.getFunds().get(fundNumber - 1);
		long total_amount = 0;
		
		System.out.println("\n\n");
		System.out.println("Here is information about this fund:");
		System.out.println("Name: " + fund.getName());
		System.out.println("Description: " + fund.getDescription());
		System.out.println("Target: $" + fund.getTarget());
		
		List<Donation> donations = fund.getDonations();
		System.out.println("Number of donations: " + donations.size());
		for (Donation donation : donations) {
			total_amount += donation.getAmount();
			System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + donation.getDate());
		}
	
		
		System.out.printf("Total donation amount: $%d (%.2f%% of target)%n", total_amount, ((double)total_amount/fund.getTarget())*100);
		System.out.println("Press the Enter key to go back to the listing of funds");
		in.nextLine();
	}
	
	
	public static void main(String[] args) {
		
		DataManager ds = new DataManager(new WebClient("localhost", 3001));
		
		String login = args[0];
		String password = args[1];
		
		
		try
		{
			Organization org = ds.attemptLogin(login, password);
			
			if (org == null) {
				System.out.println("Login failed.");
			}
			else {

				UserInterface ui = new UserInterface(ds, org);
			
				ui.start();
			
			}
		}
		catch (IllegalStateException e)
		{
			System.out.println("Error in communicating with server");
		}
	}

}
