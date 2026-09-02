package com.vendor360.model;

public class DashboardSummary {

    private int totalVendors;
    private int activeContracts;
    private int highRiskVendors;
    private int criticalDependencies;

    private double averageQualityScore;
    private double averageSlaScore;
    private double averageDeliveryScore;


    public int getTotalVendors() {
        return totalVendors;
    }

    public void setTotalVendors(int totalVendors) {
        this.totalVendors = totalVendors;
    }


    public int getActiveContracts() {
        return activeContracts;
    }

    public void setActiveContracts(int activeContracts) {
        this.activeContracts = activeContracts;
    }


    public int getHighRiskVendors() {
        return highRiskVendors;
    }

    public void setHighRiskVendors(int highRiskVendors) {
        this.highRiskVendors = highRiskVendors;
    }


    public int getCriticalDependencies() {
        return criticalDependencies;
    }

    public void setCriticalDependencies(int criticalDependencies) {
        this.criticalDependencies = criticalDependencies;
    }


    public double getAverageQualityScore() {
        return averageQualityScore;
    }

    public void setAverageQualityScore(double averageQualityScore) {
        this.averageQualityScore = averageQualityScore;
    }


    public double getAverageSlaScore() {
        return averageSlaScore;
    }

    public void setAverageSlaScore(double averageSlaScore) {
        this.averageSlaScore = averageSlaScore;
    }


    public double getAverageDeliveryScore() {
        return averageDeliveryScore;
    }

    public void setAverageDeliveryScore(double averageDeliveryScore) {
        this.averageDeliveryScore = averageDeliveryScore;
    }
}