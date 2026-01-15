package com.tng.investment.impl;

import com.tng.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component(service = InvestmentService.class)
public class InvestmentServiceImpl implements InvestmentService {

    private final Map<String, FundData> fundRepo = new ConcurrentHashMap<>();
    private final Map<String, PortfolioData> portfolioRepo = new ConcurrentHashMap<>();
    private final List<InvestmentData> historyRepo =
            Collections.synchronizedList(new ArrayList<>());

    @Reference
    private PaymentService paymentService;

    @Activate
    public void activate() {
        initSampleFunds();
    }

    // Initialize sample Fund Data
    private void initSampleFunds() {
        if (!fundRepo.isEmpty()) return;
        addFund(new FundData("F01", "Low Risk Income Fund", "Bonds and steady interest.", "Low", 1.00));
        addFund(new FundData("F02", "Balanced Global Fund", "International stocks.", "Medium", 2.50));
        addFund(new FundData("F03", "Equity Growth Fund", "High-growth tech markets.", "High", 5.75));
        addFund(new FundData("F04", "Digital Assets Fund", "Blockchain and crypto.", "High", 10.20));
    }

    // FUND METHODS
    @Override
    public void addFund(FundData fund) {
        fundRepo.put(fund.getFundId(), fund);
    }

    @Override
    public FundData getFundById(String fundId) {
        return fundRepo.get(fundId);
    }

    @Override
    public List<FundData> getAllAvailableFunds() {
        return new ArrayList<>(fundRepo.values());
    }

    @Override
    public FundData getFundByIndex(int index) {
        List<FundData> funds = getAllAvailableFunds();
        if (index < 1 || index > funds.size()) {
            throw new RuntimeException("Invalid fund selection.");
        }
        return funds.get(index - 1);
    }

    @Override
    public void deleteFundByIndex(int index) {
        FundData fund = getFundByIndex(index);
        fundRepo.remove(fund.getFundId());
    }

    @Override
    public void updateFundPrice(String fundId, double newPrice) {
        FundData fund = getFundById(fundId);
        if (fund != null) {
            fund.setPrice(newPrice);
            fund.setNav(newPrice);
        }
    }

    @Override
    public void runMarketChangeSimulation() {
        Random random = new Random();

        for (FundData fund : fundRepo.values()) {
            double volatility;

            switch (fund.getRiskCategory().toLowerCase()) {
                case "high": volatility = 0.10; break;
                case "medium": volatility = 0.04; break;
                case "low": volatility = 0.01; break;
                default: volatility = 0.03;
            }

            double change = (random.nextDouble() * 2 * volatility) - volatility;
            double newPrice = Math.max(0.01, fund.getPrice() * (1 + change));

            updateFundPrice(fund.getFundId(), newPrice);
        }
    }

    // INVESTMENT METHODS (BUY / SELL)
    private InvestmentData createInvestmentRecord(String userId, String fundId, String type, double amount,
                                                double units, String status) {
        InvestmentData record = new InvestmentData();
        record.setUserId(userId);
        record.setFundId(fundId);
        record.setType(type);
        record.setAmount(amount);
        record.setUnits(units);
        record.setStatus(status);
        return record;
    }

    @Override
    public InvestmentData investInFund(String userId, String fundId, double amount) {
        FundData fund = getFundById(fundId);
        if (fund == null) {
            throw new RuntimeException("Fund not found with ID: " + fundId);
        }

        // 1. Use payment sevice to process payment
        boolean success = paymentService.processPayment(userId, amount, fund.getName());

        if (!success) {
            throw new RuntimeException("Insufficient wallet balance.");
        }

        double units = amount / fund.getPrice();
        try {
            // 2. Update portfolio
            updateUserPortfolioHoldings(userId, fundId, units);

            // 3. Record history (AFTER success)
            InvestmentData record = createInvestmentRecord(
                userId, fundId, "BUY", amount, units, "SUCCESS"
            );
            historyRepo.add(record);
            return record;

        } catch (Exception e) {
            // 4. Rollback wallet if anything fails
            paymentService.processTopUp(userId, amount);
            InvestmentData failed = createInvestmentRecord(
                userId, fundId, "BUY", amount, 0, "FAILED"
            );
            historyRepo.add(failed);
            throw new RuntimeException("System error. Amount refunded.");
        }
    }


    @Override
    public void sellFund(String userId, String fundId, double unitsToSell) {
        PortfolioData portfolio = getUserPortfolio(userId);
        double ownedUnits = portfolio.getUnitsForFund(fundId);

        if (ownedUnits < unitsToSell) {
            throw new RuntimeException("Insufficient units. Owned: " + ownedUnits);
        }

        FundData fund = getFundById(fundId);
        if (fund == null) {
            throw new RuntimeException("Fund not found.");
        }

        double proceeds = unitsToSell * fund.getPrice();

        // 1. Update portfolio
        portfolio.updateHoldings(fundId, -unitsToSell);

        // 2. Credit wallet
        paymentService.processTopUp(userId, proceeds);

        InvestmentData record = createInvestmentRecord(
                userId, fundId, "SELL", proceeds, unitsToSell, "SUCCESS"
            );
            historyRepo.add(record);
    }


    @Override
    public List<InvestmentData> getInvestmentHistory(String userId) {
        return historyRepo.stream()
                .filter(h -> h.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    // PORTFOLIO METHODS
    @Override
    public PortfolioData getUserPortfolio(String userId) {
        return portfolioRepo.computeIfAbsent(userId, id -> {
            PortfolioData p = new PortfolioData();
            p.setUserId(id);
            return p;
        });
    }

    private void updateUserPortfolioHoldings(String userId, String fundId, double addedUnits) {
        PortfolioData portfolio = getUserPortfolio(userId);
        portfolio.updateHoldings(fundId, addedUnits);
    }

    @Override
    public double calculateReturns(String userId) {
        double netInvestment = historyRepo.stream()
                .filter(h -> h.getUserId().equals(userId))
                .mapToDouble(h ->
                        "BUY".equalsIgnoreCase(h.getType())
                                ? h.getAmount()
                                : -h.getAmount())
                .sum();

        double currentMarketValue = 0;
        PortfolioData portfolio = getUserPortfolio(userId);

        for (Map.Entry<String, Double> entry : portfolio.getFundHoldings().entrySet()) {
            FundData fund = fundRepo.get(entry.getKey());
            if (fund != null) {
                currentMarketValue += entry.getValue() * fund.getPrice();
            }
        }

        return currentMarketValue - netInvestment;
    }

    @Override
    public void evaluateRiskProfile(String userId, String riskCategory) {
        PortfolioData portfolio = getUserPortfolio(userId);
        portfolio.setRiskCategory(riskCategory);
    }
}
