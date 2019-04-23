package com.scb.s2bx.service.example;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

public class StreamingVerticle extends AbstractVerticle {


    private JsonObject getMessage(String sender) {
        return new JsonObject().put("sender", sender);
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        final EventBus eventBus = vertx.eventBus();

        vertx.setPeriodic(5000, (id) -> {
            System.out.println("Sending message to chanel 1");
            eventBus.publish("tick-address", getMessage("Sender 1"));
        });

        vertx.setPeriodic(2000, (id) -> {
            System.out.println("Sending message to chanel 2");
            eventBus.publish("tick-address-1", getMessage("Sender 2"));
        });


        startFuture.complete();
    }
}
