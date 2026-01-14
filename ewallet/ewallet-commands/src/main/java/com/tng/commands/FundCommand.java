package com.tng.commands;

import com.tng.InvestmentService;
import com.tng.FundData;
import com.tng.PortfolioData;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

import java.util.List;

@Command(scope = "ewallet", name = "invest-list", description = "List all available investment funds (optionally show user holdings)")
@Service
public class FundCommand implements Action {

    @Reference
    private InvestmentService investmentService;

    @Argument(
        index = 0,
        name = "username",
        description = "Username to display owned units (optional)",
        required = false
    )
    private String username;

    @Override
    public Object execute() {

        List<FundData> funds = investmentService.getAllAvailableFunds();

        if (funds.isEmpty()) {
            System.out.println("No funds available. Use 'invest:init' to populate samples.");
            return null;
        }

        PortfolioData portfolio = null;
        if (username != null) {
            portfolio = investmentService.getUserPortfolio(username);
        }

        System.out.printf(
            "%-3s | %-6s | %-25s | %-10s | %-8s | %-10s%n",
            "#", "ID", "NAME", "PRICE", "RISK", "OWNED"
        );
        System.out.println("----------------------------------------------------------------------------");

        int i = 1;
        for (FundData f : funds) {

            double owned = 0.0;
            if (portfolio != null) {
                owned = portfolio.getUnitsForFund(f.getFundId());
            }

            System.out.printf(
                "%-3d | %-6s | %-25s | RM %-7.2f | %-8s | %-10.4f%n",
                i++, f.getFundId(), f.getName(), f.getPrice(), f.getRiskCategory(), owned
            );
        }

        return null;
    }
}
