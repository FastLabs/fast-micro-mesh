package org.flabs.refdata.currency.service;

import com.google.gson.reflect.TypeToken;
import io.netty.handler.codec.MessageAggregationException;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.eventbus.DeliveryOptions;

import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.servicediscovery.Record;
import org.flabs.common.model.DataEntity;
import org.flabs.common.service.AbstractServiceVerticle;
import org.flabs.refdata.RefDataCodec;
import org.flabs.refdata.currency.model.CurrencyPair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ReferenceDataVerticle extends AbstractServiceVerticle {

    class CurrencyProvider {
        public Observable<CurrencyPair> getAllCurrencyPairs() {
            return Observable.fromIterable(Arrays.asList(new CurrencyPair("USD", "EUR")));
        }
    }


    @Override
    public Completable rxStop() {
        return unregisterService();
    }

    class NoParamServiceRequest<T extends DataEntity> {
        Message<?> msg;
        Supplier<Single<T>> supplierSingle;
        Supplier<Observable<T>> supplierList;
        Class<T> clzz;


        public NoParamServiceRequest(Message<?> msg, Supplier<Single<T>> supplier) {
            this.msg = msg;
            this.supplierSingle = supplier;

        }

        public NoParamServiceRequest(Message<?> msg, Supplier<Observable<T>> supplier, Class<T> clzz) {
            this.msg = msg;
            this.supplierList = supplier;
            this.clzz = clzz;
        }


        private <T extends DataEntity> void serviceReply(Message<?> msg, T data) {
            msg.reply(data, new DeliveryOptions().setCodecName(data.getMessageCodec().name()));
        }

        private <T extends DataEntity> void serviceListReply(Message<?> msg, List<T> data, Class<T> clz) {
            //var typeToken = TypeToken.getParameterized(ArrayList.class, clz);
            msg.reply(data, new DeliveryOptions().setCodecName(RefDataCodec.LIST_CURRENCY_PAIR_CODEC.name()));
        }
//TODO: exception
        public void execute() {
            final Object body = msg.body();
            if(body != null ) {
                System.out.println("Params Detected will ignore them. Calling the service");
            }
            if (supplierList != null) {
                supplierList.get()
                        .toList()
                        .subscribe(tt -> serviceListReply(msg, tt, clzz)
                                , err -> {
                                    System.out.println("Error extracting the list : ");
                                });

            }
            if (supplierSingle != null) {
                supplierSingle.get()
                        .subscribe(tt -> {
                            serviceReply(msg, tt);
                        });
            }
        }
    }



   /* class ServiceRequest<Q extends DataEntity, T extends DataEntity> {
        Message<Q> msg;
        Supplier<Single<T>> supplier;
        Function<Q, Single<T>> funct;

        public ServiceRequest(Message<Q> msg, Supplier<Single<T>> supplier) {
            this.msg = msg;
            this.supplier = supplier;
        }

        public ServiceRequest(Message<Q> msg, Function<Q, Single<T>> supplier) {
            this.msg = msg;
            this.funct = supplier;
        }

        private <T extends DataEntity> void serviceReply(Message<?> msg, T data) {
            msg.reply(data, new DeliveryOptions().setCodecName(data.getTypeToken().getType().getTypeName()));
        }

        private <T extends DataEntity> void serviceListReply(Message<?> msg, List<T> data, Class<T> clz) {
            var typeToken = TypeToken.getParameterized(ArrayList.class, clz);
            msg.reply(data, new DeliveryOptions().setCodecName(typeToken.getType().getTypeName()));
        }

        public void execute() {
            Q body = msg.body();
            if(body == null) {
                T t = supplier.get();
                serviceReply(msg, t);
            }
        }
    }*/

    @Override
    public Completable rxStart() {
        final Record serviceRecord = CurrencyReferenceDataService.createRecord();
        var currencyProvider = new CurrencyProvider();
        var eventBus = vertx.eventBus();
        eventBus.consumer(CurrencyReferenceDataService.PROVIDER_ADDRESS)
                .toObservable()
                .map(msg -> {
                    return new NoParamServiceRequest(msg, currencyProvider::getAllCurrencyPairs, CurrencyPair.class);
                }).subscribe(xx -> {
            xx.execute();
        });
                /*.subscribe(msg -> {
                            currencyProvider.getAllCurrencyPairs().toList()
                                    .subscribe(result -> {
                                        msg.reply(result, new DeliveryOptions().setCodecName(RefDataCodec.LIST_CURRENCY_PAIR_CODEC.name()));
                                    }, System.err::println);


                        }
                        , System.err::println);*/


        return registerService(serviceRecord)
                .ignoreElement();
    }
}
