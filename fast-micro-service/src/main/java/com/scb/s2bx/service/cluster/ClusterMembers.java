package com.scb.s2bx.service.cluster;

import io.reactivex.Completable;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.NodeListener;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import com.scb.s2bx.common.service.AbstractServiceVerticle;

public class ClusterMembers extends AbstractServiceVerticle {
    private final HazelcastClusterManager clusterManager;
    private static final String CLUSTER_NODE_INFO = "flabs.cluster.info";

    public ClusterMembers(HazelcastClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }




    @Override
    public Completable rxStart() {
        //TODO: think how to expose the cluster information
        //TODO: exception handling


        var hazelcastInstance = clusterManager.getHazelcastInstance();
        clusterManager.nodeListener(new NodeListener() {
            @Override
            public void nodeAdded(String nodeID) {
                System.out.println("---> New node" + nodeID);
                vertx.eventBus().publish(CLUSTER_NODE_INFO, new JsonObject().put("nodeId", nodeID).put("status", "available"));
            }

            @Override
            public void nodeLeft(String nodeID) {
                System.out.println("---> Node Removed" + nodeID);
                vertx.eventBus().publish(CLUSTER_NODE_INFO, new JsonObject().put("nodeId", nodeID).put("status", "disconnected"));
            }
        });

        System.out.println( "Custer is monitored: " + hazelcastInstance.getName());
        vertx.periodicStream(4000)
                .toObservable()
                .subscribe(timerId -> {

                    var members = hazelcastInstance.getCluster().getMembers();
                    members.forEach(member -> {
                        System.out.println(member.getAttributes());
                    });

                });
        return Completable.complete();
    }
}
