package org.flabs.refdata.currency.service;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.rxjava.ext.unit.TestContext;
import org.flabs.refdata.RefDataCodec;
import org.flabs.refdata.currency.model.Currency;
import org.flabs.refdata.currency.model.CurrencyPair;
import org.flabs.refdata.currency.service.CurrencyReferenceDataServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class CurrencyRefDataServiceTest {

    /*Test lower level messaging api*/
    @Test
    void codecTest(Vertx vertx, VertxTestContext testContext) {
        var eventBus = vertx.eventBus();
        for (var codec : RefDataCodec.values()) {
            eventBus.getDelegate().registerCodec(codec);
        }

        eventBus.consumer("test")
                .toObservable()
                .subscribe(msg -> {
                            System.out.println("Received currency: " + msg.body());
                            assertEquals(new Currency("usd"), msg.body());
                            msg.reply(new CurrencyPair("usd", "eur"), new DeliveryOptions().setCodecName(RefDataCodec.CURRENCY_PAIR_CODEC.name()));
                        },
                        testContext::failNow);

        eventBus.rxSend("test", new Currency("usd"), new DeliveryOptions()
                .setCodecName(RefDataCodec.CURRENCY_CODEC.name()))
                .subscribe(msg -> {
                    var ccyPair = msg.body();
                    System.out.println("Received currency pair: " + ccyPair);
                    assertEquals(new CurrencyPair("usd", "eur"), ccyPair);
                    testContext.completeNow();
                }, testContext::failNow);
    }

    @Test
    void testService(Vertx vertx, VertxTestContext testContext) {
        var currencyRefDataService = CurrencyReferenceDataServiceImpl.newServiceInstance(vertx.eventBus());
        vertx.rxDeployVerticle(new ReferenceDataVerticle())
                .flatMap(vId -> currencyRefDataService.getCurrencyPairs(new CurrencyDataQuery("usd")))
                .subscribe(pairList -> {
                    System.out.println(pairList);
                    assertEquals(1, pairList.size());
                    testContext.completeNow();

                }, testContext::failNow);


    }
}
