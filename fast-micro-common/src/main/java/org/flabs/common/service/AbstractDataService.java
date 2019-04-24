package org.flabs.common.service;

import io.reactivex.Single;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.Message;
import org.flabs.common.model.DataEntity;

import java.util.List;

public abstract class AbstractDataService {
    protected EventBus eventBus;

    public AbstractDataService(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    private DeliveryOptions deliveryOptions(MessageCodec<?, ?> t) {
        return new DeliveryOptions().setCodecName(t.name());
    }

    protected <T> Single<T> getEntity(String address) {
        return eventBus.<T>rxSend(address, null)
                .map(Message::body);
    }

    protected <Q extends DataEntity, R> Single<R> queryEntity(String address, Q query) {
        return eventBus.<R>rxSend(address, query, deliveryOptions(query.getMessageCodec())).map(Message::body);
    }

    protected <T extends DataEntity> Single<List<T>> getList(String address) {
        return eventBus.<List<T>>rxSend(address, null).map(Message::body);
    }

    protected <Q extends DataEntity, R> Single<List<R>> queryList(String address, Q query) {
        return eventBus.<List<R>>rxSend(address, query, deliveryOptions(query.getMessageCodec())).map(Message::body);
    }


}
