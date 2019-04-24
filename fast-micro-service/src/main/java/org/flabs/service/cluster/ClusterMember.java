package org.flabs.service.cluster;

import lombok.Value;
import org.flabs.common.codec.ValueClassCodec;
import org.flabs.common.model.AbstractDataEntity;

@Value
public class ClusterMember extends AbstractDataEntity {
    private final String name;

    public ClusterMember(String name) {
        super(ValueClassCodec.newCodec(ClusterMember.class));
        this.name = name;
    }
}
