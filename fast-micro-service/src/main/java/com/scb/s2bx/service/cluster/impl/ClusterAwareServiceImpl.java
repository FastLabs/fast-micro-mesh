package com.scb.s2bx.service.cluster.impl;

import com.hazelcast.core.Member;
import io.reactivex.Single;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import com.scb.s2bx.service.cluster.ClusterAwareService;
import com.scb.s2bx.service.cluster.ClusterMember;

import java.util.List;
import java.util.stream.Collectors;

public class ClusterAwareServiceImpl implements ClusterAwareService {

    private final HazelcastClusterManager clusterManager;

    public ClusterAwareServiceImpl(HazelcastClusterManager clusterManager) {
        this.clusterManager = clusterManager;
    }

    private ClusterMember toClusterMember(Member member) {
        return new ClusterMember(member.getUuid());
    }

    @Override
    public Single<List<ClusterMember>> getClusterMembers() {
        var members = clusterManager.getHazelcastInstance().getCluster().getMembers();
        var clusterMembers = members.stream().map(this::toClusterMember).collect(Collectors.toList());
        return Single.just(clusterMembers);

    }
}
