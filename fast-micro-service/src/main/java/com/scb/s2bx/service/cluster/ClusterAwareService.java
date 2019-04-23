package org.flabs.service.cluster;

import io.reactivex.Single;

import java.util.List;

public interface ClusterAwareService {

    Single<List<ClusterMember>> getClusterMembers();


}
