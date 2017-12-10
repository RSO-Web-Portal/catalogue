package com.kumuluz.ee.product.health;

/*import com.kumuluz.ee.discovery.annotations.DiscoverService;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Scope;
import java.util.Optional;

@Health
@ApplicationScoped
public class ProductHealthCheckBean implements HealthCheck {


    @Inject
    @DiscoverService(value = "account-service", version = "1.0.x", environment = "dev")
    private Optional<String> target;

    public HealthCheckResponse call() {
        //if (target.isPresent()) {
            return HealthCheckResponse.named(ProductHealthCheckBean.class.getSimpleName()).up().build();
       // } else {
            //return HealthCheckResponse.named(ProductHealthCheckBean.class.getSimpleName()).down().build();
        //}

    }

}*/
