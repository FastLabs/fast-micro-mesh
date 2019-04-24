package org.flabs.common.model;

import io.vertx.core.eventbus.MessageCodec;


public abstract class AbstractDataEntity implements DataEntity {

    private transient MessageCodec<?, ?> messageCodec;

    protected AbstractDataEntity(MessageCodec<?, ?> codec) {
        this.messageCodec = codec;
    }

    @Override
    public MessageCodec getMessageCodec() {
        return messageCodec;
    }
}
