package org.flabs.common.service;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.reactivex.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.Record;

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

    protected Maybe<ServiceReference> getServiceReference(String serviceName) {
        return discoSvc.rxGetRecord(new JsonObject().put("name", serviceName))
                .map(record -> discoSvc.getReference(record));
    }

    public Maybe<Record> getServiceRecord(String serviceName) {
        return discoSvc.rxGetRecord(r -> serviceName.equalsIgnoreCase(r.getName()));

    }
}
