package com.tng.commands;

import com.tng.InvestmentService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "invest-simulate", description = "Simulate market fluctuations and notify user")
@Service
public class MarketCommand implements Action {

    @Reference
    private InvestmentService investmentService;

    @Argument(index = 0, name = "phoneNumber", description = "User's phone number for notifications (optional)", required = false)
    private String phoneNumber;

    @Override
    public Object execute() {
        System.out.println("[Market] Simulating fund price changes based on volatility...");
        investmentService.runMarketChangeSimulation(phoneNumber);
        return null;
    }
}