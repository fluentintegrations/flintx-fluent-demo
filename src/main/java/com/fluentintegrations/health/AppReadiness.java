package com.fluentintegrations.health;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.camel.CamelContext;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@Singleton
public class AppReadiness implements HealthCheck {

    @Inject
    CamelContext context;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder;
        if (context.isStarted()) {
            builder = HealthCheckResponse.named("AppReadiness UP").up();
        } else {
            builder = HealthCheckResponse.named("AppReadiness DOWN").down();
        }
        return builder.build();

    }
}
