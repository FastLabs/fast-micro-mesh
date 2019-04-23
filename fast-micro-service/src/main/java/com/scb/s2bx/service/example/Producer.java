package org.flabs.service.example;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.types.MessageSource;
import org.flabs.common.service.AbstractServiceVerticle;

public class Producer extends AbstractServiceVerticle {
    private static final String ENDPOINT_ADDRESS = "simpleMessage";

    private String registrationId;
    private Disposable messageStream;


    @Override
    public Completable rxStop() {
        return discoSvc
                .rxUnpublish(registrationId)
                .doAfterTerminate(() -> {
                    System.out.println("Service  " + ENDPOINT_ADDRESS + " unsubscribed");
                });
    }

    @Override
    public Completable rxStart() {
        var msgSourceRecord = MessageSource.createRecord("simple-service", ENDPOINT_ADDRESS, String.class);
        return discoSvc.rxPublish(msgSourceRecord)
                .map(this::startService)
                //.doAfterSuccess(record -> {registrationId = record.getName();})
                .doOnError(err -> {
                })
                .ignoreElement();
    }

    private Completable startService(Record serviceRecord) {
        System.out.println("Start publishing events for: " + serviceRecord.getName());
        var eventBus = vertx.eventBus();
        messageStream = vertx.periodicStream(5000)
                .toObservable()
                .subscribe(timerId -> {
                    eventBus.send(ENDPOINT_ADDRESS, "simple data");
                }, err -> {
                    System.err.println("Error when scheduling timer for " + serviceRecord.getName());
                }, () -> {
                });
        return Single.just("").ignoreElement();
    }
}
