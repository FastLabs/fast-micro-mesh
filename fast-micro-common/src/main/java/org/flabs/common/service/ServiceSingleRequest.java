package org.flabs.common.service;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.reactivex.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class ServiceSingleRequest<T> extends AbstractAsyncServiceRequest<Object, T> {

    private Supplier<Single<T>> supplierSingle;


    public ServiceSingleRequest(Message<Object> message, Supplier<Single<T>> supplier) {
        super(ReturnType.SINGLE, message);
        this.supplierSingle = supplier;
    }
    @Override
    public Completable execute() {
        return Completable.create(emitter -> supplierSingle.get()
                .subscribe(resultObserver(emitter)));
    }

    @Override
    void validate() {
        if (isParametrized()) {
            if (log.isDebugEnabled()) {
                log.debug("Parameters supplied in a non parameterized service request");
            }
        }
    }
}

