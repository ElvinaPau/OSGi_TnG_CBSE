package com.tng.commands;

import com.tng.InvestmentService;
import com.tng.FundData;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

@Command(scope = "ewallet", name = "risk-quiz", description = "Take Risk Assessment Quiz")
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
        PrintStream out = session.getConsole();
        Scanner scanner = new Scanner(session.getKeyboard());
        int totalScore = 0;

        out.println("RISK ASSESSMENT QUIZ");
        out.println("Hello " + username + ", let's determine your risk profile.");

        // Question 1
        out.println("\n1. What is your investment goal?");
        out.println("   (1) Preserve Capital");
        out.println("   (2) Balanced Growth");
        out.println("   (3) Maximize Returns");
        totalScore += getValidInput(scanner, 1, 3);

        // Question 2
        out.println("\n2. How do you react if your investment drops 10%?");
        out.println("   (1) Sell everything");
        out.println("   (2) Do nothing");
        out.println("   (3) Buy more");
        totalScore += getValidInput(scanner, 1, 3);

        // Question 3
        out.println("\n3. What is your investment timeframe?");
        out.println("   (1) < 1 year");
        out.println("   (2) 1-5 years");
        out.println("   (3) 5+ years");
        totalScore += getValidInput(scanner, 1, 3);

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

        out.println("\n----------------------------------------------------");
        out.println("RESULT: Your Risk Profile is " + riskCategory);
        out.println("----------------------------------------------------");

        suggestFund(riskCategory, out);

        return null;
    }

    private int getValidInput(Scanner scanner, int min, int max) {
        while (true) {
            session.getConsole().print("Your choice (" + min + "-" + max + "): ");
            session.getConsole().flush();

            if (!scanner.hasNextLine()) return -1;

            String input = scanner.next();
            if (scanner.hasNextLine()) {
                scanner.nextLine(); 
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
                session.getConsole().println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                session.getConsole().println("Invalid input. Please enter a digit.");
            }
        }
    }

    private void suggestFund(String profile, PrintStream out) {
        List<FundData> funds = investmentService.getAllAvailableFunds();
        String targetRisk = profile.equals("CONSERVATIVE") ? "Low" : 
                            profile.equals("MODERATE") ? "Medium" : "High";

        out.println("Based on your profile, we recommend looking at:");
        funds.stream()
            .filter(f -> f.getRiskCategory().equalsIgnoreCase(targetRisk))
            .findFirst()
            .ifPresentOrElse(
                f -> out.println(" >> " + f.getName() + " (ID: " + f.getFundId() + ")"),
                () -> out.println(" >> No matching funds found at this time.")
            );
    }
}