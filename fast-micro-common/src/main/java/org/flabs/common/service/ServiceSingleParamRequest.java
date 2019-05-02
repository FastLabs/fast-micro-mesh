package org.flabs.common.service;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.reactivex.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

@Slf4j
public class ServiceSingleParamRequest<Q, R> extends AbstractAsyncServiceRequest<Q, R> {

    private Function<Q, Single<R>> singleProvider;

    protected ServiceSingleParamRequest(Message<Q> message, MessageCodec<R, R> codec) {
        super(ReturnType.SINGLE, message, codec);
    }



    @Override
    void validate() {
        if(log.isDebugEnabled()) {
            if(!isParametrized()) {
                log.debug("Parameters are not present in the message. This may be an issue when calling the service");
            }
        }
    }

    @Override
    public Completable execute() {
        final Q query = message.body();
        return Completable.create(emitter -> singleProvider.apply(query)
                .subscribe(resultObserver(emitter)));
    }
}
