package com.tng;

import java.util.Date;

public class InvestmentData {

    private String investmentHistoryId;
    private String fundId;
    private String userId; 
    private String type;
    private double amount;
    private double units;
    private String status;
    private Date timestamp;

    public InvestmentData() {
        this.timestamp = new Date();
    }

    public double calculateUnits(double price) {
        if (price <= 0) return 0;
        this.units = this.amount / price;
        return this.units;
    }

    // Getters and Setters
    public String getInvestmentHistoryId() { return investmentHistoryId; }
    public void setInvestmentHistoryId(String id) { this.investmentHistoryId = id; }

    public String getFundId() { return fundId; }
    public void setFundId(String fundId) { this.fundId = fundId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public double getUnits() { return units; }
    public void setUnits(double units) { this.units = units; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}