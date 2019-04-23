package com.scb.s2bx.common.model;

import com.google.gson.reflect.TypeToken;

import java.beans.Transient;


public abstract class AbstractDataEntity<T> implements DataEntity<T> {

    private transient TypeToken<T> typeToken;

    protected AbstractDataEntity(TypeToken<T> typeToken) {
        this.typeToken = typeToken;
    }

    @Override
    @Transient
    public TypeToken<T> getTypeToken() {
        return typeToken;
    }

}
