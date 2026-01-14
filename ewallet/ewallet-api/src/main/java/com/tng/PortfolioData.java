package com.tng;

import java.util.HashMap;
import java.util.Map;

public class PortfolioData {

    private String portfolioId;
    private String userId;
    private String riskCategory;
    private double totalUnits;
    private double totalValue;
    private double totalReturns;
    private Map<String, Double> fundHoldings;

    public PortfolioData() {
        this.fundHoldings = new HashMap<>();
    }

    public Map<String, Double> getFundHoldings() { 
        if (this.fundHoldings == null) {
            this.fundHoldings = new HashMap<>();
        }
        return fundHoldings; 
    }

    public double getUnitsForFund(String fundId) {
        return getFundHoldings().getOrDefault(fundId, 0.0);
    }

    public void updateHoldings(String fundId, double unitChange) {
        Map<String, Double> holdings = getFundHoldings(); 
        double currentUnits = getUnitsForFund(fundId);
        double newUnits = currentUnits + unitChange;        
        holdings.put(fundId, newUnits);
        
        this.totalUnits = holdings.values().stream()
                                  .mapToDouble(Double::doubleValue)
                                  .sum();
    }

    public void calculateTotalValue(double currentNav) {
        this.totalValue = this.totalUnits * currentNav;
    }

    // Getters and Setters
    public String getPortfolioId() { return portfolioId; }
    public void setPortfolioId(String id) { this.portfolioId = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRiskCategory() { return riskCategory; }
    public void setRiskCategory(String riskCategory) { this.riskCategory = riskCategory; }

    public double getTotalUnits() { return totalUnits; }
    public void setTotalUnits(double totalUnits) { this.totalUnits = totalUnits; }

    public double getTotalValue() { return totalValue; }
    public void setTotalValue(double totalValue) { this.totalValue = totalValue; }

    public double getTotalReturns() { return totalReturns; }
    public void setTotalReturns(double totalReturns) { this.totalReturns = totalReturns; }

    public void setFundHoldings(Map<String, Double> fundHoldings) { this.fundHoldings = fundHoldings; }
}