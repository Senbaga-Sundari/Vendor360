package com.vendor360.routes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class Vendor360Route extends RouteBuilder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure() {

        /*
         * ============================================================
         * SUPPLIERS JSON ROUTE
         * ============================================================
         *
         * Input:
         * data/input/supplier_profiles.json
         *
         * Output:
         * Azure SQL -> Vendor360DB -> suppliers
         *
         * Features:
         * 1. Reads supplier_profiles.json
         * 2. Parses JSON using Jackson
         * 3. Converts Yes/No -> true/false for BIT
         * 4. Converts date values
         * 5. Handles missing created_at
         * 6. Checks vendor_id before inserting
         * 7. Prevents duplicate suppliers
         * ============================================================
         */

        from("file:data/input"
                + "?include=supplier_profiles\\.json"
                + "&noop=true"
                + "&readLock=changed")
            .routeId("suppliers-json-route")

            .log("================================================")
            .log("Supplier JSON file received: ${header.CamelFileName}")
            .log("================================================")

            /*
             * Read complete JSON file.
             */
            .convertBodyTo(String.class)

            .log("Supplier JSON file loaded successfully")

            /*
             * Parse JSON array.
             */
            .process(exchange -> {

                String json =
                        exchange.getMessage().getBody(String.class);

                ArrayNode suppliers =
                        (ArrayNode) objectMapper.readTree(json);

                exchange.getMessage().setBody(suppliers);

                exchange.getMessage().setHeader(
                        "supplierCount",
                        suppliers.size()
                );
            })

            .log("Total suppliers found: ${header.supplierCount}")

            /*
             * Split JSON array.
             */
            .split(body())
                .streaming()

                /*
                 * ====================================================
                 * EXTRACT SUPPLIER DATA
                 * ====================================================
                 */
                .process(exchange -> {

                    JsonNode supplier =
                            exchange.getMessage().getBody(JsonNode.class);

                    /*
                     * ------------------------------------------------
                     * vendor_id
                     * ------------------------------------------------
                     */
                    String vendorId =
                            getText(supplier, "vendor_id");

                    if (vendorId == null) {
                        throw new IllegalArgumentException(
                                "vendor_id is missing in supplier JSON"
                        );
                    }

                    exchange.getMessage().setHeader(
                            "vendorId",
                            vendorId
                    );

                    /*
                     * ------------------------------------------------
                     * vendor_name
                     * ------------------------------------------------
                     */
                    String vendorName =
                            getText(supplier, "vendor_name");

                    if (vendorName == null) {
                        throw new IllegalArgumentException(
                                "vendor_name is missing for vendor: "
                                + vendorId
                        );
                    }

                    exchange.getMessage().setHeader(
                            "vendorName",
                            vendorName
                    );

                    /*
                     * ------------------------------------------------
                     * Optional VARCHAR fields
                     * ------------------------------------------------
                     */
                    exchange.getMessage().setHeader(
                            "category",
                            getText(supplier, "category")
                    );

                    exchange.getMessage().setHeader(
                            "country",
                            getText(supplier, "country")
                    );

                    exchange.getMessage().setHeader(
                            "city",
                            getText(supplier, "city")
                    );

                    exchange.getMessage().setHeader(
                            "contactEmail",
                            getText(supplier, "contact_email")
                    );

                    /*
                     * ------------------------------------------------
                     * DATE FIELDS
                     * ------------------------------------------------
                     */
                    exchange.getMessage().setHeader(
                            "onboardingDate",
                            parseSqlDate(
                                    getText(
                                            supplier,
                                            "onboarding_date"
                                    )
                            )
                    );

                    exchange.getMessage().setHeader(
                            "contractStartDate",
                            parseSqlDate(
                                    getText(
                                            supplier,
                                            "contract_start_date"
                                    )
                            )
                    );

                    exchange.getMessage().setHeader(
                            "contractEndDate",
                            parseSqlDate(
                                    getText(
                                            supplier,
                                            "contract_end_date"
                                    )
                            )
                    );

                    /*
                     * ------------------------------------------------
                     * criticality
                     * ------------------------------------------------
                     */
                    exchange.getMessage().setHeader(
                            "criticality",
                            getText(supplier, "criticality")
                    );

                    /*
                     * ------------------------------------------------
                     * sole_source
                     *
                     * JSON may contain:
                     *
                     * "Yes"
                     * "No"
                     *
                     * Azure SQL column:
                     *
                     * BIT
                     *
                     * Yes -> true
                     * No  -> false
                     * ------------------------------------------------
                     */
                    Boolean soleSource =
                            parseBoolean(
                                    supplier.get("sole_source")
                            );

                    /*
                     * Database column is NOT NULL.
                     *
                     * If value is missing, use false.
                     */
                    if (soleSource == null) {
                        soleSource = false;
                    }

                    exchange.getMessage().setHeader(
                            "soleSource",
                            soleSource
                    );

                    /*
                     * ------------------------------------------------
                     * status
                     * ------------------------------------------------
                     */
                    exchange.getMessage().setHeader(
                            "status",
                            getText(supplier, "status")
                    );

                    /*
                     * ------------------------------------------------
                     * created_at
                     *
                     * Database:
                     * created_at DATETIME2 NOT NULL
                     *
                     * If JSON contains a valid value:
                     *     use JSON value
                     *
                     * If JSON is missing created_at:
                     *     use current timestamp
                     * ------------------------------------------------
                     */
                    Timestamp createdAt =
                            parseTimestamp(
                                    getText(
                                            supplier,
                                            "created_at"
                                    )
                            );

                    if (createdAt == null) {

                        createdAt =
                                Timestamp.valueOf(
                                        LocalDateTime.now()
                                );

                    }

                    exchange.getMessage().setHeader(
                            "createdAt",
                            createdAt
                    );
                })

                /*
                 * ====================================================
                 * LOG DATA BEFORE DATABASE INSERT
                 * ====================================================
                 */
                .log(
                    "Processing supplier | " +
                    "Vendor ID: ${header.vendorId} | " +
                    "Name: ${header.vendorName} | " +
                    "Sole Source: ${header.soleSource} | " +
                    "Created At: ${header.createdAt}"
                )

                /*
                 * ====================================================
                 * CHECK DUPLICATE + INSERT
                 * ====================================================
                 *
                 * vendor_id is used as the duplicate key.
                 *
                 * If vendor_id already exists:
                 *     DO NOT INSERT
                 *
                 * If vendor_id does not exist:
                 *     INSERT
                 * ====================================================
                 */
                .to(
                    "sql:"
                    + "IF NOT EXISTS "
                    + "("
                    + "SELECT 1 "
                    + "FROM suppliers "
                    + "WHERE vendor_id = :#${header.vendorId}"
                    + ") "
                    + "BEGIN "

                    + "INSERT INTO suppliers "
                    + "("
                    + "vendor_id, "
                    + "vendor_name, "
                    + "category, "
                    + "country, "
                    + "city, "
                    + "contact_email, "
                    + "onboarding_date, "
                    + "contract_start_date, "
                    + "contract_end_date, "
                    + "criticality, "
                    + "sole_source, "
                    + "status, "
                    + "created_at"
                    + ") "

                    + "VALUES "
                    + "("
                    + ":#${header.vendorId}, "
                    + ":#${header.vendorName}, "
                    + ":#${header.category}, "
                    + ":#${header.country}, "
                    + ":#${header.city}, "
                    + ":#${header.contactEmail}, "
                    + ":#${header.onboardingDate}, "
                    + ":#${header.contractStartDate}, "
                    + ":#${header.contractEndDate}, "
                    + ":#${header.criticality}, "
                    + ":#${header.soleSource}, "
                    + ":#${header.status}, "
                    + ":#${header.createdAt}"
                    + ") "

                    + "END"
                )

                .log(
                    "Supplier processed successfully: "
                    + "${header.vendorId}"
                )

            .end()

            .log("================================================")
            .log("Supplier JSON processing completed")
            .log("================================================");
    }


    /*
     * ================================================================
     * GET TEXT
     * ================================================================
     */
    private String getText(
            JsonNode node,
            String fieldName) {

        if (node == null
                || !node.has(fieldName)
                || node.get(fieldName).isNull()) {

            return null;
        }

        String value =
                node.get(fieldName).asText();

        if (value == null
                || value.trim().isEmpty()) {

            return null;
        }

        return value.trim();
    }


    /*
     * ================================================================
     * YES / NO -> BOOLEAN
     * ================================================================
     *
     * Supported values:
     *
     * Yes   -> true
     * No    -> false
     * True  -> true
     * False -> false
     * Y     -> true
     * N     -> false
     * 1     -> true
     * 0     -> false
     * ================================================================
     */
    private Boolean parseBoolean(JsonNode node) {

        if (node == null || node.isNull()) {
            return null;
        }

        /*
         * JSON boolean
         */
        if (node.isBoolean()) {
            return node.asBoolean();
        }

        String value =
                node.asText();

        if (value == null) {
            return null;
        }

        value = value.trim();

        /*
         * TRUE
         */
        if (value.equalsIgnoreCase("yes")
                || value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("y")
                || value.equals("1")) {

            return Boolean.TRUE;
        }

        /*
         * FALSE
         */
        if (value.equalsIgnoreCase("no")
                || value.equalsIgnoreCase("false")
                || value.equalsIgnoreCase("n")
                || value.equals("0")) {

            return Boolean.FALSE;
        }

        throw new IllegalArgumentException(
                "Invalid sole_source value: " + value
        );
    }


    /*
     * ================================================================
     * STRING -> SQL DATE
     * ================================================================
     *
     * Supported:
     *
     * 2025-01-15
     * 2025-01-15T00:00:00
     * ================================================================
     */
    private Date parseSqlDate(String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return null;
        }

        value = value.trim();

        try {

            /*
             * Take first 10 characters:
             *
             * 2025-01-15
             */
            String datePart =
                    value.substring(0, 10);

            LocalDate date =
                    LocalDate.parse(
                            datePart,
                            DateTimeFormatter.ISO_LOCAL_DATE
                    );

            return Date.valueOf(date);

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "Invalid date value: " + value,
                    e
            );
        }
    }


    /*
     * ================================================================
     * STRING -> SQL TIMESTAMP
     * ================================================================
     *
     * Supports:
     *
     * 2025-01-15T10:30:00
     *
     * 2025-01-15T10:30:00.000
     *
     * 2025-01-15T10:30:00Z
     *
     * 2025-01-15 10:30:00
     *
     * Missing/empty value -> null
     *
     * Caller will replace null with current timestamp because
     * created_at is NOT NULL.
     * ================================================================
     */
    private Timestamp parseTimestamp(String value) {

        if (value == null
                || value.trim().isEmpty()) {

            return null;
        }

        value = value.trim();

        try {

            /*
             * Timestamp with timezone:
             *
             * 2025-01-15T10:30:00Z
             */
            if (value.endsWith("Z")
                    || value.contains("+")) {

                OffsetDateTime dateTime =
                        OffsetDateTime.parse(value);

                return Timestamp.from(
                        dateTime.toInstant()
                );
            }

            /*
             * ISO local datetime:
             *
             * 2025-01-15T10:30:00
             */
            if (value.contains("T")) {

                LocalDateTime dateTime =
                        LocalDateTime.parse(value);

                return Timestamp.valueOf(dateTime);
            }

            /*
             * SQL datetime:
             *
             * 2025-01-15 10:30:00
             */
            return Timestamp.valueOf(value);

        } catch (Exception e) {

            throw new IllegalArgumentException(
                    "Invalid created_at timestamp: "
                    + value,
                    e
            );
        }
    }
}