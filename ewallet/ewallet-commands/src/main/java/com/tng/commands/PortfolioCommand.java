package com.tng.commands;

import com.tng.FundData;
import com.tng.InvestmentService;
import com.tng.PortfolioData;

import java.util.Map;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;

@Command(scope = "ewallet", name = "invest-portfolio", description = "View portfolio and returns")
@Service
public class PortfolioCommand implements Action {

    @Reference
    private InvestmentService investmentService;

    @Reference
    private Session session;

    @Argument(index = 0, name = "username", required = true)
    String username;

    @Override
    public Object execute() {
        PortfolioData p = investmentService.getUserPortfolio(username);
        double returns = investmentService.calculateReturns(username);
        String status = (returns >= 0) ? "PROFIT" : "LOSS";
        String risk = (p.getRiskCategory() != null) ? p.getRiskCategory() : "Not Assessed (Take the quiz!)";

        StringBuilder sb = new StringBuilder();
        sb.append("\n==========================================\n");
        sb.append("         YOUR PORTFOLIO SUMMARY           \n");
        sb.append("==========================================\n");
        sb.append(String.format("Username     : %s%n", username));
        sb.append(String.format("Risk Profile : %s%n", risk));
        sb.append("------------------------------------------\n");

        Map<String, Double> holdings = p.getFundHoldings();
        if (holdings == null || holdings.isEmpty()) {
            sb.append(String.format("Holdings     : No active investments.%n"));
        } else {
            sb.append("CURRENT HOLDINGS:\n");
            for (Map.Entry<String, Double> entry : holdings.entrySet()) {
                String fundId = entry.getKey();
                double units = entry.getValue();
                
                // Fetch Fund Details from the service
                FundData f = investmentService.getFundById(fundId);
                
                if (f != null && units > 0) {
                    double marketValue = units * f.getPrice();
                    sb.append(String.format(" - %-20s: %.4f units (RM %.2f)%n", 
                        f.getName(), units, marketValue));
                }
            }
        }

        sb.append("------------------------------------------\n");
        sb.append(String.format("Net Performance : RM %.2f%n", returns));
        sb.append(String.format("Overall Status  : %s%n", status));
        sb.append("==========================================\n");

        session.getConsole().print(sb.toString());

        return null;
    }
}