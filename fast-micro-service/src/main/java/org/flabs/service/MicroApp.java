package org.flabs.service;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.spi.cluster.hazelcast.ConfigUtil;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.flabs.common.service.DiscoServiceFactory;

import java.util.List;
import java.util.function.Function;

public abstract class MicroApp {

    protected String profileName;
    //TODO: check if i need to expose this, usually is required for monitor services such as ClusterMembers
    protected HazelcastClusterManager clusterManager;

    public MicroApp(String profileName) {
        this.profileName = profileName;
        getVertx().
                doOnSuccess(this::shutDownHook)
                .map(this::start)
                .subscribe(res -> {
                }, err -> {
                    System.err.println("Error when starting the service " + err + ". Will shut down");
                    System.exit(-1);
                });
    }


    private void shutDownHook(Vertx vertx) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down the system");
            //TODO: check if this is blocking, if not it may be the case that the logs will never be displayed
            vertx.rxClose()
                    .subscribe(() -> {
                        System.out.println("Vertx stopped successfully");
                    }, err -> {
                        System.out.println("Error exiting vertx gracefully: " + err);
                    });
        }));
    }

    protected Single<Vertx> getVertx() {
        var hazelcastConfig = ConfigUtil.loadConfig();
        hazelcastConfig.setInstanceName("fast-labs-" + hazelcastConfig.getGroupConfig().getName()+ "-" + this.profileName);
        clusterManager = new HazelcastClusterManager(hazelcastConfig);
        return Vertx.rxClusteredVertx(new VertxOptions()
                .setClusterManager(clusterManager)
                .setEventBusOptions(new EventBusOptions()
                        .setClustered(true))
                .setHAEnabled(true));
    }

    protected abstract List<Function<Vertx, Observable<String>>> getModuleFactories();


    protected String start(Vertx vertx) {
        var serviceDiscovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions()
                .setName(profileName));

        vertx.registerVerticleFactory(new DiscoServiceFactory(serviceDiscovery));

        Observable.fromIterable(getModuleFactories())

                .flatMap(factory -> factory.apply(vertx))
                .subscribe(verticleId -> {
                            System.out.println("Component " + verticleId + " deployed");
                        }, err -> {
                            System.err.println("Error when starting component: " + err);
                        },
                        () -> {
                            System.out.println("ALl components started");
                        });
        return "Server started";
    }

}
