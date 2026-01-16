package com.tng;

public class FundData {
    private String fundId;
    private String name;
    private String description;
    private String riskCategory;
    private double nav; // Net Asset Value
    private double price;

    public FundData() {}

    public FundData(String fundId, String name, String description, String riskCategory, double nav) {
        this.fundId = fundId;
        this.name = name;
        this.description = description;
        this.riskCategory = riskCategory;
        this.nav = nav;
        this.price = nav;
    }

    // Getters and Setters
    public String getFundId() { return fundId; }
    public void setFundId(String fundId) { this.fundId = fundId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRiskCategory() { return riskCategory; }
    public void setRiskCategory(String riskCategory) { this.riskCategory = riskCategory; }

    public double getNav() { return nav; }
    public void setNav(double nav) { this.nav = nav; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getLatestNAV() {
        return this.nav;
    }
}