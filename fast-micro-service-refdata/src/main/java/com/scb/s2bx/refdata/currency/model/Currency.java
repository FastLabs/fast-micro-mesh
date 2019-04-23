package com.scb.s2bx.refdata.currency.model;

import com.google.gson.reflect.TypeToken;
import lombok.Value;
import com.scb.s2bx.common.model.AbstractDataEntity;

@Value
public class Currency extends AbstractDataEntity<Currency> {
    public static final TypeToken<Currency> typeToken = TypeToken.get(Currency.class);

    private final String code;

    public Currency(String code) {
        super(typeToken);
        this.code = code;
    }
}
