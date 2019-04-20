package org.flabs.refdata;

import org.flabs.common.codec.ValueClassCodec;
import org.flabs.refdata.currency.model.Currency;
import org.flabs.refdata.currency.model.CurrencyPair;

import java.lang.reflect.Type;

public enum RefDataCodec implements ValueClassCodec {
    CURRENCY_CODEC(Currency.class),
    CURRENCY_PAIR_CODEC(CurrencyPair.class),
    LIST_CURRENCY_PAIR_CODEC(CurrencyPair.class) ;


    private final ValueClassCodec<?> delegate;

    RefDataCodec(Class<?> clz) {
        if (name().startsWith("LIST")) {
            this.delegate = ValueClassCodec.newListCodec(clz);
        } else {
            this.delegate = ValueClassCodec.newCodec(clz);
        }

    }

    @Override
    public Type getType() {
        return delegate.getType();
    }
}
