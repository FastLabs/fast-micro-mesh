package org.flabs.service.cluster;

import com.google.gson.reflect.TypeToken;
import lombok.Value;
import org.flabs.common.model.AbstractDataEntity;

@Value
public class ClusterMember extends AbstractDataEntity<ClusterMember> {
    public static final TypeToken<ClusterMember> typeToken = TypeToken.get(ClusterMember.class);
    private final String name;

    public ClusterMember(String name) {
        super(typeToken);
        this.name = name;
    }
}
