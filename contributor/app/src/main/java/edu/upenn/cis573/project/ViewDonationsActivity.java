package edu.upenn.cis573.project;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Scanner;

public class ViewDonationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donations);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView messageField = findViewById(R.id.donationsListHeaderField);

        Contributor contributor = MainActivity.contributor;

        Log.v("contributor", "number of donations when listing " + contributor.getDonations().size());

        messageField.setText("Here are " + contributor.getName() + "'s donations:");

        // Ask the user if they want to see aggregated donations
        System.out.println("Enter 'a' to see aggregated donations by fund, or press Enter to see individual donations");
        String input = new Scanner(System.in).nextLine().trim();

        if (input.equals("a")) {
            displayAggregatedDonations(contributor);
        } else {
            displayIndividualDonations(contributor);
        }
    }

    private void displayIndividualDonations(Contributor contributor) {
        String[] donations = new String[contributor.getDonations().size()];

        int index = 0;

        for (Donation d : contributor.getDonations()) {
            donations[index++] = d.toString();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.listview, donations);

        ListView listView = findViewById(R.id.donationsList);
        listView.setAdapter(adapter);
    }

    private void displayAggregatedDonations(Contributor contributor) {
        List<ContributorAggregate> aggregatedDonations = contributor.getAggregatedDonations();
        String[] aggregates = new String[aggregatedDonations.size()];

        int index = 0;
        for (ContributorAggregate aggregate : aggregatedDonations) {
            aggregates[index++] = String.format("%s, %d donations, $%d total", aggregate.getFundName(), aggregate.getDonationCount(), aggregate.getTotalAmount());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.listview, aggregates);

        ListView listView = findViewById(R.id.donationsList);
        listView.setAdapter(adapter);
    }
}
