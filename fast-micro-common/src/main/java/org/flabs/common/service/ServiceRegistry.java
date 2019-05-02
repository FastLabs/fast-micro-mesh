package org.flabs.common.service;

import io.reactivex.Single;
import io.vertx.servicediscovery.Record;

public interface ServiceRegistry {

    Single<ServiceProvider> getServiceProvider(String serviceName);
    Single<Record> getServiceRecord(String serviceName);
}
