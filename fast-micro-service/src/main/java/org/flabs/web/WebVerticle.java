package org.flabs.web;

import com.google.gson.Gson;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.reactivex.servicediscovery.ServiceReference;
import org.flabs.common.service.AbstractServiceVerticle;
import org.flabs.refdata.currency.model.CurrencyPair;
import org.flabs.refdata.currency.service.CurrencyReferenceDataService;

import java.util.List;

public class WebVerticle extends AbstractServiceVerticle {

    private static final int DEFAULT_PORT = 8080;
    private HttpServer server;

    private final Gson gson = new Gson();

    private void setServer(HttpServer server) {
        this.server = server;
    }

    private io.vertx.reactivex.ext.web.Router getApiRouter() {
        var apiRouter = Router.router(vertx);
        apiRouter.route().handler(BodyHandler.create());
        apiRouter.route().consumes("application/json");
        apiRouter.route().produces("application/json");
        apiRouter.get("/currencyPair").handler(rc -> {

            //TODO: switch when empty or this should be handled at the level of extraction method
            getServiceReference(CurrencyReferenceDataService.SERVICE_NAME)
                    .flatMapObservable(serviceReference -> serviceReference.getAs(CurrencyReferenceDataService.class).getCurrencyPairs().toObservable())
                    .subscribe(currencies -> {
                        var response = rc.response();
                        response.end(gson.toJson(currencies));
                    }, System.out::println, System.err::println);
        });

        return apiRouter;
    }


    @Override
    public Completable rxStart() {
        var httpPort = config().containsKey("http.port") ? config().getInteger("http.port") : DEFAULT_PORT;
        var router = Router.router(vertx);
        var sjsOptions = new SockJSHandlerOptions().setHeartbeatInterval(2000);
        var sockJSHandler = SockJSHandler.create(vertx, sjsOptions);
        var options = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddressRegex("tick-address.*"))
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("tick-address.*"));
        sockJSHandler.bridge(options);

        router.route("/site")
                .handler(StaticHandler.create()
                        .setWebRoot("public")
                        .setCachingEnabled(false)
                        .setIndexPage("index.html"))
        ;
        router.mountSubRouter("/api/v1", getApiRouter());
        router.route("/tick/*")
                .handler(sockJSHandler);
        System.out.println("Web Server running on port: " + httpPort);
        return vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(httpPort)
                .doAfterSuccess(this::setServer)
                .ignoreElement();
    }

    @Override
    public Completable rxStop() {
        return server.rxClose();
    }
}
