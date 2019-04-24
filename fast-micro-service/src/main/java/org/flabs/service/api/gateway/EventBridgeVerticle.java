package org.flabs.service.api.gateway;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.vertx.ext.bridge.BridgeOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.eventbus.bridge.tcp.TcpEventBusBridge;


//TODO: could be the case in a separate project
public class EventBridgeVerticle extends AbstractVerticle {
    @Override
    public Completable rxStart() {

        TcpEventBusBridge bridge = TcpEventBusBridge.create(
                vertx,
                new BridgeOptions()
                        .addInboundPermitted(new PermittedOptions().setAddress("in"))
                        .addOutboundPermitted(new PermittedOptions().setAddress("out")));

        bridge.listen(7000, res -> {
            if (res.succeeded()) {
                System.out.println("EventBus bridge is up and running");
            } else {
                System.out.println("Error starting the event buss: " + res.cause());
            }
        });
        return Observable.empty().ignoreElements();
    }
}
