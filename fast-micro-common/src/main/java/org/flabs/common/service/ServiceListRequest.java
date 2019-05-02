package org.flabs.common.service;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.vertx.reactivex.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;
import org.flabs.common.codec.ValueClassCodec;

import java.util.function.Supplier;

@Slf4j
public class ServiceListRequest<T> extends AbstractAsyncServiceRequest< Object, T> {

    private Supplier<Observable<T>> supplierObservable;

    ServiceListRequest(Message message, Supplier<Observable<T>> supplier, ValueClassCodec<T> codec) {
        super(ReturnType.LIST, message);
        this.supplierObservable = supplier;
        this.codec = codec;
    }

    public Completable execute() {
        return Completable.create(listEmitter -> supplierObservable.get()
                .toList()
                .subscribe(resultObserver(listEmitter)));
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
