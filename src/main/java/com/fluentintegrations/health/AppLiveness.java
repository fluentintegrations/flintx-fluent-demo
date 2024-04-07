package com.fluentintegrations.health;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.camel.CamelContext;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

@Liveness
@Singleton
public class AppLiveness implements HealthCheck {

    @Inject
    CamelContext context;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder;
        if (!context.isStopped()) {
            builder = HealthCheckResponse.named("AppLiveness UP").up();
        } else {
            builder = HealthCheckResponse.named("AppLiveness DOWN").down();
        }
        return builder.build();
    }
}
