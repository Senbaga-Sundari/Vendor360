package com.vendor360.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Vendor360 {

    // ============================================
    // SUPPLIER DETAILS
    // ============================================

    private String vendorId;
    private String vendorName;
    private String category;
    private String country;
    private String city;
    private String contactEmail;


    // ============================================
    // CONTRACT DETAILS
    // ============================================

    private String contractId;
    private String contractType;
    private BigDecimal contractValue;
    private String currency;
    private Integer paymentTermsDays;
    private Integer renewalNoticeDays;
    private String contractStatus;


    // ============================================
    // PERFORMANCE DETAILS
    // ============================================

    private String performanceId;
    private String period;
    private Integer orders;
    private Integer onTimeDeliveries;
    private Integer lateDeliveries;
    private BigDecimal qualityScore;
    private BigDecimal slaScore;
    private BigDecimal deliveryScore;


    // ============================================
    // RISK DETAILS
    // ============================================

    private String riskId;
    private Integer invoiceCount;
    private Integer invoiceDiscrepancies;
    private Integer paymentDelays;
    private Integer complianceIssues;
    private Boolean criticalDependency;
    private String riskEvent;
    private LocalDate riskDate;


    // ============================================
    // GETTERS AND SETTERS
    // ============================================


    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }


    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }


    // ============================================
    // CONTRACT GETTERS AND SETTERS
    // ============================================

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }


    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }


    public BigDecimal getContractValue() {
        return contractValue;
    }

    public void setContractValue(BigDecimal contractValue) {
        this.contractValue = contractValue;
    }


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public Integer getPaymentTermsDays() {
        return paymentTermsDays;
    }

    public void setPaymentTermsDays(Integer paymentTermsDays) {
        this.paymentTermsDays = paymentTermsDays;
    }


    public Integer getRenewalNoticeDays() {
        return renewalNoticeDays;
    }

    public void setRenewalNoticeDays(Integer renewalNoticeDays) {
        this.renewalNoticeDays = renewalNoticeDays;
    }


    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }


    // ============================================
    // PERFORMANCE GETTERS AND SETTERS
    // ============================================

    public String getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(String performanceId) {
        this.performanceId = performanceId;
    }


    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }


    public Integer getOrders() {
        return orders;
    }

    public void setOrders(Integer orders) {
        this.orders = orders;
    }


    public Integer getOnTimeDeliveries() {
        return onTimeDeliveries;
    }

    public void setOnTimeDeliveries(Integer onTimeDeliveries) {
        this.onTimeDeliveries = onTimeDeliveries;
    }


    public Integer getLateDeliveries() {
        return lateDeliveries;
    }

    public void setLateDeliveries(Integer lateDeliveries) {
        this.lateDeliveries = lateDeliveries;
    }


    public BigDecimal getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(BigDecimal qualityScore) {
        this.qualityScore = qualityScore;
    }


    public BigDecimal getSlaScore() {
        return slaScore;
    }

    public void setSlaScore(BigDecimal slaScore) {
        this.slaScore = slaScore;
    }


    public BigDecimal getDeliveryScore() {
        return deliveryScore;
    }

    public void setDeliveryScore(BigDecimal deliveryScore) {
        this.deliveryScore = deliveryScore;
    }


    // ============================================
    // RISK GETTERS AND SETTERS
    // ============================================

    public String getRiskId() {
        return riskId;
    }

    public void setRiskId(String riskId) {
        this.riskId = riskId;
    }


    public Integer getInvoiceCount() {
        return invoiceCount;
    }

    public void setInvoiceCount(Integer invoiceCount) {
        this.invoiceCount = invoiceCount;
    }


    public Integer getInvoiceDiscrepancies() {
        return invoiceDiscrepancies;
    }

    public void setInvoiceDiscrepancies(Integer invoiceDiscrepancies) {
        this.invoiceDiscrepancies = invoiceDiscrepancies;
    }


    public Integer getPaymentDelays() {
        return paymentDelays;
    }

    public void setPaymentDelays(Integer paymentDelays) {
        this.paymentDelays = paymentDelays;
    }


    public Integer getComplianceIssues() {
        return complianceIssues;
    }

    public void setComplianceIssues(Integer complianceIssues) {
        this.complianceIssues = complianceIssues;
    }


    public Boolean getCriticalDependency() {
        return criticalDependency;
    }

    public void setCriticalDependency(Boolean criticalDependency) {
        this.criticalDependency = criticalDependency;
    }


    public String getRiskEvent() {
        return riskEvent;
    }

    public void setRiskEvent(String riskEvent) {
        this.riskEvent = riskEvent;
    }


    public LocalDate getRiskDate() {
        return riskDate;
    }

    public void setRiskDate(LocalDate riskDate) {
        this.riskDate = riskDate;
    }
}