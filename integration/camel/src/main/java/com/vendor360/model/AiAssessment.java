package com.vendor360.model;

public class AiAssessment {

    private String vendorId;
    private String vendorName;
    private String assessment;

    public AiAssessment() {
    }

    public AiAssessment(
            String vendorId,
            String vendorName,
            String assessment) {

        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.assessment = assessment;
    }

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

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }
}
