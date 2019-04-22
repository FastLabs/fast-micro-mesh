package org.flabs.streamer;

import io.reactivex.Observable;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import org.flabs.refdata.RefDataCodec;
import org.flabs.service.MicroApp;
import org.flabs.service.cluster.ClusterMembers;
import org.flabs.web.WebVerticle;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Main extends MicroApp {

    private final int httpPort;


    private Main(String profile, int httpPort) {
        super(profile);
        this.httpPort = httpPort;
    }

    //TODO: understand more about rxjava2, example: disposable

    public static void main(String... args) {
        var profile = "consumer";
        var port = 8080;
        if (args.length == 1) {
            profile = args[0];
        }

        if (args.length == 2) {
            port = Integer.parseInt(args[1]);
        }
        //TODO: look into instantiation and configuration
        var main = new Main(profile, port);
    }

    Observable<String> deployWeb(Vertx vertx) {
        return vertx.rxDeployVerticle("service:org.flabs.web.WebVerticle",
                new DeploymentOptions()
                        .setConfig(new JsonObject()
                                .put("http.port", this.httpPort)))
                .toObservable();
    }

    Observable<String> deployTestStreaming(Vertx vertx) {
        return vertx.rxDeployVerticle("org.flabs.service.example.StreamingVerticle",
                new DeploymentOptions()
                        .setHa(true)).toObservable();
    }

    Observable<String> deployClusterMembersService(Vertx vertx) {
        return vertx.rxDeployVerticle(new ClusterMembers(this.clusterManager)).toObservable();
    }

    Observable<String> deployProducer(Vertx vertx) {
        return vertx.rxDeployVerticle("service:org.flabs.service.example.Producer", new DeploymentOptions().setHa(true)).toObservable();
    }

    Observable<String> deployConsumer(Vertx vertx) {
        return vertx.rxDeployVerticle("service:org.flabs.service.example.Consumer", new DeploymentOptions().setHa(true)).toObservable();
    }

    Observable<String> deployReferenceDataService(Vertx vertx) {
        return vertx.rxDeployVerticle("service:org.flabs.refdata.currency.service.ReferenceDataVerticle", new DeploymentOptions().setHa(true)).toObservable();
    }


    @Override
    protected List<Function<Vertx, Observable<String>>> getModuleFactories() {
        final List<Function<Vertx, Observable<String>>> moduleFactories = new ArrayList<>();

        switch (profileName) {
            case "producer":
                moduleFactories.add(this::deployClusterMembersService);
                moduleFactories.add(this::deployWeb);
                moduleFactories.add(this::deployProducer);
                break;
            case "consumer":
                moduleFactories.add(this::deployConsumer);
                moduleFactories.add(this::deployClusterMembersService);
                break;
            case "ref-data-service":
                moduleFactories.add(this::deployReferenceDataService);
                break;
            case "ref-data-consumer":
                moduleFactories.add(this::deployWeb);
                break;
            default:
                System.out.print("Unknown profile selected");
        }
        return moduleFactories;
    }

    @Override
    protected void init(Vertx vertx) {
        //TODO: make this configurable
        System.out.println("Listening for service events");
        vertx.eventBus().consumer("vertx.discovery.announce")
                .toObservable()
                .subscribe(xx -> {
                    System.out.println("Discovery event: " + xx.body());
                });

        System.out.println("Initializes codecs");
        vertx.eventBus().registerCodec(RefDataCodec.CURRENCY_CODEC)
                .registerCodec(RefDataCodec.CURRENCY_PAIR_CODEC)
                .registerCodec(RefDataCodec.LIST_CURRENCY_PAIR_CODEC);
    }


}
