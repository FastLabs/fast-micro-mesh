package org.flabs.common.model;

import io.vertx.core.eventbus.MessageCodec;

public interface DataEntity {

    MessageCodec<?, ?> getMessageCodec();
}
