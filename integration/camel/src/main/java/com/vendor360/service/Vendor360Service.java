package com.vendor360.service;

import com.vendor360.model.DashboardSummary;
import com.vendor360.model.Vendor360;
import com.vendor360.repository.Vendor360Repository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Vendor360Service {


    private final Vendor360Repository repository;

    private final OllamaService ollamaService;


    public Vendor360Service(

            Vendor360Repository repository,

            OllamaService ollamaService

    ) {

        this.repository = repository;

        this.ollamaService = ollamaService;
    }


    // ============================================
    // GET ALL VENDORS
    // ============================================

    public List<Vendor360> getAllVendors() {

        return repository.findAll();
    }


    // ============================================
    // GET VENDOR BY ID
    // ============================================

    public Vendor360 getVendorById(
            String vendorId) {

        return repository.findByVendorId(
                vendorId
        );
    }


    // ============================================
    // AI VENDOR ASSESSMENT
    // ============================================

    public String getAiAssessment(
            String vendorId) {


        Vendor360 vendor =
                repository.findByVendorId(
                        vendorId
                );


        if (vendor == null) {

            return "Vendor not found: "
                    + vendorId;
        }


        return ollamaService
                .generateVendorAssessment(
                        vendor
                );
    }


    // ============================================
    // DASHBOARD SUMMARY
    // ============================================

    public DashboardSummary getDashboardSummary() {

        DashboardSummary summary =
                new DashboardSummary();


        summary.setTotalVendors(
                repository.getTotalVendors()
        );


        summary.setActiveContracts(
                repository.getActiveContracts()
        );


        summary.setHighRiskVendors(
                repository.getHighRiskVendors()
        );


        summary.setCriticalDependencies(
                repository.getCriticalDependencies()
        );


        summary.setAverageQualityScore(
                repository.getAverageQualityScore()
        );


        summary.setAverageSlaScore(
                repository.getAverageSlaScore()
        );


        summary.setAverageDeliveryScore(
                repository.getAverageDeliveryScore()
        );


        return summary;
    }


    // ============================================
    // INDIVIDUAL DASHBOARD METHODS
    // ============================================

    public int getTotalVendors() {

        return repository.getTotalVendors();
    }


    public int getActiveContracts() {

        return repository.getActiveContracts();
    }


    public int getCriticalDependencies() {

        return repository.getCriticalDependencies();
    }


    public int getHighRiskVendors() {

        return repository.getHighRiskVendors();
    }


    public double getAverageQualityScore() {

        return repository
                .getAverageQualityScore();
    }


    public double getAverageSlaScore() {

        return repository
                .getAverageSlaScore();
    }


    public double getAverageDeliveryScore() {

        return repository
                .getAverageDeliveryScore();
    }

}