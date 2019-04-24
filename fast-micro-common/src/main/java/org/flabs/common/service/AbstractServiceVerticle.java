package org.flabs.common.service;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.Status;

import java.util.Optional;

public class AbstractServiceVerticle extends AbstractVerticle {
    //TODO: make it private?
    protected ServiceDiscovery discoSvc;
    private String serviceId;


    public void setDiscoService(ServiceDiscovery discovery) {
        this.discoSvc = discovery;
    }

    protected Single<Record> registerService(Record record) {
        return discoSvc
                .rxPublish(record)
                .doOnSuccess(rec -> {
                    serviceId = rec.getRegistration();
                    System.out.println("Service " + rec + " successfully deployed");
                });
    }

    protected Completable unregisterService() {
        System.out.println("Unregister service " + serviceId);
        return discoSvc.rxUnpublish(serviceId);
    }

    protected Single<ServiceProvider> getServiceProvider(String serviceName) {
        return discoSvc.rxGetRecord(new JsonObject().put("name", serviceName))
                .map(record -> {
                            if (record.getStatus() == Status.UP) {
                                return ServiceProvider.serviceAvailable(discoSvc.getReference(record));
                            } else {
                                System.out.println("Service down " + serviceName + " with status " + record.getStatus());
                                return ServiceProvider.serviceDown();
                            }
                        }
                )
                .switchIfEmpty(Single.just(ServiceProvider.serviceDown()));
    }

    public Single<Record> getServiceRecord(String serviceName) {
        return discoSvc.rxGetRecord(r -> serviceName.equalsIgnoreCase(r.getName()))
                .switchIfEmpty(Single.just(new Record()
                        .setName(serviceName)
                        .setStatus(Status.DOWN)));

    }
}
