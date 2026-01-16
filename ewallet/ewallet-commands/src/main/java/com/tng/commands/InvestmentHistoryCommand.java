package com.tng.commands;

import com.tng.InvestmentService;
import com.tng.InvestmentData;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.List;

@Command(scope = "ewallet", name = "invest-history", description = "View investment transaction history")
@Service
public class InvestmentHistoryCommand implements Action {

    @Reference
    private InvestmentService investmentService;

    @Argument(index = 0, name = "username", description = "User's ID to fetch history", required = true)
    String username;

    @Override
    public Object execute() {
        List<InvestmentData> history = investmentService.getInvestmentHistory(username);

        if (history == null || history.isEmpty()) {
            System.out.println("\n[System] No transaction history found for user: " + username);
            return null;
        }

        System.out.println("\n" + "=".repeat(95));
        System.out.println(
            String.format("                  INVESTMENT TRANSACTION HISTORY: %s", username.toUpperCase())
        );
        System.out.println("=".repeat(95));
        
        // Table Header
        System.out.println(String.format("%-10s | %-12s | %-8s | %-12s | %-10s | %-10s", 
            "TYPE", "FUND ID", "STATUS", "AMOUNT", "UNITS", "DATE"));
        System.out.println("-".repeat(95));

        for (InvestmentData record : history) {
            // Logic for visual indicators (+ for Sell, - for Buy)
            String prefix = "BUY".equalsIgnoreCase(record.getType()) ? "-" : "+";
            
            System.out.println(String.format("%-10s | %-12s | %-8s | %-12s | %-10.4f | %-10s", 
                record.getType(),
                record.getFundId(),
                record.getStatus(),
                prefix + " RM " + String.format("%.2f", record.getAmount()),
                record.getUnits(),
                record.getTimestamp()
            ));
        }

        System.out.println("=".repeat(95));
        System.out.println(String.format("Total Transactions: %d", history.size()));
        System.out.println("=".repeat(95) + "\n");

        return null;
    }
}
