package org.flabs.common.service;

import io.vertx.reactivex.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.Status;

import java.util.Optional;

public class ServiceProvider {
    private Status serviceStatus;
    private ServiceReference serviceReference;

    private ServiceProvider(Status serviceStatus, ServiceReference serviceReference) {
        this.serviceStatus = serviceStatus;
        this.serviceReference = serviceReference;
    }

    static ServiceProvider serviceAvailable(ServiceReference serviceReference) {
        return new ServiceProvider(Status.UP, serviceReference);
    }
    //TODO: maybe i need to propagate with other unavailability statuses
    static ServiceProvider serviceDown() {
        return new ServiceProvider(Status.DOWN, null);
    }

    public <T> Optional<T> getAs(Class<T> clzz) {
        if(serviceStatus == Status.UP) {
            return Optional.of( serviceReference.getAs(clzz));
        }
        return Optional.empty();
    }

    public void release () {
        if(serviceReference != null) {
            serviceReference.release();
        }
    }

}
