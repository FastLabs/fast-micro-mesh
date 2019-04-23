package com.scb.s2bx.refdata.currency.service;

import io.reactivex.Single;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceReference;
import io.vertx.servicediscovery.spi.ServiceType;
import io.vertx.servicediscovery.types.AbstractServiceReference;
import com.scb.s2bx.refdata.currency.model.CurrencyPair;

import java.util.List;


public interface CurrencyReferenceDataService {
    String PROVIDER_ADDRESS = "ref-data.currency-pair";
    String SERVICE_NAME = "ref-data-service";

    static Record createRecord() {
        return new Record().setType(SERVICE_NAME).setName(SERVICE_NAME);
    }

    Single<List<CurrencyPair>> getCurrencyPairs();

    interface CurrencyServiceType extends ServiceType {
        @Override
        default String name() {
            return SERVICE_NAME;
        }

        @Override
        default ServiceReference get(Vertx vertx, ServiceDiscovery discovery, Record record, JsonObject configuration) {
            return new CurrencyReferenceDataServiceReference(vertx, discovery, record);
        }
    }


    class CurrencyReferenceDataServiceReference extends AbstractServiceReference<CurrencyReferenceDataService> {
        CurrencyReferenceDataServiceReference(Vertx vertx, ServiceDiscovery discovery, Record record) {
            super(vertx, discovery, record);
        }

        @Override
        protected CurrencyReferenceDataService retrieve() {
            return CurrencyReferenceDataServiceImpl.newServiceInstance(new EventBus(vertx.eventBus()));
        }
    }
}
