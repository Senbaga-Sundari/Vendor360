package com.vendor360.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.vendor360.model.Vendor360;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class OllamaService {


    @Value("${ollama.base-url}")
    private String ollamaBaseUrl;


    @Value("${ollama.model}")
    private String ollamaModel;


    // ============================================
    // GENERATE AI VENDOR ASSESSMENT
    // ============================================

    public String generateVendorAssessment(
            Vendor360 vendor) {

        try {

            String prompt =
                    buildPrompt(vendor);


            URL url =
                    new URL(
                            ollamaBaseUrl
                                    + "/api/generate"
                    );


            HttpURLConnection connection =
                    (HttpURLConnection)
                            url.openConnection();


            connection.setRequestMethod("POST");

            connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );

            connection.setDoOutput(true);

            connection.setConnectTimeout(10000);

            connection.setReadTimeout(120000);


            ObjectMapper mapper =
                    new ObjectMapper();


            String requestBody =
                    mapper.writeValueAsString(
                            new OllamaRequest(
                                    ollamaModel,
                                    prompt,
                                    false
                            )
                    );


            try (
                    OutputStream outputStream =
                            connection.getOutputStream()
            ) {

                outputStream.write(
                        requestBody.getBytes(
                                StandardCharsets.UTF_8
                        )
                );
            }


            int responseCode =
                    connection.getResponseCode();


            if (responseCode != 200) {

                return "Ollama AI service returned error: "
                        + responseCode;
            }


            JsonNode response =
                    mapper.readTree(
                            connection.getInputStream()
                    );


            return response
                    .path("response")
                    .asText();


        } catch (Exception e) {

            e.printStackTrace();

            return "Unable to generate AI assessment. "
                    + "Error: "
                    + e.getMessage();
        }
    }


    // ============================================
    // BUILD AI PROMPT
    // ============================================

    private String buildPrompt(
            Vendor360 vendor) {

        return """
You are an AI Vendor Risk Analyst working for a Vendor Management System.

Analyze the vendor data below.

============================================
VENDOR INFORMATION
============================================

Vendor ID: %s
Vendor Name: %s
Category: %s
Country: %s
City: %s


============================================
CONTRACT INFORMATION
============================================

Contract Type: %s
Contract Value: %s
Currency: %s
Contract Status: %s


============================================
PERFORMANCE INFORMATION
============================================

Quality Score: %s
SLA Score: %s
Delivery Score: %s

Orders: %s
On-Time Deliveries: %s
Late Deliveries: %s


============================================
RISK INFORMATION
============================================

Invoice Count: %s
Invoice Discrepancies: %s
Payment Delays: %s
Compliance Issues: %s
Critical Dependency: %s
Risk Event: %s


Analyze the vendor and provide the response
using EXACTLY the following format:

OVERALL RISK LEVEL:
Low / Medium / High

KEY RISK FACTORS:
- List important risk factors.

PERFORMANCE ANALYSIS:
- Briefly analyze vendor performance.

RECOMMENDED ACTION:
- Provide practical recommendations.

Keep the response concise and professional.

Do not invent information.
Use only the data provided.
"""
                .formatted(

                        safe(vendor.getVendorId()),
                        safe(vendor.getVendorName()),
                        safe(vendor.getCategory()),
                        safe(vendor.getCountry()),
                        safe(vendor.getCity()),


                        safe(vendor.getContractType()),
                        safe(vendor.getContractValue()),
                        safe(vendor.getCurrency()),
                        safe(vendor.getContractStatus()),


                        safe(vendor.getQualityScore()),
                        safe(vendor.getSlaScore()),
                        safe(vendor.getDeliveryScore()),

                        safe(vendor.getOrders()),
                        safe(vendor.getOnTimeDeliveries()),
                        safe(vendor.getLateDeliveries()),


                        safe(vendor.getInvoiceCount()),
                        safe(
                                vendor.getInvoiceDiscrepancies()
                        ),

                        safe(
                                vendor.getPaymentDelays()
                        ),

                        safe(
                                vendor.getComplianceIssues()
                        ),

                        safe(
                                vendor.getCriticalDependency()
                        ),

                        safe(
                                vendor.getRiskEvent()
                        )
                );
    }


    // ============================================
    // NULL SAFE METHOD
    // ============================================

    private String safe(Object value) {

        return value != null
                ? value.toString()
                : "Not Available";
    }


    // ============================================
    // OLLAMA REQUEST MODEL
    // ============================================

    private static class OllamaRequest {

        private String model;

        private String prompt;

        private boolean stream;


        public OllamaRequest(
                String model,
                String prompt,
                boolean stream) {

            this.model = model;
            this.prompt = prompt;
            this.stream = stream;
        }


        public String getModel() {

            return model;
        }


        public String getPrompt() {

            return prompt;
        }


        public boolean isStream() {

            return stream;
        }

    }

}