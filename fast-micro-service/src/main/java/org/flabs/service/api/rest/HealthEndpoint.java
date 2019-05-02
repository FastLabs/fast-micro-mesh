package org.flabs.service.api.rest;

import org.flabs.common.service.ServiceRegistry;

public class HealthEndpoint {

    private final ServiceRegistry sr;

    public HealthEndpoint(ServiceRegistry sr) {
        this.sr = sr;
    }
}
