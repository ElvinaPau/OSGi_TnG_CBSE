package com.tng.commands;

import com.tng.InvestmentService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "invest-trade", description = "Buy or Sell fund units")
@Service
public class TradeCommand implements Action {

    @Reference
    private InvestmentService investmentService;

    @Argument(index = 0, name = "username", description = "User's ID", required = true)
    String username;

    @Argument(index = 1, name = "action", description = "BUY or SELL", required = true)
    String action;

    @Argument(index = 2, name = "fundId", description = "The ID of the fund", required = true)
    String fundId;

    @Argument(index = 3, name = "amount", description = "RM amount to buy or Unit count to sell", required = true)
    double value;

    @Override
    public Object execute() {
        try {
            if ("BUY".equalsIgnoreCase(action)) {
                investmentService.investInFund(username, fundId, value);
                System.out.println("Successfully invested RM " + value + " in " + fundId);
            } else if ("SELL".equalsIgnoreCase(action)) {
                investmentService.sellFund(username, fundId, value);
                System.out.println("Successfully sold " + value + " units of " + fundId);
            } else {
                System.err.println("Invalid action. Use BUY or SELL.");
            }
        } catch (Exception e) {
            System.err.println("Transaction failed: " + e.getMessage());
        }
        return null;
    }
}