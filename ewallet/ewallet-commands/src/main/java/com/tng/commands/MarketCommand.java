package com.tng.commands;

import com.tng.InvestmentService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "ewallet", name = "invest-simulate", description = "Simulate market price changes")
@Service
public class MarketCommand implements Action {

    @Reference
    private InvestmentService investmentService;

    @Override
    public Object execute() {
        System.out.println("[Market] Simulating fund price changes based on volatility...");
        investmentService.runMarketChangeSimulation(); 
        return null;
    }
}