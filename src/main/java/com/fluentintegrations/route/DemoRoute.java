package com.fluentintegrations.route;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

@ApplicationScoped
public class DemoRoute extends RouteBuilder {
    public static final String ROUTE_URI_PROCESS_PRODUCT = "direct:process-product";
    @Override
    public void configure() throws Exception {
        // poll google cloud storage bucket for new files
        from("google-storage://flintx_demo?serviceAccountKey={{service.account.key}}")
                .routeId("demo-google-route")
                .log("Processing new file from Google Cloud Storage ${header.CamelGoogleCloudStorageObjectName}")
                // split files into individual product records
                .split().jsonpath("$.products")
                    .log("Processing product record: ${body}")
                    // map each product record using the mapstruct
                    .to("bean:com.fluentintegrations.mapping.DemoProductMappingBean")
                    .log("FlintX product: ${body}")
                    // send each record to the flintx fluent product service
                    .marshal().json(JsonLibrary.Jackson)
                    .setHeader("Content-Type").simple("application/json")
                    .setHeader("apiKey").simple("{{flintx.apikey}}")
                    .toD("{{flintx-fluent-product-service.url}}/fluent/product/load")
                    .log("Response: ${body}")
                .end()
        ;
    }
}
