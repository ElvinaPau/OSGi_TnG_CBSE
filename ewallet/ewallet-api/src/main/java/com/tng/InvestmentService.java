package com.tng;

import java.util.List;

public interface InvestmentService {

    // 1. Fund functions
    void addFund(FundData fund);
    FundData getFundById(String fundId);
    List<FundData> getAllAvailableFunds();
    void updateFundPrice(String fundId, double newPrice);
    void runMarketChangeSimulation();
    FundData getFundByIndex(int index);
    void deleteFundByIndex(int index);

    // 2. Investment functions
    InvestmentData investInFund(String phoneNumber, String username, String fundId, double amount);
    void sellFund(String phoneNumber, String username, String fundId, double units);
    List<InvestmentData> getInvestmentHistory(String username);

    // 3. Portfolio functions
    PortfolioData getUserPortfolio(String username);
    double calculateReturns(String username);
    void evaluateRiskProfile(String username, String riskCategory);
}
