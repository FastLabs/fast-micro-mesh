package org.flabs.common.service;

import io.vertx.core.Verticle;
import io.vertx.core.impl.JavaVerticleFactory;
import io.vertx.core.spi.VerticleFactory;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import org.flabs.common.service.AbstractServiceVerticle;

public class DiscoServiceFactory extends JavaVerticleFactory {

    private final ServiceDiscovery serviceDiscovery;

    public DiscoServiceFactory(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }


    public String prefix() {
        return "service";
    }

    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        var verticle = super.createVerticle(VerticleFactory.removePrefix(verticleName), classLoader);
        if (verticle instanceof AbstractServiceVerticle) {
            System.out.println("Injected service discover for: " + verticleName);
            ((AbstractServiceVerticle) verticle).setDiscoService(serviceDiscovery);

        }
        return verticle;
    }
}
