package org.flabs.web;

import com.google.gson.Gson;
import io.reactivex.Completable;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandlerOptions;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;
import io.vertx.reactivex.ext.healthchecks.HealthChecks;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;
import org.flabs.common.service.AbstractServiceVerticle;
import org.flabs.refdata.currency.service.CurrencyDataQuery;
import org.flabs.refdata.currency.service.CurrencyReferenceDataService;

import java.util.Optional;

import static io.vertx.servicediscovery.Status.*;

public class WebVerticle extends AbstractServiceVerticle {

    private static final int DEFAULT_PORT = 8080;
    private HttpServer server;

    private final Gson gson = new Gson();

    private void setServer(HttpServer server) {
        this.server = server;
    }

    private Router getApiRouter() {
        var apiRouter = Router.router(vertx);
        apiRouter.route().handler(BodyHandler.create());
        apiRouter.route().consumes("application/json");
        apiRouter.route().produces("application/json");
        apiRouter.get("/currencyPair").handler(rc -> {
            //TODO: access direct to service, discovery should be dynamic
            //TODO: understand more about release service reference
            getServiceProvider(CurrencyReferenceDataService.SERVICE_NAME)
                    .flatMap(serviceProvider -> {
                        var currencyPairSp = serviceProvider.getAs(CurrencyReferenceDataService.class);
                        if (currencyPairSp.isPresent()) {
                            return currencyPairSp.get()
                                    .getCurrencyPairs(new CurrencyDataQuery("usd"));
                                    //TODO: understand more about release service reference
                                    //.doAfterTerminate(serviceProvider::release);
                        }
                        throw new RuntimeException("Service not available exception"); //TODO: start thinking about exception hierarchy
                    })
                    .subscribe(currencies -> {
                                var response = rc.response();
                                response.end(gson.toJson(currencies));
                            }
                            , err -> {
                                System.out.println(err);
                                rc.response().setStatusCode(500).setStatusMessage("Application error " + err.getMessage()).end();
                            }
                    );
        });

        return apiRouter;
    }

    private Router registerHealthEndpoint(Router router) {
        var healthChecks = HealthChecks.create(vertx);
        //TODO: access direct to service discovery should be dynamic

        healthChecks.register(CurrencyReferenceDataService.SERVICE_NAME, fut -> {
            getServiceRecord(CurrencyReferenceDataService.SERVICE_NAME)
                    .subscribe(r -> {
                        System.out.println("Extract health for reference data");
                        if (r.getStatus() == UP) {
                            fut.complete(Status.OK());
                        } else {
                            fut.complete(Status.KO());
                        }
                    });
        });
        var healthCheckHandler = HealthCheckHandler.createWithHealthChecks(healthChecks);
        router.get("/health*").handler(healthCheckHandler);
        return router;
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

        router.mountSubRouter("/api/v1", getApiRouter());
        router.route("/tick/*")
                .handler(sockJSHandler);

        registerHealthEndpoint(router);

        router.route("/*")
                .handler(StaticHandler.create()
                        .setWebRoot("public")
                        .setCachingEnabled(false)
                        .setIndexPage("index.html"));

        System.out.println("Web Server running on port: " + httpPort);
        return vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(httpPort)
                .doAfterSuccess(this::setServer)
                .ignoreElement();
    }

    @Override
    public Completable rxStop() {
        System.out.println("Stop the web :)");
        return server.rxClose();
    }
}
