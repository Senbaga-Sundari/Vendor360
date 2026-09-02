package com.vendor360.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ContractsRoute extends RouteBuilder {

    @Override
    public void configure() {

        /*
         * ============================================================
         * CONTRACTS CSV ROUTE
         * ============================================================
         *
         * Reads:
         *     data/input/contracts.csv
         *
         * Inserts into:
         *     Azure SQL -> contracts table
         *
         * Duplicate check:
         *     contract_id
         *
         * If contract_id already exists:
         *     Skip the record
         *
         * If contract_id does not exist:
         *     Insert the record
         *
         * ============================================================
         */

        from("file:data/input"
                + "?include=contracts\\.csv"
                + "&noop=true"
                + "&readLock=changed")
        
            .routeId("contracts-csv-route")

            .log("================================================")
            .log("Contracts CSV file received: ${header.CamelFileName}")
            .log("================================================")

            /*
             * Read the complete CSV file.
             */
            .convertBodyTo(String.class)

            .log("Contracts CSV file loaded successfully")

            /*
             * Split the CSV file line by line.
             *
             * skipHeaderRecord = first line is the column header.
             */
            .split(body().tokenize("\n"))
                .streaming()

                .process(exchange -> {

                    String line = exchange.getIn().getBody(String.class);

                    /*
                     * Remove whitespace and Windows CR character.
                     */
                    line = line.trim();

                    /*
                     * Ignore empty lines.
                     */
                    if (line.isEmpty()) {
                        exchange.setProperty("skipRecord", true);
                        return;
                    }

                    /*
                     * Ignore CSV header.
                     */
                    if (line.toLowerCase().startsWith("contract_id,")) {
                        exchange.setProperty("skipRecord", true);
                        return;
                    }

                    exchange.setProperty("skipRecord", false);

                    /*
                     * Split CSV line.
                     *
                     * -1 ensures empty fields are retained.
                     */
                    String[] values = line.split(",", -1);

                    /*
                     * Validate column count.
                     */
                    if (values.length < 8) {

                        throw new IllegalArgumentException(
                            "Invalid contracts.csv row. Expected 8 columns but found "
                            + values.length
                            + ". Row: "
                            + line
                        );
                    }

                    /*
                     * ====================================================
                     * Extract CSV columns
                     * ====================================================
                     */

                    String contractId =
                            clean(values[0]);

                    String vendorId =
                            clean(values[1]);

                    String contractType =
                            clean(values[2]);

                    String contractValue =
                            clean(values[3]);

                    String currency =
                            clean(values[4]);

                    String paymentTermsDays =
                            clean(values[5]);

                    String renewalNoticeDays =
                            clean(values[6]);

                    String contractStatus =
                            clean(values[7]);

                    /*
                     * ====================================================
                     * Validate mandatory values
                     * ====================================================
                     */

                    if (contractId.isEmpty()) {
                        throw new IllegalArgumentException(
                            "contract_id cannot be empty. Row: " + line
                        );
                    }

                    if (vendorId.isEmpty()) {
                        throw new IllegalArgumentException(
                            "vendor_id cannot be empty for contract "
                            + contractId
                        );
                    }

                    /*
                     * ====================================================
                     * Store values in Camel headers
                     * ====================================================
                     */

                    exchange.getIn().setHeader(
                            "contractId",
                            contractId
                    );

                    exchange.getIn().setHeader(
                            "vendorId",
                            vendorId
                    );

                    exchange.getIn().setHeader(
                            "contractType",
                            contractType
                    );

                    exchange.getIn().setHeader(
                            "contractValue",
                            contractValue
                    );

                    exchange.getIn().setHeader(
                            "currency",
                            currency
                    );

                    exchange.getIn().setHeader(
                            "paymentTermsDays",
                            paymentTermsDays
                    );

                    exchange.getIn().setHeader(
                            "renewalNoticeDays",
                            renewalNoticeDays
                    );

                    exchange.getIn().setHeader(
                            "contractStatus",
                            contractStatus
                    );
                })

                /*
                 * ========================================================
                 * Skip header / empty records
                 * ========================================================
                 */
                .choice()

                    .when(exchange ->
                        Boolean.TRUE.equals(
                            exchange.getProperty("skipRecord")
                        )
                    )

                        .log("Skipping CSV header/empty record")

                    .otherwise()

                        .log(
                            "Processing contract: ${header.contractId}"
                        )

                        /*
                         * ==================================================
                         * INSERT ONLY IF CONTRACT DOES NOT ALREADY EXIST
                         * ==================================================
                         *
                         * contract_id is used for duplicate checking.
                         *
                         * created_at is generated by SQL Server using
                         * SYSDATETIME().
                         */
                        .to(
                            "sql:"
                            + "IF NOT EXISTS "
                            + "(SELECT 1 FROM contracts "
                            + "WHERE contract_id = :#${header.contractId}) "
                            + "BEGIN "
                            + "INSERT INTO contracts "
                            + "("
                            + "contract_id, "
                            + "vendor_id, "
                            + "contract_type, "
                            + "contract_value, "
                            + "currency, "
                            + "payment_terms_days, "
                            + "renewal_notice_days, "
                            + "contract_status, "
                            + "created_at"
                            + ") "
                            + "VALUES "
                            + "("
                            + ":#${header.contractId}, "
                            + ":#${header.vendorId}, "
                            + ":#${header.contractType}, "
                            + ":#${header.contractValue}, "
                            + ":#${header.currency}, "
                            + ":#${header.paymentTermsDays}, "
                            + ":#${header.renewalNoticeDays}, "
                            + ":#${header.contractStatus}, "
                            + "SYSDATETIME()"
                            + ") "
                            + "END"
                        )

                        .log(
                            "Contract processed successfully: "
                            + "${header.contractId}"
                        )

                .end();
    }


    /*
     * ================================================================
     * CLEAN CSV VALUE
     * ================================================================
     *
     * Removes:
     * - leading/trailing spaces
     * - double quotes
     * - carriage return
     */
    private String clean(String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .replace("\r", "")
                .replace("\"", "");
    }
}