package com.scb.s2bx.service.example;

import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;

import io.vertx.reactivex.servicediscovery.types.MessageSource;
import com.scb.s2bx.common.service.AbstractServiceVerticle;

public class Consumer extends AbstractServiceVerticle {
    @Override
    public Completable rxStop() {
        return Completable.complete();
    }

    @Override
    public Completable rxStart() {
        //TODO: think about dispose
        var simpleServiceName = "simple-service";


        MessageSource.rxGetConsumer(discoSvc, new JsonObject().put("name", simpleServiceName))
                .flatMapObservable(consumer -> consumer.bodyStream().toObservable().cast(String.class))
                .subscribe(value -> {
                    System.out.println("Received value: " + value);
                        },
                        System.err::println,
                        ()->{
                            System.out.println("Stream Completed");
                        })
        ;
        return Completable.complete();
    }
}
