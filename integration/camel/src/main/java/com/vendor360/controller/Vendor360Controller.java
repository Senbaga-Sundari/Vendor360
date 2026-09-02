package com.vendor360.controller;

import com.vendor360.model.DashboardSummary;
import com.vendor360.model.Vendor360;
import com.vendor360.service.Vendor360Service;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(origins = "*")
public class Vendor360Controller {

    private final Vendor360Service service;


    public Vendor360Controller(Vendor360Service service) {
        this.service = service;
    }


    // ============================================
    // GET ALL VENDORS
    // URL: /api/vendors
    // ============================================

    @GetMapping
    public List<Vendor360> getAllVendors() {

        return service.getAllVendors();
    }


    // ============================================
    // GET DASHBOARD SUMMARY
    // URL: /api/vendors/dashboard
    // ============================================

    @GetMapping("/dashboard")
    public DashboardSummary getDashboardSummary() {

        return service.getDashboardSummary();
    }

}