package org.flabs.refdata.currency.service;

import io.reactivex.Completable;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.servicediscovery.Record;
import org.flabs.common.service.AbstractServiceVerticle;
import org.flabs.refdata.RefDataCodec;
import org.flabs.refdata.currency.model.CurrencyPair;

import java.util.ArrayList;
import java.util.List;

public class ReferenceDataVerticle extends AbstractServiceVerticle {


    @Override
    public Completable rxStart() {
        final Record serviceRecord = CurrencyReferenceDataService.createRecord();
        var eventBus = vertx.eventBus();
        eventBus.registerCodec(RefDataCodec.CURRENCY_CODEC)
                .registerCodec(RefDataCodec.CURRENCY_PAIR_CODEC)
                .registerCodec(RefDataCodec.LIST_CURRENCY_PAIR_CODEC);

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
                            final List<CurrencyPair> result = new ArrayList<>();
                            result.add(new CurrencyPair("USD", "EUR"));
                            msg.reply(result, new DeliveryOptions().setCodecName(RefDataCodec.LIST_CURRENCY_PAIR_CODEC.name()));
                        }
                        , err -> {
                        });


        return registerService(serviceRecord).ignoreElement();
    }
}
