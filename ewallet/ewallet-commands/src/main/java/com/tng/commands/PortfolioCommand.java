package com.tng.commands;

import com.tng.InvestmentService;
import com.tng.PortfolioData;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "invest-portfolio", description = "View user portfolio and returns")
@Service
public class PortfolioCommand implements Action {

    @Reference
    private InvestmentService investmentService;

    @Argument(index = 0, name = "username", required = true)
    String username;

    @Override
    public Object execute() {

        PortfolioData p = investmentService.getUserPortfolio(username);
        double returns = investmentService.calculateReturns(username);

        System.out.println("\n=== PORTFOLIO FOR: " + username + " ===");
        System.out.println("Risk Profile: " +
                (p.getRiskCategory() != null ? p.getRiskCategory() : "Not Assessed"));
        System.out.println("Holdings: " + p.getFundHoldings());
        System.out.println("----------------------------------");
        System.out.printf("Net Performance: RM %.2f%n", returns);

        return null;
    }
}