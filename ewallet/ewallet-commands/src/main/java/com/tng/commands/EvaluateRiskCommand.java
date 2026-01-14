package com.tng.commands;

import com.tng.InvestmentService;
import com.tng.FundData;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;

import java.util.List;
import java.util.Scanner;

@Command(scope = "ewallet", name = "risk-quiz", description = "Interactive Risk Assessment Quiz")
@Service
public class EvaluateRiskCommand implements Action {

    @Reference
    private InvestmentService investmentService;

    @Reference
    private Session session;

    @Argument(index = 0, name = "username", description = "The user taking the quiz", required = true)
    String username;

    @Override
    public Object execute() {
        int totalScore = 0;

        System.out.println("RISK ASSESSMENT QUIZ");
        System.out.println("Hello " + username + ", let's determine your risk profile.");

        // Question 1
        System.out.println("\n1. What is your investment timeframe?");
        System.out.println("   (1) Less than 1 year (Short term)");
        System.out.println("   (2) 1 to 5 years (Medium term)");
        System.out.println("   (3) More than 5 years (Long term)");
        totalScore += getValidInput(1, 3);

        // Question 2
        System.out.println("\n2. How much of your savings are you willing to invest?");
        System.out.println("   (1) Small portion (< 10%)");
        System.out.println("   (2) Moderate portion (10% - 30%)");
        System.out.println("   (3) Large portion (> 30%)");
        totalScore += getValidInput(1, 3);

        // Question 3
        System.out.println("\n3. If your investment value drops 10% in a month, you would:");
        System.out.println("   (1) Sell immediately to prevent further loss");
        System.out.println("   (2) Hold and wait for recovery");
        System.out.println("   (3) Buy more while it is cheaper");
        totalScore += getValidInput(1, 3);

        // Logic to determine category
        String riskCategory;
        if (totalScore <= 3) {
            riskCategory = "CONSERVATIVE";
        } else if (totalScore <= 6) {
            riskCategory = "MODERATE";
        } else {
            riskCategory = "AGGRESSIVE";
        }

        // Save to the Service
        investmentService.evaluateRiskProfile(username, riskCategory);

        System.out.println("\n----------------------------------------------------");
        System.out.println("RESULT: Your Risk Profile is " + riskCategory);
        System.out.println("----------------------------------------------------");

        suggestFund(riskCategory);

        return null;
    }

    private int getValidInput(int min, int max) {
        Scanner scanner = new Scanner(session.getKeyboard());
        int choice = -1;
        
        while (choice < min || choice > max) {
            session.getConsole().print("Your choice (" + min + "-" + max + "): ");
            session.getConsole().flush(); 
            
            try {
                if (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim();
                    if (input.isEmpty()) continue;
                    choice = Integer.parseInt(input);
                } else {
                    break; // Stream ended
                }
            } catch (Exception e) {
                session.getConsole().println("Invalid input. Please enter a number.");
            }
        }
        return choice;
    }

    private void suggestFund(String profile) {
        List<FundData> funds = investmentService.getAllAvailableFunds();
        String targetRisk = profile.equals("CONSERVATIVE") ? "Low" : 
                            profile.equals("MODERATE") ? "Medium" : "High";

        System.out.println("Based on your profile, we recommend looking at:");
        funds.stream()
            .filter(f -> f.getRiskCategory().equalsIgnoreCase(targetRisk))
            .findFirst()
            .ifPresentOrElse(
                f -> System.out.println(" >> " + f.getName() + " (ID: " + f.getFundId() + ")"),
                () -> System.out.println(" >> No matching funds found at this time.")
            );
    }
}