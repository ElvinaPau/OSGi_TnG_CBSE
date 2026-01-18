package com.tng.investment.impl;

import com.tng.*;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component(service = InvestmentService.class)
public class InvestmentServiceImpl implements InvestmentService {

    private final Map<String, FundData> fundRepo = Collections.synchronizedMap(new LinkedHashMap<>());
    private final Map<String, PortfolioData> portfolioRepo = new ConcurrentHashMap<>();
    private final List<InvestmentData> historyRepo =
            Collections.synchronizedList(new ArrayList<>());

    @Reference
    private PaymentService paymentService;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    private NotificationService notificationService;

    @Activate
    public void activate() {
        fundRepo.clear();
        initSampleFunds();
    }

    // Initialize sample Fund Data
    private void initSampleFunds() {
        if (!fundRepo.isEmpty()) return;
        addFund(new FundData("F01", "Low Risk Income Fund", "Bonds and steady interest.", "Low", 1.0000));
        addFund(new FundData("F02", "Balanced Global Fund", "International stocks.", "Medium", 2.5000));
        addFund(new FundData("F03", "Equity Growth Fund", "High-growth tech markets.", "High", 5.7500));
        addFund(new FundData("F04", "Digital Assets Fund", "Blockchain and crypto.", "High", 10.2000));
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
    public void runMarketChangeSimulation(String phoneNumber) {
        Random random = new Random();
        for (FundData fund : fundRepo.values()) {
            double volatility;
            switch (fund.getRiskCategory().toLowerCase()) {
                case "high":   volatility = 0.10; break;
                case "medium": volatility = 0.04; break;
                case "low":    volatility = 0.01; break;
                default:       volatility = 0.03;
            }
            double oldPrice = fund.getPrice();
            double changePercent = (random.nextDouble() * 2 * volatility) - volatility;
            double rawNewPrice = oldPrice * (1 + changePercent);
            double finalPrice = Math.max(0.01, rawNewPrice);
            double roundedPrice = Math.round(finalPrice * 10000.0) / 10000.0;

            updateFundPrice(fund.getFundId(), roundedPrice);
            System.out.printf("[Market] %-25s: RM %8.4f -> RM %8.4f (%+.2f%%)%n", 
                fund.getName(), oldPrice, roundedPrice, changePercent * 100);
            
            // Notify user only if fluctuation > 5%
            if (phoneNumber != null && !phoneNumber.isBlank() && notificationService != null) {
                double absChangePercent = Math.abs(changePercent * 100);
                if (absChangePercent > 5.0) {
                    notificationService.generateNotification(phoneNumber, "MARKET", 
                        "Market Alert: " + fund.getName() + " fluctuated significantly! RM " + String.format("%.4f", oldPrice) + 
                        " → RM " + String.format("%.4f", roundedPrice) + " (" + String.format("%+.2f", changePercent * 100) + "%)");
                }
            }
        }
    }

    // INVESTMENT METHODS (BUY / SELL)
    private InvestmentData createInvestmentRecord(String username, String fundId, String type, double amount,
                                                double units, String status) {
        InvestmentData record = new InvestmentData();
        record.setUserId(username);
        record.setFundId(fundId);
        record.setType(type);
        record.setAmount(amount);
        record.setUnits(units);
        record.setStatus(status);
        return record;
    }

    @Override
    public InvestmentData investInFund(String phoneNumber, String username, String fundId, double amount) {
        FundData fund = getFundById(fundId);
        if (fund == null) {
            throw new RuntimeException("Fund not found with ID: " + fundId);
        }

        // 1. Use payment sevice to process payment
        boolean success = paymentService.processPayment(phoneNumber, username, amount, fund.getName());

        if (!success) {
            throw new RuntimeException("Insufficient wallet balance.");
        }

        double units = amount / fund.getPrice();
        try {
            // 2. Update portfolio
            updateUserPortfolioHoldings(username, fundId, units);

            // 3. Record history (AFTER success)
            InvestmentData record = createInvestmentRecord(
                username, fundId, "BUY", amount, units, "SUCCESS"
            );
            historyRepo.add(record);
            
            return record;

        } catch (Exception e) {
            // 4. Rollback wallet if anything fails
            paymentService.processTopUp(phoneNumber, username, amount);
            InvestmentData failed = createInvestmentRecord(
                username, fundId, "BUY", amount, 0, "FAILED"
            );
            historyRepo.add(failed);
            
            throw new RuntimeException("System error. Amount refunded.");
        }
    }


    @Override
    public void sellFund(String phoneNumber, String username, String fundId, double unitsToSell) {
        PortfolioData portfolio = getUserPortfolio(username);
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
        paymentService.processTopUp(phoneNumber, username, proceeds);

        InvestmentData record = createInvestmentRecord(
                username, fundId, "SELL", proceeds, unitsToSell, "SUCCESS"
            );
            historyRepo.add(record);
    }


    @Override
    public List<InvestmentData> getInvestmentHistory(String username) {
        return historyRepo.stream()
                .filter(h -> h.getUserId().equals(username))
                .collect(Collectors.toList());
    }

    // PORTFOLIO METHODS
    @Override
    public PortfolioData getUserPortfolio(String username) {
        return portfolioRepo.computeIfAbsent(username, id -> {
            PortfolioData p = new PortfolioData();
            p.setUserId(id);
            return p;
        });
    }

    private void updateUserPortfolioHoldings(String username, String fundId, double addedUnits) {
        PortfolioData portfolio = getUserPortfolio(username);
        portfolio.updateHoldings(fundId, addedUnits);
    }

    @Override
    public double calculateReturns(String username) {
        double netInvestment = historyRepo.stream()
                .filter(h -> h.getUserId().equals(username))
                .mapToDouble(h ->
                        "BUY".equalsIgnoreCase(h.getType())
                                ? h.getAmount()
                                : -h.getAmount())
                .sum();

        double currentMarketValue = 0;
        PortfolioData portfolio = getUserPortfolio(username);

        for (Map.Entry<String, Double> entry : portfolio.getFundHoldings().entrySet()) {
            FundData fund = fundRepo.get(entry.getKey());
            if (fund != null) {
                currentMarketValue += entry.getValue() * fund.getPrice();
            }
        }

        return currentMarketValue - netInvestment;
    }

    @Override
    public void evaluateRiskProfile(String username, String riskCategory) {
        PortfolioData portfolio = getUserPortfolio(username);
        portfolio.setRiskCategory(riskCategory);
    }
}
