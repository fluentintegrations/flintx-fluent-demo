package com.fluentintegrations.route;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

@ApplicationScoped
public class DemoRoute extends RouteBuilder {
    @Override
    public void configure() {
        declareGoogleRoute();
        declareKafkaRoute();
    }
    private void declareGoogleRoute() {
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
    private void declareKafkaRoute() {
        from("{{kafka.event.uri}}")
                .routeId("demo-kafka-route")
                .unmarshal().json(JsonLibrary.Jackson)
                .split().jsonpath("$.products").streaming()
                .log("Mapping product:${body}")
                .to("bean:com.fluentintegrations.mapping.DemoProductMappingBean")
                .log("Mapped product:${body}")
                .marshal().json(JsonLibrary.Jackson)
                .log("Sending product: ${body}")
                .setHeader(Exchange.HTTP_METHOD).simple("POST")
                .setHeader("Content-Type").simple("application/json")
                .setHeader("apiKey").simple("{{flintx.apikey}}")
                .toD("{{flintx-fluent-product-service.url}}/fluent/product/load?bridgeEndpoint=true")
                .log("Response: ${body}")
                .to("{{kafka.eventVerification.uri}}")
                .end()
        ;
    }
}
