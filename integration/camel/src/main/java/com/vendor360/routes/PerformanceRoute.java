package com.vendor360.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PerformanceRoute extends RouteBuilder {

    @Override
    public void configure() {

        /*
         * ============================================================
         * PERFORMANCE ROUTE
         * ============================================================
         *
         * Reads:
         * data/input/performance_records.csv
         *
         * Inserts into:
         * Azure SQL -> performance table
         *
         * Duplicate check:
         * performance_id
         *
         * ============================================================
         */

        from("file:data/input?include=performance_records\\.csv&noop=true")
            .routeId("performance-csv-route")

            .log("================================================")
            .log("Performance CSV file received: ${header.CamelFileName}")
            .log("================================================")

            /*
             * Read complete CSV as String.
             */
            .convertBodyTo(String.class)

            .log("Performance CSV file loaded successfully")

            /*
             * Split CSV into individual lines.
             *
             * IMPORTANT:
             * Do NOT use tokenize("\n", true)
             * because that causes the compilation error
             * you received.
             */
            .split(body().tokenize("\n"))

                .streaming()

                /*
                 * Process every CSV row.
                 */
                .process(exchange -> {

                    String line = exchange.getIn().getBody(String.class);

                    if (line == null) {
                        exchange.getIn().setHeader("skipRow", true);
                        return;
                    }

                    line = line.trim();

                    /*
                     * Skip empty lines.
                     */
                    if (line.isEmpty()) {
                        exchange.getIn().setHeader("skipRow", true);
                        return;
                    }

                    /*
                     * Skip CSV header.
                     */
                    if (line.toLowerCase().startsWith("performance_id")) {
                        exchange.getIn().setHeader("skipRow", true);
                        return;
                    }

                    exchange.getIn().setHeader("skipRow", false);

                    /*
                     * Split CSV values.
                     *
                     * -1 keeps empty values.
                     */
                    String[] values = line.split(",", -1);

                    /*
                     * Expected 9 columns:
                     *
                     * 0 = performance_id
                     * 1 = vendor_id
                     * 2 = period
                     * 3 = orders
                     * 4 = on_time_deliveries
                     * 5 = late_deliveries
                     * 6 = quality_score
                     * 7 = sla_score
                     * 8 = delivery_score
                     */

                    if (values.length < 9) {

                        throw new IllegalArgumentException(
                            "Invalid CSV row. Expected 9 columns but found "
                            + values.length
                            + " | Row: "
                            + line
                        );
                    }

                    /*
                     * Remove spaces from values.
                     */
                    String performanceId = values[0].trim();
                    String vendorId = values[1].trim();
                    String period = values[2].trim();

                    String orders = values[3].trim();
                    String onTimeDeliveries = values[4].trim();
                    String lateDeliveries = values[5].trim();

                    String qualityScore = values[6].trim();
                    String slaScore = values[7].trim();
                    String deliveryScore = values[8].trim();

                    /*
                     * Store values in Camel headers.
                     */
                    exchange.getIn().setHeader(
                        "performanceId",
                        performanceId
                    );

                    exchange.getIn().setHeader(
                        "vendorId",
                        vendorId
                    );

                    exchange.getIn().setHeader(
                        "period",
                        period
                    );

                    /*
                     * Integer values.
                     */
                    exchange.getIn().setHeader(
                        "orders",
                        orders.isEmpty() ? null : Integer.valueOf(orders)
                    );

                    exchange.getIn().setHeader(
                        "onTimeDeliveries",
                        onTimeDeliveries.isEmpty()
                            ? null
                            : Integer.valueOf(onTimeDeliveries)
                    );

                    exchange.getIn().setHeader(
                        "lateDeliveries",
                        lateDeliveries.isEmpty()
                            ? null
                            : Integer.valueOf(lateDeliveries)
                    );

                    /*
                     * Decimal values.
                     */
                    exchange.getIn().setHeader(
                        "qualityScore",
                        qualityScore.isEmpty()
                            ? null
                            : new java.math.BigDecimal(qualityScore)
                    );

                    exchange.getIn().setHeader(
                        "slaScore",
                        slaScore.isEmpty()
                            ? null
                            : new java.math.BigDecimal(slaScore)
                    );

                    exchange.getIn().setHeader(
                        "deliveryScore",
                        deliveryScore.isEmpty()
                            ? null
                            : new java.math.BigDecimal(deliveryScore)
                    );
                })

                /*
                 * Process only actual data rows.
                 * Header/empty rows are skipped.
                 */
                .choice()

                    .when(simple("${header.skipRow} == true"))

                        .log("Skipping CSV header/empty row")

                    .otherwise()

                        .log(
                            "Processing performance | "
                            + "Performance ID: ${header.performanceId} "
                            + "| Vendor ID: ${header.vendorId} "
                            + "| Period: ${header.period}"
                        )

                        /*
                         * =================================================
                         * DUPLICATE CHECK + INSERT
                         * =================================================
                         *
                         * If performance_id already exists:
                         *     Do nothing
                         *
                         * If performance_id does not exist:
                         *     Insert record
                         *
                         * created_at is generated by Azure SQL using
                         * SYSDATETIME().
                         */
                        .to(
                            "sql:"
                            + "IF NOT EXISTS "
                            + "(SELECT 1 FROM performance "
                            + "WHERE performance_id = :#${header.performanceId}) "
                            + "BEGIN "
                            + "INSERT INTO performance "
                            + "("
                            + "performance_id, "
                            + "vendor_id, "
                            + "period, "
                            + "orders, "
                            + "on_time_deliveries, "
                            + "late_deliveries, "
                            + "quality_score, "
                            + "sla_score, "
                            + "delivery_score, "
                            + "created_at"
                            + ") "
                            + "VALUES "
                            + "("
                            + ":#${header.performanceId}, "
                            + ":#${header.vendorId}, "
                            + ":#${header.period}, "
                            + ":#${header.orders}, "
                            + ":#${header.onTimeDeliveries}, "
                            + ":#${header.lateDeliveries}, "
                            + ":#${header.qualityScore}, "
                            + ":#${header.slaScore}, "
                            + ":#${header.deliveryScore}, "
                            + "SYSDATETIME()"
                            + ") "
                            + "END"
                        )

                        .log(
                            "Performance processed successfully: "
                            + "${header.performanceId}"
                        );

        /*
         * ============================================================
         * END OF ROUTE
         * ============================================================
         */

    }
}