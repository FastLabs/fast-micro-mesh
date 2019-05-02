package org.flabs.common.service;

import io.reactivex.CompletableEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonArray;
import io.vertx.reactivex.core.eventbus.Message;
import lombok.extern.slf4j.Slf4j;
import org.flabs.common.model.DataEntity;

import java.util.List;

@Slf4j
public abstract class AbstractAsyncServiceRequest<Q, T> implements AsyncServiceRequest {

    MessageCodec<T, T> codec;
    Message<Q> message;

    private AsyncServiceRequest.ReturnType returnType;

    AbstractAsyncServiceRequest(AsyncServiceRequest.ReturnType returnType, Message<Q> message, MessageCodec<T, T> codec) {
        this.codec = codec;
        this.message = message;
        this.returnType = returnType;
    }

    AbstractAsyncServiceRequest(AsyncServiceRequest.ReturnType returnType, Message<Q> message) {
        this(returnType, message, null);
    }

    DeliveryOptions deliveryOptions(MessageCodec codec) {
        return new DeliveryOptions().setCodecName(codec.name());
    }


    //TODO: think of better dispatch
    private void reply(Object data) {
        if (data instanceof DataEntity) {
            message.reply(data, deliveryOptions(((DataEntity) data).getMessageCodec()));
        } else if (codec != null) {
            message.reply(data, deliveryOptions(codec));
        } else if(data instanceof List) {
            message.reply(new JsonArray(((List)data)));
        }else  {
            message.reply(data);
        }
    }

    //TODO: check the failure code and create the message hierarchy
    private Throwable errorReply(Throwable err) {
        message.fail(-1, err.getMessage());
        return err;
    }

    <X> SingleObserver<X> resultObserver(CompletableEmitter emitter) {
        return new SingleObserver<>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(X x) {
                reply(x);
                emitter.onComplete();
            }

            @Override
            public void onError(Throwable e) {
                emitter.onError(errorReply(e));
            }
        };
    }

    //abstract Completable replyList();

    //abstract Completable replySingle();

    abstract void validate();

    boolean isParametrized() {
        return message.body() != null;
    }

    /*@Override
    public Completable execute() {

        switch (returnType) {
            case LIST:
                return replyList();
            case SINGLE:
                return replySingle();
            default:
                return Completable.error(new RuntimeException("Unsupported Service request Operation"));
        }


    }*/
}
