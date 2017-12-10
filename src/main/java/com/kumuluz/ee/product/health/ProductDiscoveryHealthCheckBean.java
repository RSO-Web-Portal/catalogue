package com.kumuluz.ee.product.health;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.ws.rs.client.WebTarget;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Logger;

public class ProductDiscoveryHealthCheckBean implements HealthCheck {

    @Inject
    @DiscoverService(value = "account-service", version = "1.0.x", environment = "dev")
    private Optional<WebTarget> target;

    @Override
    public HealthCheckResponse call() {

        if (target != null) {
            return HealthCheckResponse.named(ProductDiscoveryHealthCheckBean.class.getSimpleName()).up().build();
        } else {
            return HealthCheckResponse.named(ProductDiscoveryHealthCheckBean.class.getSimpleName()).down().build();
        }

    }
}