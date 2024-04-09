package com.fluentintegrations.route;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

@ApplicationScoped
public class DemoRoute extends RouteBuilder {
    public static final String ROUTE_URI_PROCESS_PRODUCT = "direct:process-product";
    @Override
    public void configure() {
        declareGoogleRoute();
        declareKafkaRoute();
        declareProcessProductRoute();
    }
    private void declareGoogleRoute() {
        // poll google cloud storage bucket for new files
        from("google-storage://flintx_demo?serviceAccountKey={{service.account.key}}")
                .routeId("demo-google-route")
                .log("Processing new file from Google Cloud Storage ${header.CamelGoogleCloudStorageObjectName}")
                // split files into individual product records
                .split().jsonpath("$.products")
                    .to(ROUTE_URI_PROCESS_PRODUCT)
                .end()
        ;
    }
    private void declareKafkaRoute() {
        from("{{kafka.event.uri}}")
                .routeId("demo-kafka-route")
                .log("Processing product update from kafka topic {{kafka.event.uri}}")
                // split message into individual product records
                .split().jsonpath("$.products").streaming()
                    .to(ROUTE_URI_PROCESS_PRODUCT)
                    .to("{{kafka.eventVerification.uri}}")
                .end()
        ;
    }

    private void declareProcessProductRoute() {
        from(ROUTE_URI_PROCESS_PRODUCT)
                .log("Mapping product:${body}")
                // map each product record using the mapstruct
                .to("bean:com.fluentintegrations.mapping.DemoProductMappingBean")
                .log("Mapped product:${body}")
                .marshal().json(JsonLibrary.Jackson)
                // send each record to the flintx fluent product service
                .log("Sending product: ${body}")
                .setHeader(Exchange.HTTP_METHOD).simple("POST")
                .setHeader("Content-Type").simple("application/json")
                .setHeader("apiKey").simple("{{flintx.apikey}}")
                .toD("{{flintx-fluent-product-service.url}}/fluent/product/load?bridgeEndpoint=true")
                .log("Response: ${body}")
        ;

    }
}

