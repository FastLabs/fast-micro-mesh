package org.flabs.common.service;

import io.reactivex.Completable;

public interface AsyncServiceRequest {
    enum ReturnType {
        LIST, SINGLE
    }
    Completable execute();
}
