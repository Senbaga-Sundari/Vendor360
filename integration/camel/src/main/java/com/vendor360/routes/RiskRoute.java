package com.vendor360.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RiskRoute extends RouteBuilder {

    @Override
    public void configure() {

        /*
         * ============================================================
         * RISK ROUTE
         * ============================================================
         *
         * Reads:
         * data/input/risk_signals.csv
         *
         * Inserts into:
         * Azure SQL -> risk table
         *
         * Features:
         * 1. Reads CSV file
         * 2. Skips CSV header
         * 3. Converts Yes/No to 1/0 for BIT column
         * 4. Inserts only if risk_id does not already exist
         * 5. SQL Server generates created_at
         * 6. Handles empty risk_date
         * 7. Logs every record
         *
         * ============================================================
         */

        from("file:data/input?include=risk_signals\\.csv&noop=true")
            .routeId("risk-csv-route")

            .log("================================================")
            .log("Risk CSV file received: ${header.CamelFileName}")
            .log("================================================")

            /*
             * Convert complete CSV file to String
             */
            .convertBodyTo(String.class)

            .log("Risk CSV file loaded successfully")

            /*
             * Split file line by line.
             *
             * IMPORTANT:
             * Do NOT use .tokenize(",", true)
             * because it causes the error you received.
             */
            .split().tokenize("\n")
                .streaming()

                /*
                 * Process every CSV line
                 */
                .process(exchange -> {

                    String line = exchange.getIn().getBody(String.class);

                    if (line == null) {
                        exchange.getIn().setHeader("skipRow", true);
                        return;
                    }

                    line = line.trim();

                    /*
                     * Skip empty lines
                     */
                    if (line.isEmpty()) {
                        exchange.getIn().setHeader("skipRow", true);
                        return;
                    }

                    /*
                     * Skip CSV header
                     */
                    if (line.toLowerCase().startsWith("risk_id,")) {
                        exchange.getIn().setHeader("skipRow", true);
                        return;
                    }

                    exchange.getIn().setHeader("skipRow", false);

                    /*
                     * ------------------------------------------------
                     * Parse CSV
                     * ------------------------------------------------
                     *
                     * Expected format:
                     *
                     * risk_id,
                     * vendor_id,
                     * invoice_count,
                     * invoice_discrepancies,
                     * payment_delays,
                     * compliance_issues,
                     * critical_dependency,
                     * risk_event,
                     * risk_date
                     *
                     */

                    String[] fields = line.split(",", -1);

                    /*
                     * Validate number of columns
                     */
                    if (fields.length < 9) {
                        throw new IllegalArgumentException(
                            "Invalid risk CSV row. Expected 9 columns but found "
                            + fields.length
                            + ". Row: "
                            + line
                        );
                    }

                    /*
                     * Remove spaces around values
                     */
                    String riskId =
                            fields[0].trim();

                    String vendorId =
                            fields[1].trim();

                    String invoiceCount =
                            fields[2].trim();

                    String invoiceDiscrepancies =
                            fields[3].trim();

                    String paymentDelays =
                            fields[4].trim();

                    String complianceIssues =
                            fields[5].trim();

                    String criticalDependency =
                            fields[6].trim();

                    String riskEvent =
                            fields[7].trim();

                    String riskDate =
                            fields[8].trim();

                    /*
                     * ------------------------------------------------
                     * Convert Critical Dependency
                     * ------------------------------------------------
                     *
                     * CSV:
                     * Yes / No
                     *
                     * Azure SQL:
                     * BIT
                     *
                     * Yes -> 1
                     * No  -> 0
                     */
                    int criticalDependencyBit;

                    if ("yes".equalsIgnoreCase(criticalDependency)
                            || "true".equalsIgnoreCase(criticalDependency)
                            || "1".equals(criticalDependency)) {

                        criticalDependencyBit = 1;

                    } else {

                        criticalDependencyBit = 0;
                    }

                    /*
                     * ------------------------------------------------
                     * Handle risk_event
                     * ------------------------------------------------
                     *
                     * If value is "None", store NULL.
                     */
                    String riskEventValue = riskEvent;

                    if (riskEventValue.isEmpty()
                            || "none".equalsIgnoreCase(riskEventValue)
                            || "null".equalsIgnoreCase(riskEventValue)) {

                        riskEventValue = null;
                    }

                    /*
                     * ------------------------------------------------
                     * Handle risk_date
                     * ------------------------------------------------
                     *
                     * Empty date will be stored as NULL.
                     */
                    String riskDateValue = riskDate;

                    if (riskDateValue.isEmpty()
                            || "none".equalsIgnoreCase(riskDateValue)
                            || "null".equalsIgnoreCase(riskDateValue)) {

                        riskDateValue = null;
                    }

                    /*
                     * ------------------------------------------------
                     * Set Camel headers
                     * ------------------------------------------------
                     */

                    exchange.getIn().setHeader(
                            "riskId",
                            riskId
                    );

                    exchange.getIn().setHeader(
                            "vendorId",
                            vendorId
                    );

                    exchange.getIn().setHeader(
                            "invoiceCount",
                            invoiceCount
                    );

                    exchange.getIn().setHeader(
                            "invoiceDiscrepancies",
                            invoiceDiscrepancies
                    );

                    exchange.getIn().setHeader(
                            "paymentDelays",
                            paymentDelays
                    );

                    exchange.getIn().setHeader(
                            "complianceIssues",
                            complianceIssues
                    );

                    exchange.getIn().setHeader(
                            "criticalDependency",
                            criticalDependencyBit
                    );

                    exchange.getIn().setHeader(
                            "riskEvent",
                            riskEventValue
                    );

                    exchange.getIn().setHeader(
                            "riskDate",
                            riskDateValue
                    );
                })

                /*
                 * ====================================================
                 * Skip header / empty rows
                 * ====================================================
                 */
                .choice()

                    .when(simple("${header.skipRow} == true"))

                        .log("Skipping CSV header/empty row")

                    .otherwise()

                        /*
                         * ====================================================
                         * Log record before insertion
                         * ====================================================
                         */
                        .log(
                            "Processing risk | "
                            + "Risk ID: ${header.riskId} | "
                            + "Vendor ID: ${header.vendorId} | "
                            + "Critical Dependency: ${header.criticalDependency} | "
                            + "Risk Event: ${header.riskEvent} | "
                            + "Risk Date: ${header.riskDate}"
                        )

                        /*
                         * ====================================================
                         * INSERT ONLY IF NOT ALREADY PRESENT
                         * ====================================================
                         *
                         * risk_id is the primary key.
                         *
                         * If risk_id already exists:
                         *     Skip the record.
                         *
                         * If risk_id does not exist:
                         *     Insert it.
                         *
                         * created_at is generated by SQL Server.
                         */
                        .to(
                            "sql:"
                            + "IF NOT EXISTS "
                            + "("
                            + "SELECT 1 "
                            + "FROM risk "
                            + "WHERE risk_id = :#${header.riskId}"
                            + ") "
                            + "BEGIN "

                            + "INSERT INTO risk "
                            + "("
                            + "risk_id, "
                            + "vendor_id, "
                            + "invoice_count, "
                            + "invoice_discrepancies, "
                            + "payment_delays, "
                            + "compliance_issues, "
                            + "critical_dependency, "
                            + "risk_event, "
                            + "risk_date, "
                            + "created_at"
                            + ") "

                            + "VALUES "
                            + "("
                            + ":#${header.riskId}, "
                            + ":#${header.vendorId}, "
                            + ":#${header.invoiceCount}, "
                            + ":#${header.invoiceDiscrepancies}, "
                            + ":#${header.paymentDelays}, "
                            + ":#${header.complianceIssues}, "
                            + ":#${header.criticalDependency}, "
                            + ":#${header.riskEvent}, "
                            + "TRY_CONVERT(date, :#${header.riskDate}), "
                            + "SYSDATETIME()"
                            + ") "

                            + "END"
                        )

                        /*
                         * ====================================================
                         * Success log
                         * ====================================================
                         */
                        .log(
                            "Risk processed successfully: "
                            + "${header.riskId}"
                        );

    }
}