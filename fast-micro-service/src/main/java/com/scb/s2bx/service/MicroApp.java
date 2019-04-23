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
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class MicroApp {

    protected String profileName;
    //TODO: check if i need to expose this, usually is required for monitor services such as ClusterMembers
    protected HazelcastClusterManager clusterManager;
    private ServiceDiscovery serviceDiscovery;

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
            //clusterManager.leave(); TODO: not sure if this is mandatory
            serviceDiscovery.close();
            boolean closeResult = vertx.rxClose().blockingAwait(3, TimeUnit.SECONDS);
            if (closeResult) {
                System.out.println("Service stopped normally");
            } else {
                System.out.println("Unable to stop in 3 s");
            }

        }));
    }

    protected Single<Vertx> getVertx() {
        var hazelcastConfig = ConfigUtil.loadConfig();
        hazelcastConfig.setInstanceName("fast-labs-" + hazelcastConfig.getGroupConfig().getName() + "-" + this.profileName);
        clusterManager = new HazelcastClusterManager(hazelcastConfig);
        return Vertx.rxClusteredVertx(new VertxOptions()
                .setClusterManager(clusterManager)
                .setEventBusOptions(new EventBusOptions()
                        .setClustered(true))
                .setHAEnabled(true));
    }

    protected abstract List<Function<Vertx, Observable<String>>> getModuleFactories();


    protected String start(Vertx vertx) {
        serviceDiscovery = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions()
                .setName(profileName));

        vertx.registerVerticleFactory(new DiscoServiceFactory(serviceDiscovery));
        init(vertx);
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

    protected abstract void init(Vertx vertx);

}
