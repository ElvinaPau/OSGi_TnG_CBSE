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

@Command(scope = "ewallet", name = "invest-list", description = "View available investment funds")
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
            System.out.println("No funds available.");
            return null;
        }

        PortfolioData portfolio = null;
        if (username != null) {
            portfolio = investmentService.getUserPortfolio(username);
            System.out.println("Username: " + username);
        }

        System.out.printf(
            "%n%-3s | %-6s | %-25s | %10s | %-8s | %10s%n",
            "#", "ID", "NAME", "NAV (RM)", "RISK", "OWNED"
        );
        System.out.println("-----------------------------------------------------------------------------");

        int i = 1;
        for (FundData f : funds) {

            double owned = 0.0;
            if (portfolio != null) {
                owned = portfolio.getUnitsForFund(f.getFundId());
            }

            System.out.printf(
                "%-3d | %-6s | %-25s | %10.4f | %-8s | %10.4f%n",
                i++, f.getFundId(), f.getName(), f.getNav(), f.getRiskCategory(), owned
            );
        }
        System.out.println("");

        return null;
    }
}
