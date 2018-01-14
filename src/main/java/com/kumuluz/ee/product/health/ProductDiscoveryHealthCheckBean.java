package com.kumuluz.ee.product.health;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Optional;


@RequestScoped
@Health
public class ProductDiscoveryHealthCheckBean implements HealthCheck {

    @Inject
    @DiscoverService(value = "priority-service", version = "1.0.x", environment = "dev")
    private Optional<WebTarget> target;

    @Override
    public HealthCheckResponse call() {

        System.out.println("qweqweqweqwewqeqweqwe HEALTH BEAN");

        if (target.isPresent()) {
            System.out.println("HEALTH BEAN TARGET NOT NULL");

            WebTarget priorityService = target.get().path("v1/priorities");

            Response response;
            try {
                response = priorityService.request().get();
                return HealthCheckResponse.named(ProductDiscoveryHealthCheckBean.class.getSimpleName()).up().build();
            } catch (ProcessingException e) {
                return HealthCheckResponse.named(ProductDiscoveryHealthCheckBean.class.getSimpleName()).down().build();
            }

        } else {
            System.out.println("HEALTH BEAN TARGET NULL");
            return HealthCheckResponse.named(ProductDiscoveryHealthCheckBean.class.getSimpleName()).down().build();
        }

    }
}