package org.flabs.common.service;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.vertx.reactivex.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class ServiceListParamRequest<Q, T> extends AbstractAsyncServiceRequest<Q, T> {

    private Function<Q, Observable<T>> supplierObservable;

    private ServiceListParamRequest(Message<Q> message, Function<Q, Observable<T>> supplier) {
        super(ReturnType.LIST, message);
        this.supplierObservable = supplier;
    }

    public Completable execute() {
        final Q request = message.body();
        return Completable.create(listEmitter -> supplierObservable.apply(request)
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
