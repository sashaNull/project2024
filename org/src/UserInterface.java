import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private DataManager dataManager;
    private Organization org;
    private Scanner in = new Scanner(System.in);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

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
                System.out.println("Enter the fund number to see more information or make a donation.");
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

        try {
            Fund fund = dataManager.createFund(org.getId(), name, description, target);
            if (fund != null) {
                org.getFunds().add(fund);
                System.out.println("Fund created successfully.");
            } else {
                System.out.println("Failed to create fund. Please try again.");
            }
        } catch (IllegalStateException e) {
            System.out.println("Error in communicating with server. Please try again.");
        }
    }

    public void makeDonation(String fundId) {
        String contributorId;
        long amount = -1;

        // Display the list of all contributors
        List<Contributor> contributors = dataManager.getAllContributors();
        if (contributors.isEmpty()) {
            System.out.println("No contributors found. Cannot proceed with the donation.");
            return;
        }

        // Prompt for contributor ID and ensure it's not blank
        while (true) {
            System.out.print("Enter the contributor ID: ");
            contributorId = in.nextLine().trim();
            if (!contributorId.isEmpty() && contributorExists(contributorId, contributors)) {
                break;
            }
            System.out.println("Contributor ID cannot be blank or invalid. Please enter a valid contributor ID.");
        }

        // Prompt for donation amount and ensure it's a positive number
        while (true) {
            System.out.print("Enter the donation amount: ");
            String amountInput = in.nextLine().trim();
            try {
                amount = Long.parseLong(amountInput);
                if (amount > 0) {
                    break;
                }
                System.out.println("Donation amount must be a positive number. Please enter a valid donation amount.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value for the donation amount.");
            }
        }

        try {
            boolean success = dataManager.makeDonation(fundId, contributorId, amount);
            if (success) {
                System.out.println("Donation made successfully.");
                displayDonationsForFund(fundId);
            } else {
                System.out.println("Failed to make donation. Please try again.");
            }
        } catch (IllegalStateException e) {
            System.out.println("Error in communicating with server. Please try again.");
        }
    }

    private boolean contributorExists(String contributorId, List<Contributor> contributors) {
        for (Contributor contributor : contributors) {
            if (contributor.getId().equals(contributorId)) {
                return true;
            }
        }
        return false;
    }

    public void displayFund(int fundNumber) {
        Fund fund = org.getFunds().get(fundNumber - 1);
        long totalAmount = 0;

        System.out.println("\n\n");
        System.out.println("Here is information about this fund:");
        System.out.println("Name: " + fund.getName());
        System.out.println("Description: " + fund.getDescription());
        System.out.println("Target: $" + fund.getTarget());

        List<Donation> donations = fund.getDonations();
        System.out.println("Number of donations: " + donations.size());
        for (Donation donation : donations) {
            totalAmount += donation.getAmount();
            String formattedDate = Instant.parse(donation.getDate()).atZone(ZoneId.systemDefault()).format(formatter);
            System.out.println("* " + donation.getContributorName() + ": $" + donation.getAmount() + " on " + formattedDate);
        }

        System.out.printf("Total donation amount: $%d (%.2f%% of target)%n", totalAmount,
                ((double) totalAmount / fund.getTarget()) * 100);

        System.out.println("Enter 'd' to make a donation to this fund");
        System.out.println("Enter 'a' to see aggregated donations by contributor or press Enter to go back to the listing of funds");
        String input = in.nextLine().trim();
        if (input.equals("a")) {
            displayAggregatedDonations(fund);
        } else if (input.equals("d")) {
            makeDonation(fund.getId());
        }
    }

    public void displayAggregatedDonations(Fund fund) {
        List<ContributorAggregate> aggregatedDonations = fund.getAggregatedDonations();
        System.out.println("\n\nAggregated Donations:");
        for (ContributorAggregate aggregate : aggregatedDonations) {
            System.out.printf("%s, %d donations, $%d total%n", aggregate.getContributorName(),
                    aggregate.getDonationCount(), aggregate.getTotalAmount());
        }
        System.out.println("Press the Enter key to go back to the listing of funds");
        in.nextLine();
    }

    public void displayDonationsForFund(String fundId) {
        for (Fund fund : org.getFunds()) {
            if (fund.getId().equals(fundId)) {
                displayFund(org.getFunds().indexOf(fund) + 1);
                return;
            }
        }
        System.out.println("Fund not found. Please try again.");
    }
    
    public static void main(String[] args) {

        DataManager ds = new DataManager(new WebClient("localhost", 3001));

        String login = "upenn";
        String password = "123456";

        if (args.length == 2) {
            login = args[0];
            password = args[1];
        }

        while (true) {
            try {
                Organization org = ds.attemptLogin(login, password);

                if (org == null) {
                    System.out.println("Login failed.");
                    return;
                } else {
                    UserInterface ui = new UserInterface(ds, org);
                    ui.start();
                    return;
                }
            } catch (IllegalStateException e) {
                System.out.println("Error in communicating with server. Please try again.");
                System.out.print("Enter username: ");
                login = new Scanner(System.in).nextLine().trim();
                System.out.print("Enter password: ");
                password = new Scanner(System.in).nextLine().trim();
            }
        }
    }
}
