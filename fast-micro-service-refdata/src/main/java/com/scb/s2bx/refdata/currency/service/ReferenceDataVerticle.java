package com.scb.s2bx.refdata.currency.service;

import io.reactivex.Completable;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.servicediscovery.Record;
import com.scb.s2bx.common.service.AbstractServiceVerticle;
import com.scb.s2bx.refdata.RefDataCodec;
import com.scb.s2bx.refdata.currency.model.CurrencyPair;

import java.util.ArrayList;
import java.util.List;

public class ReferenceDataVerticle extends AbstractServiceVerticle {


    @Override
    public Completable rxStop() {
        return unregisterService();
    }

    @Override
    public Completable rxStart() {
        final Record serviceRecord = CurrencyReferenceDataService.createRecord();
        var eventBus = vertx.eventBus();

        eventBus.consumer("currency-pair")
                .toObservable()
                .subscribe(msg -> {
                    var result = new CurrencyPair("USD", "EUR");

                    if (msg.body() == null) {
                        System.out.println("Query without query");
                        msg.reply(result);
                    } else {
                        System.out.println("Query with query params " + msg.body());
                        msg.reply(result);
                    }
                }, err -> {
                    System.err.println("Error when receiving messages");
                });

        eventBus.consumer("ref-data.currency-pair")
                .toObservable()
                .subscribe(msg -> {
                            System.out.println("Extracting currency pairs");
                            final List<CurrencyPair> result = new ArrayList<>();
                            result.add(new CurrencyPair("USD", "EUR"));
                            msg.reply(result, new DeliveryOptions().setCodecName(RefDataCodec.LIST_CURRENCY_PAIR_CODEC.name()));
                        }
                        , System.err::println);


        return registerService(serviceRecord)
                .ignoreElement();
    }
}
