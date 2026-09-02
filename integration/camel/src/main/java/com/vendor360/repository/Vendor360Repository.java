package com.vendor360.repository;

import com.vendor360.model.Vendor360;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Vendor360Repository {

    private final JdbcTemplate jdbcTemplate;

    public Vendor360Repository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // ============================================
    // GET ALL VENDORS
    // ============================================

    public List<Vendor360> findAll() {

        String sql = """
            SELECT *
            FROM vw_vendor360
            ORDER BY vendor_id
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapVendor(rs));
    }


    // ============================================
    // GET VENDOR BY ID
    // ============================================

    public Vendor360 findByVendorId(String vendorId) {

        String sql = """
            SELECT *
            FROM vw_vendor360
            WHERE vendor_id = ?
            """;

        List<Vendor360> vendors = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapVendor(rs),
                vendorId
        );

        if (vendors.isEmpty()) {
            return null;
        }

        return vendors.get(0);
    }


    // ============================================
    // COMMON VENDOR MAPPER
    // ============================================

    private Vendor360 mapVendor(
            java.sql.ResultSet rs)
            throws java.sql.SQLException {

        Vendor360 v = new Vendor360();


        // ============================================
        // SUPPLIER INFORMATION
        // ============================================

        v.setVendorId(rs.getString("vendor_id"));
        v.setVendorName(rs.getString("vendor_name"));
        v.setCategory(rs.getString("category"));
        v.setCountry(rs.getString("country"));
        v.setCity(rs.getString("city"));
        v.setContactEmail(rs.getString("contact_email"));


        // ============================================
        // CONTRACT INFORMATION
        // ============================================

        v.setContractId(rs.getString("contract_id"));

        v.setContractType(
                rs.getString("contract_type"));

        v.setContractValue(
                rs.getBigDecimal("contract_value"));

        v.setCurrency(
                rs.getString("currency"));

        v.setPaymentTermsDays(
                rs.getObject(
                        "payment_terms_days",
                        Integer.class));

        v.setRenewalNoticeDays(
                rs.getObject(
                        "renewal_notice_days",
                        Integer.class));

        v.setContractStatus(
                rs.getString("contract_status"));


        // ============================================
        // PERFORMANCE INFORMATION
        // ============================================

        v.setPerformanceId(
                rs.getString("performance_id"));

        v.setPeriod(
                rs.getString("period"));

        v.setOrders(
                rs.getObject(
                        "orders",
                        Integer.class));

        v.setOnTimeDeliveries(
                rs.getObject(
                        "on_time_deliveries",
                        Integer.class));

        v.setLateDeliveries(
                rs.getObject(
                        "late_deliveries",
                        Integer.class));

        v.setQualityScore(
                rs.getBigDecimal("quality_score"));

        v.setSlaScore(
                rs.getBigDecimal("sla_score"));

        v.setDeliveryScore(
                rs.getBigDecimal("delivery_score"));


        // ============================================
        // RISK INFORMATION
        // ============================================

        v.setRiskId(
                rs.getString("risk_id"));

        v.setInvoiceCount(
                rs.getObject(
                        "invoice_count",
                        Integer.class));

        v.setInvoiceDiscrepancies(
                rs.getObject(
                        "invoice_discrepancies",
                        Integer.class));

        v.setPaymentDelays(
                rs.getObject(
                        "payment_delays",
                        Integer.class));

        v.setComplianceIssues(
                rs.getObject(
                        "compliance_issues",
                        Integer.class));

        v.setCriticalDependency(
                rs.getObject(
                        "critical_dependency",
                        Boolean.class));

        v.setRiskEvent(
                rs.getString("risk_event"));


        java.sql.Date riskDate =
                rs.getDate("risk_date");

        v.setRiskDate(
                riskDate != null
                        ? riskDate.toLocalDate()
                        : null
        );


        return v;
    }


    // ============================================
    // DASHBOARD SUMMARY METHODS
    // ============================================


    // Total Vendors

    public int getTotalVendors() {

        String sql = """
            SELECT COUNT(DISTINCT vendor_id)
            FROM vw_vendor360
            """;

        Integer result =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class
                );

        return result != null ? result : 0;
    }


    // ============================================
    // ACTIVE CONTRACTS
    // ============================================

    public int getActiveContracts() {

        String sql = """
            SELECT COUNT(*)
            FROM contracts
            WHERE contract_status = 'Active'
            """;

        Integer result =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class
                );

        return result != null ? result : 0;
    }


    // ============================================
    // CRITICAL DEPENDENCIES
    // ============================================

    public int getCriticalDependencies() {

        String sql = """
            SELECT COUNT(*)
            FROM risk
            WHERE critical_dependency = 1
            """;

        Integer result =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class
                );

        return result != null ? result : 0;
    }


    // ============================================
    // HIGH RISK VENDORS
    // ============================================

    public int getHighRiskVendors() {

        String sql = """
            SELECT COUNT(*)
            FROM risk
            WHERE
                compliance_issues >= 2
                OR payment_delays >= 3
                OR invoice_discrepancies >= 5
                OR critical_dependency = 1
            """;

        Integer result =
                jdbcTemplate.queryForObject(
                        sql,
                        Integer.class
                );

        return result != null ? result : 0;
    }


    // ============================================
    // AVERAGE QUALITY SCORE
    // ============================================

    public double getAverageQualityScore() {

        String sql = """
            SELECT AVG(CAST(quality_score AS FLOAT))
            FROM performance
            """;

        Double result =
                jdbcTemplate.queryForObject(
                        sql,
                        Double.class
                );

        return result != null ? result : 0.0;
    }


    // ============================================
    // AVERAGE SLA SCORE
    // ============================================

    public double getAverageSlaScore() {

        String sql = """
            SELECT AVG(CAST(sla_score AS FLOAT))
            FROM performance
            """;

        Double result =
                jdbcTemplate.queryForObject(
                        sql,
                        Double.class
                );

        return result != null ? result : 0.0;
    }


    // ============================================
    // AVERAGE DELIVERY SCORE
    // ============================================

    public double getAverageDeliveryScore() {

        String sql = """
            SELECT AVG(CAST(delivery_score AS FLOAT))
            FROM performance
            """;

        Double result =
                jdbcTemplate.queryForObject(
                        sql,
                        Double.class
                );

        return result != null ? result : 0.0;
    }

}